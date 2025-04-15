package utez.edu.ApiRestEventFlow.activity.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import utez.edu.ApiRestEventFlow.Role.TypeActivity;

import java.util.List;
import java.util.Optional;

public interface ActivityRepository extends JpaRepository<Activity, Long> {

    Optional<Activity> findById(Long id);

    List<Activity> findByTypeActivity(TypeActivity typeActivity);

    List<Activity> findByFromActivity_Id(Long activityId);

    @Query("SELECT a FROM Activity a WHERE a.ownerActivity.id = :ownerId AND a.status = true")
    List<Activity> findByOwnerActivityIdAndActive(@Param("ownerId") Long ownerId);


    // Método para buscar eventos por dueño
    @Query("SELECT a FROM Activity a WHERE a.ownerActivity.id = :ownerId AND a.typeActivity = 'EVENT'")
    List<Activity> findEventsByOwner(@Param("ownerId") Long ownerId);

    // Método para buscar talleres por dueño
    @Query("SELECT a FROM Activity a WHERE a.ownerActivity.id = :ownerId AND a.typeActivity = 'WORKSHOP'")
    List<Activity> findWorkshopsByOwner(@Param("ownerId") Long ownerId);

    @Query("SELECT a FROM Activity a WHERE a.typeActivity = 'EVENT' AND a.status = true")
    List<Activity> findActiveEvents();

    @Query("SELECT a FROM Activity a WHERE a.typeActivity = 'WORKSHOP' AND a.status = true")
    List<Activity> findActiveWorkshops();

    @Query("""
    SELECT a.id,
           CASE 
               WHEN COUNT(CASE WHEN u.role = 'CHECKER' AND ass.status = true THEN 1 ELSE null END) > 0 
               THEN true 
               ELSE false 
           END AS asignado
    FROM Activity a
    LEFT JOIN Assignment ass ON ass.activity.id = a.id
    LEFT JOIN User u ON u.id = ass.user.id
    WHERE a.ownerActivity.id = :ownerId AND a.status = true
    GROUP BY a.id
    """)
    List<Object[]> findActivityAssignmentStatusByOwner(@Param("ownerId") Long ownerId);







}
