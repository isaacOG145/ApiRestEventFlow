package utez.edu.ApiRestEventFlow.activity.control;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import utez.edu.ApiRestEventFlow.activity.model.ActivityDTO;
import utez.edu.ApiRestEventFlow.user.model.UserDTO;
import utez.edu.ApiRestEventFlow.utils.Message;

@RestController
@RequestMapping("/activity")
@CrossOrigin(origins = {"*"}, methods = {RequestMethod.POST, RequestMethod.GET, RequestMethod.PUT, RequestMethod.DELETE})
public class ActivityController {

    private ActivityService activityService;


    @Autowired
    public ActivityController(ActivityService activityService) {
        this.activityService = activityService;
    }

    @GetMapping("/findAll")
    public ResponseEntity<Message> getAllActivities() {
        return activityService.findAll();
    }

    @GetMapping("/findAllEvents")
    public ResponseEntity<Message> getAllEvents() {
        return activityService.findAllEvents();
    }

    @GetMapping("/findByOwner")
    public ResponseEntity<Message> getByOwner(@Validated @RequestBody ActivityDTO activityDTO) {
        return activityService.findByOwner(activityDTO);
    }

    @GetMapping("/findByEvent")
    public ResponseEntity<Message> getByEvent(@Validated @RequestBody ActivityDTO activityDTO) {
        return activityService.findByFromActivity(activityDTO);
    }


    @PostMapping("/saveEvent")
    public ResponseEntity<Message> saveEvent(@Validated @RequestBody ActivityDTO activityDTO) {
        return activityService.saveEvent(activityDTO);
    }

    @PostMapping("/saveWorkshop")
    public  ResponseEntity<Message> saveWorkshop(@Validated @RequestBody ActivityDTO activityDTO) {
        return activityService.saveWorkshop(activityDTO);
    }


}
