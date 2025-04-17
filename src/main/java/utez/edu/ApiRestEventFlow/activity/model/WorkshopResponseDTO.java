package utez.edu.ApiRestEventFlow.activity.model;

import java.time.LocalTime;
import java.util.List;

public class WorkshopResponseDTO {
    private Long id;
    private String name;
    private String description;
    private String speaker;
    private LocalTime time;
    private List<String> imageUrls;
    private Integer totalQuota;
    private Integer availableQuota;
    private String associatedEvent; // Nombre del evento padre

    // Constructor
    public WorkshopResponseDTO(Activity activity, Integer availableQuota) {
        this.id = activity.getId();
        this.name = activity.getName();
        this.description = activity.getDescription();
        this.speaker = activity.getSpeaker();
        this.time = activity.getTime();
        this.imageUrls = activity.getImageUrls();
        this.totalQuota = activity.getQuota();
        this.availableQuota = availableQuota;
        this.associatedEvent = activity.getFromActivity() != null ?
                activity.getFromActivity().getName() : null;
    }

    // Getters (no necesitamos setters para un DTO de respuesta)
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getSpeaker() { return speaker; }
    public LocalTime getTime() { return time; }
    public List<String> getImageUrls() { return imageUrls; }
    public Integer getTotalQuota() { return totalQuota; }
    public Integer getAvailableQuota() { return availableQuota; }
    public String getAssociatedEvent() { return associatedEvent; }
}