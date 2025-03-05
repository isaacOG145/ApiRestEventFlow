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

    @Column(name = "qr", columnDefinition = "VARCHAR(255)")
    private String qr;

    @Column(name = "verified", columnDefinition = "BOOL DEFAULT FALSE")
    private boolean verified;

    @Column(name = "status", columnDefinition = "VARCHAR(20) DEFAULT 'PENDING'")
    private String status; // Puede ser "PENDING", "CONFIRMED", "CANCELLED", etc.

    // Constructor vacío
    public UserActivity() {}

    // Constructor con parámetros
    public UserActivity(User user, Activity activity, String qr, boolean verified, String status) {
        this.user = user;
        this.activity = activity;
        this.qr = qr;
        this.verified = verified;
        this.status = status;
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

    public String getQr() {
        return qr;
    }

    public void setQr(String qr) {
        this.qr = qr;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}