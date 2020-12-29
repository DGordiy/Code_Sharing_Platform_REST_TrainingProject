package platform;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/api/code")
public class CodeSharingRestController {
    @Autowired
    CodeEntityRepository codeEntityRepository;

    @GetMapping(value = "/{id}")
    public CodeEntity getCode(@PathVariable String id) throws ResponseStatusException {
        CodeEntity codeEntity = codeEntityRepository.findById(id).orElse(null);
        if (codeEntity == null
            || codeEntity.isSecret()
            && (codeEntity.getInitialTime() > 0 && codeEntity.getTime() == 0
                || codeEntity.getInitialViews() > 0 && codeEntity.getViews() == 0)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Code not found");
        }
        //System.out.println(codeEntity.getId() + " " + codeEntity.getInitialViews());
        if (codeEntity.isSecret() && codeEntity.getViews() > 0) {
            codeEntity.setViews(codeEntity.getViews() - 1);
            codeEntityRepository.save(codeEntity);
        }

        return codeEntity;
    }

    @GetMapping(value = "/latest")
    public List<CodeEntity> getLatest() {
        return codeEntityRepository.findTop10BySecretOrderByDateDesc(false);
    }

    @PostMapping(value = "/new")
    public Map<String, String> createCode(@RequestBody CodeEntity codeEntity) {
        String id = codeEntityRepository.save(codeEntity).getId();
        //System.out.println(id + "  " + codeEntity.getInitialTime() + " " + codeEntity.getInitialViews());
        Map<String, String> result = Map.of("id", id);
        return result;
    }

}
