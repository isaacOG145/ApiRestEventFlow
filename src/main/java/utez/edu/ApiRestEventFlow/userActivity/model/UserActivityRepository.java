package utez.edu.ApiRestEventFlow.userActivity.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserActivityRepository extends JpaRepository<UserActivity, Long> {

    Optional<UserActivity> findByToken(String token);
}
