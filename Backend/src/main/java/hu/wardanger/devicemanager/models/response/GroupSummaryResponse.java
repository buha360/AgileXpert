package hu.wardanger.devicemanager.models.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Short summary of a group")
public class GroupSummaryResponse {

    @Schema(description = "Unique identifier of the group", example = "group-buha-family")
    private String id;

    @Schema(description = "Name of the group", example = "Buha Family")
    private String name;
}