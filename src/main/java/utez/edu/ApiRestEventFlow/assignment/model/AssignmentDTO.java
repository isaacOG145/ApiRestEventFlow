package utez.edu.ApiRestEventFlow.assignment.model;

import utez.edu.ApiRestEventFlow.activity.model.Activity;
import utez.edu.ApiRestEventFlow.user.model.User;

public class AssignmentDTO {

    private Long id;
    private Activity activity;
    private User user;

    public AssignmentDTO(){}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public interface Register{}

    public interface Modify{}

    public interface ChangeStatus{}
}
