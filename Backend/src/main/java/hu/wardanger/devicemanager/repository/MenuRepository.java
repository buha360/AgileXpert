package hu.wardanger.devicemanager.repository;

import hu.wardanger.devicemanager.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuRepository extends JpaRepository<Menu, String> {
}