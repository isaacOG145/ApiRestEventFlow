package utez.edu.ApiRestEventFlow.userActivity.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserActivityRepository extends JpaRepository<UserActivity, Long> {

    // Query mejorada que cuenta SOLO invitaciones verificadas Y activas (status=true)
    @Query("SELECT COUNT(ua) FROM UserActivity ua " +
            "WHERE ua.activity.id = :activityId " +
            "AND ua.status = true")
    int countValidRegistrationsByActivityId(@Param("activityId") Long activityId);

    // Métodos existentes (se mantienen igual)
    Optional<UserActivity> findByToken(String token);
    List<UserActivity> findAllByUserId(Long userId);
    List<UserActivity> findAllByActivityId(Long activityId);
    Optional<UserActivity> findByUserIdAndActivityId(Long userId, Long activityId);
    Optional<UserActivity> findByUserIdAndActivityIdAndStatusTrue(Long userId, Long activityId);

    // Query para encontrar registros de usuario específicos (activos y verificados)
    @Query("SELECT ua FROM UserActivity ua " +
            "WHERE ua.user.id = :userId " +
            "AND ua.status = true " +
            "AND ua.activity.typeActivity = 'EVENT'") // Solo eventos
    List<UserActivity> findAllByUserIdAndStatusAndVerified(
            @Param("userId") Long userId,
            @Param("status") boolean status

    );

    // Query para encontrar registros de usuario específicos (activos y verificados)
    @Query("SELECT ua FROM UserActivity ua " +
            "WHERE ua.user.id = :userId " +
            "AND ua.status = true " ) // Solo eventos
    List<UserActivity> findAllActivityInscriptionByUserId(
            @Param("userId") Long userId,
            @Param("status") boolean status

    );


}