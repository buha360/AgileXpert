package hu.wardanger.devicemanager.mapper;

import hu.wardanger.devicemanager.entity.UserAccount;
import hu.wardanger.devicemanager.entity.UserRole;
import hu.wardanger.devicemanager.generated.model.UserSummaryResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "role", source = "role")
    UserSummaryResponse toResponse(UserAccount userAccount);

    List<UserSummaryResponse> toResponseList(List<UserAccount> users);

    default String map(UserRole role) {
        return role == null ? null : role.name();
    }
}