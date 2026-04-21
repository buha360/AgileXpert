package hu.wardanger.devicemanager.service;

import hu.wardanger.devicemanager.entity.Menu;
import hu.wardanger.devicemanager.entity.UserAccount;
import hu.wardanger.devicemanager.entity.UserGroup;
import hu.wardanger.devicemanager.entity.UserRole;
import hu.wardanger.devicemanager.repository.MenuRepository;
import hu.wardanger.devicemanager.repository.UserAccountRepository;
import hu.wardanger.devicemanager.repository.UserGroupRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class GroupService {

    private final UserGroupRepository userGroupRepository;
    private final UserAccountRepository userAccountRepository;
    private final MenuRepository menuRepository;

    public GroupService(UserGroupRepository userGroupRepository,
                        UserAccountRepository userAccountRepository,
                        MenuRepository menuRepository) {
        this.userGroupRepository = userGroupRepository;
        this.userAccountRepository = userAccountRepository;
        this.menuRepository = menuRepository;
    }

    public List<UserGroup> findAllGroups() {
        return userGroupRepository.findAll();
    }

    @Transactional
    public UserGroup registerGroup(String groupName, String accessCode, String adminUserName) {
        String groupId = UUID.randomUUID().toString();
        String menuId = UUID.randomUUID().toString();
        String adminId = UUID.randomUUID().toString();

        UserGroup userGroup = new UserGroup(groupId, groupName, accessCode);
        userGroupRepository.save(userGroup);

        Menu rootMenu = new Menu(menuId, adminUserName + " főmenüje");
        menuRepository.save(rootMenu);

        UserAccount adminUser = new UserAccount(adminId, adminUserName, UserRole.ADMIN);
        adminUser.setGroup(userGroup);
        adminUser.setRootMenu(rootMenu);

        userAccountRepository.save(adminUser);

        return userGroup;
    }

    public UserGroup findGroupById(String id) {
        return userGroupRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Nincs ilyen group."));
    }

    public boolean validateAccessCode(String groupId, String accessCode) {
        UserGroup group = findGroupById(groupId);
        return group.getAccessCode().equals(accessCode);
    }
}