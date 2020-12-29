package platform;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CodeEntityRepository extends CrudRepository<CodeEntity, String> {
    List<CodeEntity> findTop10BySecretOrderByDateDesc(Boolean b);
}
