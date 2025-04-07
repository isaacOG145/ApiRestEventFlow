package utez.edu.ApiRestEventFlow.user.model;

import org.hibernate.usertype.UserType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import utez.edu.ApiRestEventFlow.Role.Role;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository <User, Long> {

    List<User> findByRole(Role role);

    List<User> findBySentByUser(User sentByUser);

    Optional<User> findByEmail(String email);

    Boolean existsByEmail(String email);

    Boolean existsByPhone(String phone);

}
