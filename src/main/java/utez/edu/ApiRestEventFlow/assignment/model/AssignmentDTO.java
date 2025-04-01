package utez.edu.ApiRestEventFlow.assignment.model;

public class AssignmentDTO {
    private Long assignmentId;
    private Long userId;
    private Long activityId;
    private boolean status;

    // Constructor vacío
    public AssignmentDTO() {}

    // Constructor con parámetros
    public AssignmentDTO(Long assignmentId, Long userId, Long activityId) {
        this.assignmentId = assignmentId;
        this.userId = userId;
        this.activityId = activityId;
    }

    // Getters y Setters
    public Long getAssignmentId() {
        return assignmentId;
    }

    public void setAssignmentId(Long assignmentId) {
        this.assignmentId = assignmentId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public Long getActivityId() {
        return activityId;
    }

    public void setActivityId(Long activityId) {
        this.activityId = activityId;
    }
}