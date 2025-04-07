package utez.edu.ApiRestEventFlow.assignment.model;

import java.util.List;

public class AssignmentWithWorkshopsDTO {
    private Long eventId;
    private String eventName;
    private List<AssignmentDTO> eventAssignments;
    private List<AssignmentDTO> workshopAssignments;

    public AssignmentWithWorkshopsDTO(Long eventId, String eventName, List<AssignmentDTO> eventAssignments, List<AssignmentDTO> workshopAssignments) {
        this.eventId = eventId;
        this.eventName = eventName;
        this.eventAssignments = eventAssignments;
        this.workshopAssignments = workshopAssignments;
    }

    // Getters and Setters
    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public List<AssignmentDTO> getEventAssignments() {
        return eventAssignments;
    }

    public void setEventAssignments(List<AssignmentDTO> eventAssignments) {
        this.eventAssignments = eventAssignments;
    }

    public List<AssignmentDTO> getWorkshopAssignments() {
        return workshopAssignments;
    }

    public void setWorkshopAssignments(List<AssignmentDTO> workshopAssignments) {
        this.workshopAssignments = workshopAssignments;
    }
}
