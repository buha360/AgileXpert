package hu.wardanger.devicemanager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hu.wardanger.devicemanager.entity.UserGroup;
import hu.wardanger.devicemanager.generated.model.CreateGroupRequest;
import hu.wardanger.devicemanager.generated.model.GroupSummaryResponse;
import hu.wardanger.devicemanager.generated.model.ValidateGroupAccessRequest;
import hu.wardanger.devicemanager.mapper.GroupMapper;
import hu.wardanger.devicemanager.service.GroupService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GroupController.class)
class GroupControllerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GroupService groupService;

    @MockitoBean
    private GroupMapper groupMapper;

    @Test
    @DisplayName("GET /api/groups - returns all groups")
    void getAllGroups_shouldReturnGroups() throws Exception {
        UserGroup group = new UserGroup();
        group.setId("group-1");
        group.setName("Buha Family");

        GroupSummaryResponse response = new GroupSummaryResponse();
        response.setId("group-1");
        response.setName("Buha Family");

        when(groupService.findAllGroups()).thenReturn(List.of(group));
        when(groupMapper.toResponseList(List.of(group))).thenReturn(List.of(response));

        mockMvc.perform(get("/api/groups"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("group-1"))
                .andExpect(jsonPath("$[0].name").value("Buha Family"));
    }

    @Test
    @DisplayName("POST /api/groups - creates a new group")
    void createGroup_shouldReturnCreatedGroup() throws Exception {
        CreateGroupRequest request = new CreateGroupRequest();
        request.setGroupName("Test Family");
        request.setAccessCode("asd");
        request.setAdminUserName("Buha");
        request.setAdminPassword("1234");

        UserGroup createdGroup = new UserGroup();
        createdGroup.setId("group-1");
        createdGroup.setName("Test Family");

        GroupSummaryResponse response = new GroupSummaryResponse();
        response.setId("group-1");
        response.setName("Test Family");

        when(groupService.registerGroup("Test Family", "asd", "Buha", "1234"))
                .thenReturn(createdGroup);
        when(groupMapper.toResponse(createdGroup))
                .thenReturn(response);

        mockMvc.perform(post("/api/groups")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("group-1"))
                .andExpect(jsonPath("$.name").value("Test Family"));
    }

    @Test
    @DisplayName("POST /api/groups/{groupId}/access-validation - returns valid true")
    void validateAccessCode_shouldReturnValidTrue() throws Exception {
        ValidateGroupAccessRequest request = new ValidateGroupAccessRequest();
        request.setAccessCode("asd");

        when(groupService.validateAccessCode("group-1", "asd"))
                .thenReturn(true);

        mockMvc.perform(post("/api/groups/group-1/access-validation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(true))
                .andExpect(jsonPath("$.message").value("Access code is valid."));
    }

    @Test
    @DisplayName("POST /api/groups/{groupId}/access-validation - returns valid false")
    void validateAccessCode_shouldReturnValidFalse() throws Exception {
        ValidateGroupAccessRequest request = new ValidateGroupAccessRequest();
        request.setAccessCode("wrong");

        when(groupService.validateAccessCode("group-1", "wrong"))
                .thenReturn(false);

        mockMvc.perform(post("/api/groups/group-1/access-validation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(false))
                .andExpect(jsonPath("$.message").value("Invalid access code."));
    }
}