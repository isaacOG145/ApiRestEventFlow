package utez.edu.ApiRestEventFlow.activity.model;

import org.springframework.data.jpa.repository.JpaRepository;
import utez.edu.ApiRestEventFlow.Role.TypeActivity;

import java.util.List;

public interface ActivityRepository extends JpaRepository<Activity, Long> {

    List<Activity> findByTypeActivity(TypeActivity typeActivity);

    List<Activity> findByOwnerActivity_Id(Long ownerId);

    List<Activity> findByFromActivity_Id(Long activityId);


}
