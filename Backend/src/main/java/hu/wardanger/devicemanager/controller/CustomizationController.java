package hu.wardanger.devicemanager.controller;

import hu.wardanger.devicemanager.entity.Theme;
import hu.wardanger.devicemanager.entity.Wallpaper;
import hu.wardanger.devicemanager.generated.api.CustomizationApi;
import hu.wardanger.devicemanager.generated.model.SelectThemeRequest;
import hu.wardanger.devicemanager.generated.model.SelectWallpaperRequest;
import hu.wardanger.devicemanager.generated.model.ThemeResponse;
import hu.wardanger.devicemanager.generated.model.WallpaperResponse;
import hu.wardanger.devicemanager.mapper.CustomizationMapper;
import hu.wardanger.devicemanager.service.CustomizationService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Data
public class CustomizationController implements CustomizationApi {

    private final CustomizationService customizationService;
    private final CustomizationMapper customizationMapper;

    @Override
    public ResponseEntity<List<WallpaperResponse>> getAllWallpapers() {
        List<Wallpaper> wallpapers = customizationService.findAllWallpapers();

        List<WallpaperResponse> response = customizationMapper.toWallpaperResponseList(wallpapers);

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<List<ThemeResponse>> getAllThemes() {
        List<Theme> themes = customizationService.findAllThemes();

        List<ThemeResponse> response = customizationMapper.toThemeResponseList(themes);

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Void> selectWallpaper(String userId, SelectWallpaperRequest request) {
        customizationService.setWallpaperForUser(userId, request.getWallpaperId());

        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Void> selectTheme(String userId, SelectThemeRequest request) {
        customizationService.setThemeForUser(userId, request.getThemeId());

        return ResponseEntity.noContent().build();
    }
}