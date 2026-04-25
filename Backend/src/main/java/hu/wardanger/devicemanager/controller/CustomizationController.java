package hu.wardanger.devicemanager.controller;

import hu.wardanger.devicemanager.dto.request.SelectThemeRequest;
import hu.wardanger.devicemanager.dto.request.SelectWallpaperRequest;
import hu.wardanger.devicemanager.dto.response.ThemeResponse;
import hu.wardanger.devicemanager.dto.response.WallpaperResponse;
import hu.wardanger.devicemanager.entity.Theme;
import hu.wardanger.devicemanager.entity.Wallpaper;
import hu.wardanger.devicemanager.mapper.CustomizationMapper;
import hu.wardanger.devicemanager.service.CustomizationService;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import lombok.AllArgsConstructor;
import lombok.Data;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@Tag(name = "Customization", description = "Endpoints for wallpapers and themes")
@RestController
@Data
@AllArgsConstructor
public class CustomizationController {

    private final CustomizationService customizationService;
    private final CustomizationMapper customizationMapper;

    @Operation(
            summary = "List all wallpapers",
            description = "Returns all available wallpapers."
    )
    @ApiResponse(responseCode = "200", description = "Wallpapers returned successfully")
    @GetMapping("/api/customization/wallpapers")
    public List<WallpaperResponse> getAllWallpapers() {
        List<Wallpaper> wallpapers = customizationService.findAllWallpapers();
        return customizationMapper.toWallpaperResponseList(wallpapers);
    }

    @Operation(
            summary = "List all themes",
            description = "Returns all available themes."
    )
    @ApiResponse(responseCode = "200", description = "Themes returned successfully")
    @GetMapping("/api/customization/themes")
    public List<ThemeResponse> getAllThemes() {
        List<Theme> themes = customizationService.findAllThemes();
        return customizationMapper.toThemeResponseList(themes);
    }

    @Operation(
            summary = "Select wallpaper for user",
            description = "Sets the selected wallpaper for the given user."
    )
    @ApiResponse(responseCode = "204", description = "Wallpaper updated successfully")
    @PutMapping("/api/users/{userId}/wallpaper")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void selectWallpaper(@PathVariable String userId,
                                @RequestBody SelectWallpaperRequest request) {
        customizationService.setWallpaperForUser(userId, request.getWallpaperId());
    }

    @Operation(
            summary = "Select theme for user",
            description = "Sets the selected theme for the given user."
    )
    @ApiResponse(responseCode = "204", description = "Theme updated successfully")
    @PutMapping("/api/users/{userId}/theme")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void selectTheme(@PathVariable String userId,
                            @RequestBody SelectThemeRequest request) {
        customizationService.setThemeForUser(userId, request.getThemeId());
    }
}