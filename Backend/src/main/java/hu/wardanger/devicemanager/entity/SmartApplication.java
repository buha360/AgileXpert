package hu.wardanger.devicemanager.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "smart_application")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SmartApplication {

    @Id
    private String id;

    private String name;

    private String type;

    private String launchMessage;
}