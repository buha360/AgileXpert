package hu.wardanger.devicemanager.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "wallpaper")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Wallpaper {

    @Id
    private String id;

    private String name;
}