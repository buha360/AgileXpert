package hu.wardanger.devicemanager.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "smart_application")
public class SmartApplication {

    @Id
    private String id;

    private String name;

    private String type;

    private String launchMessage;

    public SmartApplication() {}

    public SmartApplication(String id, String name, String type, String launchMessage) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.launchMessage = launchMessage;
    }
}