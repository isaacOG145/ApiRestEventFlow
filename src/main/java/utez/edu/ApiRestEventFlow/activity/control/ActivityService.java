package utez.edu.ApiRestEventFlow.activity.control;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import utez.edu.ApiRestEventFlow.activity.model.ActivityRepository;

@Service
public class ActivityService {

    private final ActivityRepository activityRepository;

    @Autowired
    public ActivityService(ActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }


}
