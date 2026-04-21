package hu.wardanger.devicemanager.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

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

    public UserAccount(String id, String name, UserRole role) {
        this.id = id;
        this.name = name;
        this.role = role;
    }
}