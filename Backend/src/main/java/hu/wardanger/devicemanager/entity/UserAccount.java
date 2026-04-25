package hu.wardanger.devicemanager.entity;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "user_account")
public class UserAccount {

    @Id
    private String id;

    private String name;

    @Column(name = "user_password")
    private String password;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private UserGroup group;

    @OneToOne
    @JoinColumn(name = "root_menu_id")
    private Menu rootMenu;

    @ManyToOne
    @JoinColumn(name = "wallpaper_id")
    private Wallpaper wallpaper;

    @ManyToOne
    @JoinColumn(name = "theme_id")
    private Theme theme;

    public UserAccount() {
    }

    public UserAccount(String id, String name, String password, UserRole role) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.role = role;
    }
}