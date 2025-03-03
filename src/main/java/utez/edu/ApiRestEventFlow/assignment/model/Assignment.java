package utez.edu.ApiRestEventFlow.assignment.model;

import jakarta.persistence.*;
import utez.edu.ApiRestEventFlow.event.model.Event;
import utez.edu.ApiRestEventFlow.user.model.User;
import utez.edu.ApiRestEventFlow.workshop.model.Workshop;

@Entity
@Table(name = "assignment")
public class Assignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "assignment_id")
    private Long id;

    // Relación Many-to-One con User
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false) // Columna que almacena la clave foránea de User
    private User user; // Varias asignaciones pueden pertenecer a un usuario

    // Relación Many-to-One con Event
    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false) // Columna que almacena la clave foránea de Event
    private Event event; // Varias asignaciones pueden estar vinculadas a un evento

    // Relación Many-to-One con Workshop
    @ManyToOne
    @JoinColumn(name = "workshop_id", nullable = false) // Columna que almacena la clave foránea de Workshop
    private Workshop workshop; // Varias asignaciones pueden estar vinculadas a un taller

    // Constructor vacío
    public Assignment() {}

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public Workshop getWorkshop() {
        return workshop;
    }

    public void setWorkshop(Workshop workshop) {
        this.workshop = workshop;
    }
}