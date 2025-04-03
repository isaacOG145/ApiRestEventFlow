package utez.edu.ApiRestEventFlow.assignment.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import utez.edu.ApiRestEventFlow.activity.model.Activity;

import java.util.List;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Long> {

    List<Assignment> findByActivityId(Long activityId);

    List <Assignment> findByUserId(Long userId);

    List<Assignment> findByOwnerAndActivityIn(Long ownerId, List<Activity> activities);
}
