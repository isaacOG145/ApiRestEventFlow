package utez.edu.ApiRestEventFlow.activity.model;

import utez.edu.ApiRestEventFlow.Role.TypeActivity;
import utez.edu.ApiRestEventFlow.user.model.User;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalTime;

public class ActivityDTO {

    private Long id;
    private User ownerActivity;
    private String speaker;
    private String name;
    private String description;
    private Date date;
    private LocalTime time;
    private TypeActivity typeActivity;
    private Activity fromActivity;

    //faltan las imagenes

    private boolean status;

    public ActivityDTO() {}

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
