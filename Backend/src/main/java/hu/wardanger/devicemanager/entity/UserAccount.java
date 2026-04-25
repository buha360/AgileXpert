package hu.wardanger.devicemanager.entity;

import jakarta.persistence.*;

import lombok.*;

@Entity
@Table(name = "user_account")
@Data
@AllArgsConstructor
@NoArgsConstructor
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
}