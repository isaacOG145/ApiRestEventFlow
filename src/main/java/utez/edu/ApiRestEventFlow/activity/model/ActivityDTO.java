package utez.edu.ApiRestEventFlow.activity.model;

import jakarta.validation.constraints.*;
import utez.edu.ApiRestEventFlow.Role.TypeActivity;
import utez.edu.ApiRestEventFlow.user.model.User;
import utez.edu.ApiRestEventFlow.user.model.UserDTO;
import utez.edu.ApiRestEventFlow.validation.ErrorMessages;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalTime;

public class ActivityDTO {

    @NotNull(groups = {ModifyEvent.class, ChangeStatus.class}, message = ErrorMessages.ID_REQUIRED)
    private Long id;
    @NotNull(groups = {RegisterEvent.class}, message = "El dueño de la actividad es obligatorio")
    private User ownerActivity;
    @NotBlank(groups = {RegisterWorkshop.class}, message = "El nombre del ponente es obligatorio")
    private String speaker;
    @NotBlank(groups = {RegisterEvent.class,RegisterWorkshop.class}, message = ErrorMessages.NAME_REQUIRED)
    private String name;
    @NotBlank(groups = {RegisterEvent.class, RegisterWorkshop.class}, message = "La descripción es obligatoria")
    private String description;
    @NotNull(groups = {RegisterWorkshop.class}, message = "El cupo es obligatorio")
    private Integer quota;
    @NotNull(groups = {RegisterEvent.class}, message = "La fecha es obligatoria")
    private Date date;
    @NotNull(groups = {RegisterWorkshop.class}, message = "La hora es obligatoria")
    private LocalTime time;
    private TypeActivity typeActivity;
    @NotNull(groups = {RegisterWorkshop.class}, message = "El ID del evento es obligatorio")
    private Activity fromActivity;

    //faltan las imagenes

    private boolean status;

    public ActivityDTO() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getOwnerActivity() {
        return ownerActivity;
    }

    public void setOwnerActivity(User ownerActivity) {
        this.ownerActivity = ownerActivity;
    }

    public String getSpeaker() {
        return speaker;
    }

    public void setSpeaker(String speaker) {
        this.speaker = speaker;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getQuota() {
        return quota;
    }

    public void setQuota(Integer quota) {
        this.quota = quota;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public TypeActivity getTypeActivity() {
        return typeActivity;
    }

    public void setTypeActivity(TypeActivity typeActivity) {
        this.typeActivity = typeActivity;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public Activity getFromActivity() {
        return fromActivity;
    }

    public void setFromActivity(Activity fromActivity) {
        this.fromActivity = fromActivity;
    }

    public interface RegisterEvent{}

    public interface RegisterWorkshop{}

    public interface ModifyEvent{}

    public interface ModifyWorkshop{}

    public interface ChangeStatus{}
}
