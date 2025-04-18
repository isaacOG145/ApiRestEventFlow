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

    @JoinColumn(name = "owner_id", referencedColumnName = "owner_id", nullable = false)
    private Long owner;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "activity_id", referencedColumnName = "activity_id", nullable = false)
    private Activity activity;

    @Column(name = "status", columnDefinition = "BOOL DEFAULT TRUE")
    private boolean status;


    public Assignment() {}

    public Assignment(Long owner,User user, Activity activity) {
        this.owner = owner;
        this.user = user;
        this.activity = activity;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOwner() {
        return owner;
    }

    public void setOwner(Long owner) {
        this.owner = owner;
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

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}