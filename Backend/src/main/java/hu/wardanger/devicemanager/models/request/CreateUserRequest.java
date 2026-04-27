package hu.wardanger.devicemanager.dto.request;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateUserRequest {
    private String name;
    private String password;
}