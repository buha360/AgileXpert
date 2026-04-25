package hu.wardanger.devicemanager.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "menu_item")
public class MenuItem {

    @Id
    private String id;

    private String name;

    private Integer positionIndex;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id")
    private Menu menu;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id")
    private SmartApplication application;

    public MenuItem() {}

    public MenuItem(String id, String name, Integer positionIndex) {
        this.id = id;
        this.name = name;
        this.positionIndex = positionIndex;
    }
}