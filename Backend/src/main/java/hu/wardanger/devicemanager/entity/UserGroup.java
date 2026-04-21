package hu.wardanger.devicemanager.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "user_group")
public class UserGroup {

    @Id
    private String id;

    private String name;

    private String accessCode;

    @OneToMany(mappedBy = "group")
    private List<UserAccount> users = new ArrayList<>();

    public UserGroup() {}

    public UserGroup(String id, String name, String accessCode) {
        this.id = id;
        this.name = name;
        this.accessCode = accessCode;
    }
}