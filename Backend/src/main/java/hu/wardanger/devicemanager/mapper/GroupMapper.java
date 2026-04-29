package hu.wardanger.devicemanager.mapper;

import hu.wardanger.devicemanager.entity.UserGroup;
import hu.wardanger.devicemanager.generated.model.GroupSummaryResponse;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface GroupMapper {

    GroupSummaryResponse toResponse(UserGroup userGroup);

    List<GroupSummaryResponse> toResponseList(List<UserGroup> userGroups);
}