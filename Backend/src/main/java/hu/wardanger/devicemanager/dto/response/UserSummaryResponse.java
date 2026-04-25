package hu.wardanger.devicemanager.dto.response;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserSummaryResponse {
    private String id;
    private String name;
    private String role;
}