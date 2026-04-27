package hu.wardanger.devicemanager.models.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Response returned after launching an application")
public class LaunchResponse {

    @Schema(description = "Launch result message", example = "Alkalmazás elindítva: Paint")
    private String message;
}