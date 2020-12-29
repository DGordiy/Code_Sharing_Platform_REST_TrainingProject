package platform;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
public class CodeEntity {
    private String code;

    @JsonIgnore
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
    private String id;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @JsonFormat(pattern = "yyyy/MM/dd hh:mm:ss")
    private LocalDateTime date;

    private int time;
    private int views;

    @JsonIgnore
    private int initialViews;

    @JsonIgnore
    private boolean secret;

    CodeEntity() {
        date = LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    @JsonIgnore
    public String getFormattedLoadDate() {
        return date.format(DateTimeFormatter.ofPattern("yyyy/MM/dd hh:mm:ss"));
    }

    public int getTime() {
        return Math.max(time - (LocalDateTime.now().getSecond() - date.getSecond()), 0);
    }

    @JsonIgnore
    public int getInitialTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = Math.max(time, 0);
        if (time > 0) {
            setSecret(true);
        }
    }

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = Math.max(views, 0);
        if (views > 0) {
            setSecret(true);
        }
        if (initialViews == 0) {
            initialViews = views;
        }
    }

    public int getInitialViews() {
        return initialViews;
    }

    public void setInitialViews(int initialViews) {
        this.initialViews = initialViews;
    }

    public boolean isSecret() {
        return secret;
    }

    public void setSecret(boolean secret) {
        this.secret = secret;
    }
}
