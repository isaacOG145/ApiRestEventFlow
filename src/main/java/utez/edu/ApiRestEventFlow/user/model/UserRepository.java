package utez.edu.ApiRestEventFlow.user.model;

import org.hibernate.usertype.UserType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import utez.edu.ApiRestEventFlow.Role.Role;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    List<User> findByRole(Role role);

    List<User> findBySentByUser(User sentByUser);

    Optional<User> findByEmail(String email);

    Optional<User> findByEmailAndStatusTrue(String email);


    Boolean existsByEmail(String email);

    Boolean existsByPhone(String phone);

    // Checadores ocupados para eventos en una fecha
    @Query("SELECT a.user FROM Assignment a " +
            "WHERE a.status = true " +
            "AND a.activity.fromActivity IS NULL " +
            "AND a.activity.date = :date")
    List<User> findCheckersUnAvailableForEvent(@Param("date") LocalDate date);

    // Checadores ocupados para talleres en fecha + hora (fecha tomada del padre)
    @Query("SELECT a.user FROM Assignment a " +
            "WHERE a.status = true " +
            "AND a.activity.fromActivity IS NOT NULL " +
            "AND a.activity.fromActivity.date = :date " +
            "AND a.activity.time = :time")
    List<User> findCheckersUnavailableForWorkshop(@Param("date") LocalDate date, @Param("time") LocalTime time);

    // Devuelve checadores activos que no están en la lista de ocupados
    @Query("SELECT u FROM User u " +
            "WHERE u.role = 'CHECKER' AND u.status = true AND u NOT IN :ocupados")
    List<User> findAvailableCheckersExcluding(@Param("ocupados") List<User> ocupados);
}

