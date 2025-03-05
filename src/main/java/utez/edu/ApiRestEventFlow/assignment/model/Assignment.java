package utez.edu.ApiRestEventFlow.assignment.model;

import jakarta.persistence.*;
import utez.edu.ApiRestEventFlow.activity.model.Activity;
import utez.edu.ApiRestEventFlow.user.model.User;

@Entity
@Table(name = "assignment")
public class Assignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "assignment_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false)
    private User user; // Cambiado de "assignedUser" a "user"

    @ManyToOne
    @JoinColumn(name = "activity_id", referencedColumnName = "activity_id", nullable = false)
    private Activity activity;

    // Constructor vacío
    public Assignment() {}

    // Constructor con parámetros
    public Assignment(User user, Activity activity) {
        this.user = user;
        this.activity = activity;
    }

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

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }
}