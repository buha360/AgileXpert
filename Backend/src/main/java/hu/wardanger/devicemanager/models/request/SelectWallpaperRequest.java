package hu.wardanger.devicemanager.models.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Request body for selecting a wallpaper for a user")
public class SelectWallpaperRequest {

    @Schema(description = "Identifier of the selected wallpaper", example = "wallpaper-mountain", requiredMode = Schema.RequiredMode.REQUIRED)
    private String wallpaperId;
}