package utez.edu.ApiRestEventFlow.userActivity.model;

import jakarta.persistence.*;
import utez.edu.ApiRestEventFlow.activity.model.Activity;
import utez.edu.ApiRestEventFlow.user.model.User;

@Entity
@Table(name = "user_activity")
public class UserActivity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_activity_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "activity_id", referencedColumnName = "activity_id", nullable = false)
    private Activity activity;

    @Column(name = "from_activity")
    private Long fromActivityId;

    @Column(name = "token", columnDefinition = "VARCHAR(36)")
    private String token;

    @Column(name = "verified", columnDefinition = "BOOL DEFAULT FALSE")
    private boolean verified;


    // Constructor vacío
    public UserActivity() {}


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

    public Long getFromActivityId() {
        return fromActivityId;
    }

    public void setFromActivityId(Long fromActivityId) {
        this.fromActivityId = fromActivityId;
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
}