package hu.wardanger.devicemanager.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "theme")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Theme {

    @Id
    private String id;

    private String name;
}