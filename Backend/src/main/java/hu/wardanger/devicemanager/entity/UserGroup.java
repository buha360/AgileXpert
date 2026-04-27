package hu.wardanger.devicemanager.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "user_group")
@Data
@NoArgsConstructor
public class UserGroup {

    @Id
    private String id;

    private String name;

    private String accessCode;

    @OneToMany(mappedBy = "group")
    private List<UserAccount> users = new ArrayList<>();

    public UserGroup(String id, String name, String accessCode) {
        this.id = id;
        this.name = name;
        this.accessCode = accessCode;
    }
}