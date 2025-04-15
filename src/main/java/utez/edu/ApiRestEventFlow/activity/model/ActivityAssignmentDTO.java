package utez.edu.ApiRestEventFlow.activity.model;

public class ActivityAssignmentDTO {
    private Long id;
    private Boolean asignado;

    public ActivityAssignmentDTO(Long id, Boolean asignado) {
        this.id = id;
        this.asignado = asignado;
    }

    // Getters y setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getAsignado() {
        return asignado;
    }

    public void setAsignado(Boolean asignado) {
        this.asignado = asignado;
    }
}
