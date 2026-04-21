package hu.wardanger.devicemanager.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "theme")
public class Theme {

    @Id
    private String id;

    private String name;

    public Theme() {
    }

    public Theme(String id, String name) {
        this.id = id;
        this.name = name;
    }

}