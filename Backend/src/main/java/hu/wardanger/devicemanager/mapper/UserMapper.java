package hu.wardanger.devicemanager.mapper;

import hu.wardanger.devicemanager.models.response.UserSummaryResponse;
import hu.wardanger.devicemanager.entity.UserAccount;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "role", expression = "java(userAccount.getRole().name())")
    UserSummaryResponse toResponse(UserAccount userAccount);

    List<UserSummaryResponse> toResponseList(List<UserAccount> users);
}