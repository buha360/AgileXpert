package hu.wardanger.devicemanager.mapper;

import hu.wardanger.devicemanager.dto.response.ThemeResponse;
import hu.wardanger.devicemanager.dto.response.WallpaperResponse;
import hu.wardanger.devicemanager.entity.Theme;
import hu.wardanger.devicemanager.entity.Wallpaper;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CustomizationMapper {
    WallpaperResponse toWallpaperResponse(Wallpaper wallpaper);

    List<WallpaperResponse> toWallpaperResponseList(List<Wallpaper> wallpapers);

    ThemeResponse toThemeResponse(Theme theme);

    List<ThemeResponse> toThemeResponseList(List<Theme> themes);
}