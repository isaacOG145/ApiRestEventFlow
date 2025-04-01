package utez.edu.ApiRestEventFlow.activity.model;

import jakarta.validation.constraints.*;
import org.springframework.web.multipart.MultipartFile;
import utez.edu.ApiRestEventFlow.Role.TypeActivity;
import utez.edu.ApiRestEventFlow.user.model.User;
import utez.edu.ApiRestEventFlow.validation.ErrorMessages;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class ActivityDTO {

    @NotNull(groups = {ModifyEvent.class, ChangeStatus.class}, message = ErrorMessages.ID_REQUIRED)
    private Long id;

    @NotNull(groups = {RegisterEvent.class}, message = "El dueño de la actividad es obligatorio")
    private User ownerActivity;

    @NotBlank(groups = {RegisterWorkshop.class}, message = "El nombre del ponente es obligatorio")
    private String speaker;

    @NotBlank(groups = {RegisterEvent.class, RegisterWorkshop.class}, message = ErrorMessages.NAME_REQUIRED)
    private String name;

    @NotBlank(groups = {RegisterEvent.class, RegisterWorkshop.class}, message = "La descripción es obligatoria")
    private String description;

    @NotNull(groups = {RegisterWorkshop.class}, message = "El cupo es obligatorio")
    private Integer quota;

    @NotNull(groups = {RegisterEvent.class}, message = "La fecha es obligatoria")
    private LocalDate date;

    @NotNull(groups = {RegisterWorkshop.class}, message = "La hora es obligatoria")
    private LocalTime time;

    private TypeActivity typeActivity;

    @NotNull(groups = {RegisterWorkshop.class}, message = "El ID del evento es obligatorio")
    private Activity fromActivity;

    // Campo para las imágenes
    private List<MultipartFile> images;

    // Campo para las URLs de imágenes a eliminar
    private List<String> deletedImages;

    // Campo para las URLs de imágenes existentes que se conservan
    private List<String> existingImages;

    private boolean status;

    public ActivityDTO() {}

    // Getters y Setters
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

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
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

    public Activity getFromActivity() {
        return fromActivity;
    }

    public void setFromActivity(Activity fromActivity) {
        this.fromActivity = fromActivity;
    }

    public List<MultipartFile> getImages() {
        return images;
    }

    public void setImages(List<MultipartFile> images) {
        this.images = images;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }


    // Nuevos getters y setters
    public List<String> getDeletedImages() {
        return deletedImages;
    }

    public void setDeletedImages(List<String> deletedImages) {
        this.deletedImages = deletedImages;
    }

    public List<String> getExistingImages() {
        return existingImages;
    }

    public void setExistingImages(List<String> existingImages) {
        this.existingImages = existingImages;
    }

    // Interfaces para validación por grupos
    public interface RegisterEvent {}

    public interface RegisterWorkshop {}

    public interface ModifyEvent {}

    public interface ModifyWorkshop {}

    public interface ChangeStatus {}
}