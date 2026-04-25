package hu.wardanger.devicemanager.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "wallpaper")
public class Wallpaper {

    @Id
    private String id;

    private String name;

    public Wallpaper() {}

    public Wallpaper(String id, String name) {
        this.id = id;
        this.name = name;
    }
}