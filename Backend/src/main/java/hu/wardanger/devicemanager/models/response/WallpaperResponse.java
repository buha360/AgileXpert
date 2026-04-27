package hu.wardanger.devicemanager.models.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Represents an available wallpaper")
public class WallpaperResponse {

    @Schema(description = "Unique identifier of the wallpaper", example = "wallpaper-mountain")
    private String id;

    @Schema(description = "Name of the wallpaper", example = "Mountain")
    private String name;
}