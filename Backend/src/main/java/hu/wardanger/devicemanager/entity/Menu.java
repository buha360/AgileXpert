package hu.wardanger.devicemanager.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "menu")
@Data
@NoArgsConstructor
public class Menu {

    @Id
    private String id;

    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_menu_id")
    private Menu parentMenu;

    @OneToMany(mappedBy = "parentMenu")
    private List<Menu> childMenus = new ArrayList<>();

    @OneToMany(mappedBy = "menu")
    private List<MenuItem> menuItems = new ArrayList<>();

    public Menu(String id, String name) {
        this.id = id;
        this.name = name;
    }
}