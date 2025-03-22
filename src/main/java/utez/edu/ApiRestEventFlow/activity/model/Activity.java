package utez.edu.ApiRestEventFlow.activity.model;

import jakarta.persistence.*;
import utez.edu.ApiRestEventFlow.Role.TypeActivity;
import utez.edu.ApiRestEventFlow.user.model.User;

import java.sql.Date;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "activity")
public class Activity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "activity_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "owner_activity", referencedColumnName = "user_id")
    private User ownerActivity;

    @Column(name = "speaker", columnDefinition = "VARCHAR(150)")
    private String speaker;

    @Column(name = "name", columnDefinition = "VARCHAR(50)")
    private String name;

    @Column(name = "description", columnDefinition = "VARCHAR(100)")
    private String description;

    @Column(name ="quota", columnDefinition = "NUMERIC")
    private Integer quota;

    @Column(name = "date", columnDefinition = "DATE")
    private Date date; // Usado solo para eventos

    @Column(name = "time", columnDefinition = "TIME")
    private LocalTime time; // Usado solo para talleres

    @ElementCollection
    @CollectionTable(name = "activity_images", joinColumns = @JoinColumn(name = "activity_id"))
    @Column(name = "image_url", columnDefinition = "VARCHAR(255)")
    private List<String> imageUrls = new ArrayList<>();


    @ManyToOne
    @JoinColumn(name = "from_activity", referencedColumnName = "activity_id")
    private Activity fromActivity;


    @Enumerated(EnumType.STRING)
    @Column(name = "type_activity", columnDefinition = "VARCHAR(10)")
    private TypeActivity typeActivity; // EVENT o WORKSHOP

    @Column(name = "status", columnDefinition = "BOOL DEFAULT TRUE")
    private boolean status;

    // Constructor vacío
    public Activity() {}

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

    // Getters y Setters para imageUrls
    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
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
}