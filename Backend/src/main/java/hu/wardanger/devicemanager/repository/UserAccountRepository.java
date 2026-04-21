package hu.wardanger.devicemanager.repository;

import hu.wardanger.devicemanager.entity.UserAccount;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserAccountRepository extends JpaRepository<UserAccount, String> {

    @EntityGraph(attributePaths = {
            "rootMenu"
    })
    Optional<UserAccount> findDetailedById(String id);

    List<UserAccount> findByGroupId(String groupId);
}