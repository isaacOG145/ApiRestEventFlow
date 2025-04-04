package utez.edu.ApiRestEventFlow.userActivity.model;


import jakarta.validation.constraints.NotNull;
import utez.edu.ApiRestEventFlow.activity.model.ActivityDTO;
import utez.edu.ApiRestEventFlow.validation.ErrorMessages;

import java.time.LocalDate;

public class UserActivityDTO {

    private Long id;

    @NotNull(groups = {register.class}, message = "ID del usuario requerido")
    private Long userId;
    @NotNull(groups = {register.class}, message = "ID de la actividad es requerida")
    private Long activityId;

    private String token;

    private boolean verified;

    public UserActivityDTO() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getActivityId() {
        return activityId;
    }

    public void setActivityId(Long activityId) {
        this.activityId = activityId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }
    public interface register{}

    public interface modify{}

    public interface ChangeStatus {}
}
