package hu.wardanger.devicemanager.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;

import lombok.*;

@Entity
@Table(name = "user_group")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserGroup {

    @Id
    private String id;

    private String name;

    private String accessCode;

    @OneToMany(mappedBy = "group")
    private List<UserAccount> users = new ArrayList<>();
}