package utez.edu.ApiRestEventFlow.userActivity.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserActivityRepository extends JpaRepository<UserActivity, Long> {

    Optional<UserActivity> findByToken(String token);

    List<UserActivity> findAllByUserId(Long userId);
    List<UserActivity> findAllByActivityId(Long activityId);

    Optional<UserActivity> findByUserIdAndActivityId(Long userId, Long activityId);
}
