package hu.wardanger.devicemanager.jpa;

import hu.wardanger.devicemanager.entity.Menu;
import hu.wardanger.devicemanager.entity.Theme;
import hu.wardanger.devicemanager.entity.UserAccount;
import hu.wardanger.devicemanager.entity.UserGroup;
import hu.wardanger.devicemanager.entity.UserRole;
import hu.wardanger.devicemanager.entity.Wallpaper;
import hu.wardanger.devicemanager.repository.UserAccountRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("h2")
class UserAccountRepositoryTest {

    @Autowired
    private UserAccountRepository userAccountRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    @DisplayName("findDetailedById returns user with rootMenu, wallpaper and theme loaded")
    void findDetailedById_shouldReturnUserWithDetailedRelations() {
        UserGroup group = new UserGroup();
        group.setId("group-1");
        group.setName("Buha Family");
        group.setAccessCode("asd");
        entityManager.persist(group);

        Menu rootMenu = new Menu();
        rootMenu.setId("menu-1");
        rootMenu.setName("Buha főmenüje");
        entityManager.persist(rootMenu);

        Wallpaper wallpaper = new Wallpaper();
        wallpaper.setId("wallpaper-1");
        wallpaper.setName("Mountain");
        entityManager.persist(wallpaper);

        Theme theme = new Theme();
        theme.setId("theme-1");
        theme.setName("Dark");
        entityManager.persist(theme);

        UserAccount user = new UserAccount();
        user.setId("user-1");
        user.setName("Buha");
        user.setPassword("asd");
        user.setRole(UserRole.ADMIN);
        user.setGroup(group);
        user.setRootMenu(rootMenu);
        user.setWallpaper(wallpaper);
        user.setTheme(theme);
        entityManager.persist(user);

        entityManager.flush();
        entityManager.clear();

        Optional<UserAccount> result = userAccountRepository.findDetailedById("user-1");

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo("user-1");
        assertThat(result.get().getName()).isEqualTo("Buha");
        assertThat(result.get().getRootMenu()).isNotNull();
        assertThat(result.get().getRootMenu().getName()).isEqualTo("Buha főmenüje");
        assertThat(result.get().getWallpaper()).isNotNull();
        assertThat(result.get().getWallpaper().getName()).isEqualTo("Mountain");
        assertThat(result.get().getTheme()).isNotNull();
        assertThat(result.get().getTheme().getName()).isEqualTo("Dark");
    }

    @Test
    @DisplayName("findDetailedById returns empty when user does not exist")
    void findDetailedById_shouldReturnEmptyWhenMissing() {
        Optional<UserAccount> result = userAccountRepository.findDetailedById("missing-user");

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("findByGroupId returns only users belonging to the given group")
    void findByGroupId_shouldReturnUsersOfGivenGroupOnly() {
        UserGroup firstGroup = new UserGroup();
        firstGroup.setId("group-1");
        firstGroup.setName("Buha Family");
        firstGroup.setAccessCode("asd");
        entityManager.persist(firstGroup);

        UserGroup secondGroup = new UserGroup();
        secondGroup.setId("group-2");
        secondGroup.setName("Other Family");
        secondGroup.setAccessCode("qwe");
        entityManager.persist(secondGroup);

        UserAccount firstUser = new UserAccount();
        firstUser.setId("user-1");
        firstUser.setName("Buha");
        firstUser.setPassword("asd");
        firstUser.setRole(UserRole.ADMIN);
        firstUser.setGroup(firstGroup);
        entityManager.persist(firstUser);

        UserAccount secondUser = new UserAccount();
        secondUser.setId("user-2");
        secondUser.setName("Niki");
        secondUser.setPassword("asd");
        secondUser.setRole(UserRole.MEMBER);
        secondUser.setGroup(firstGroup);
        entityManager.persist(secondUser);

        UserAccount thirdUser = new UserAccount();
        thirdUser.setId("user-3");
        thirdUser.setName("Other");
        thirdUser.setPassword("asd");
        thirdUser.setRole(UserRole.MEMBER);
        thirdUser.setGroup(secondGroup);
        entityManager.persist(thirdUser);

        entityManager.flush();
        entityManager.clear();

        List<UserAccount> result = userAccountRepository.findByGroupId("group-1");

        assertThat(result).hasSize(2);
        assertThat(result)
                .extracting(UserAccount::getId)
                .containsExactlyInAnyOrder("user-1", "user-2");
    }

    @Test
    @DisplayName("findByGroupId returns empty list when group has no users")
    void findByGroupId_shouldReturnEmptyListWhenNoUsersExist() {
        List<UserAccount> result = userAccountRepository.findByGroupId("missing-group");

        assertThat(result).isEmpty();
    }
}