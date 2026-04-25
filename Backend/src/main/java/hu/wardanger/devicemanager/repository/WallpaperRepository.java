package hu.wardanger.devicemanager.repository;

import hu.wardanger.devicemanager.entity.Wallpaper;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WallpaperRepository extends JpaRepository<Wallpaper, String> {
}