package hu.wardanger.devicemanager.repository;

import hu.wardanger.devicemanager.entity.Theme;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ThemeRepository extends JpaRepository<Theme, String> {
}