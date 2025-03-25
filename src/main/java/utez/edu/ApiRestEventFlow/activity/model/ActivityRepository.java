package utez.edu.ApiRestEventFlow.activity.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import utez.edu.ApiRestEventFlow.Role.TypeActivity;

import java.util.List;

public interface ActivityRepository extends JpaRepository<Activity, Long> {

    List<Activity> findByTypeActivity(TypeActivity typeActivity);

    List<Activity> findByOwnerActivity_Id(Long ownerId);

    List<Activity> findByFromActivity_Id(Long activityId);

    // Método para buscar eventos por dueño
    @Query("SELECT a FROM Activity a WHERE a.ownerActivity.id = :ownerId AND a.typeActivity = 'EVENT'")
    List<Activity> findEventsByOwner(@Param("ownerId") Long ownerId);

    // Método para buscar talleres por dueño
    @Query("SELECT a FROM Activity a WHERE a.ownerActivity.id = :ownerId AND a.typeActivity = 'WORKSHOP'")
    List<Activity> findWorkshopsByOwner(@Param("ownerId") Long ownerId);


}
