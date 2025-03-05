package utez.edu.ApiRestEventFlow.user.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import utez.edu.ApiRestEventFlow.Role.Role;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository <User, Long> {

    List<User> findByRole(Role role);

}
