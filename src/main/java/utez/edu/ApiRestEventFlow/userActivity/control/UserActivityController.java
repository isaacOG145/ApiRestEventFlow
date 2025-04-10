package utez.edu.ApiRestEventFlow.userActivity.control;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import utez.edu.ApiRestEventFlow.user.model.UserDTO;
import utez.edu.ApiRestEventFlow.userActivity.model.UserActivityDTO;
import utez.edu.ApiRestEventFlow.userActivity.model.UserActivityRepository;
import utez.edu.ApiRestEventFlow.utils.Message;

@RestController
@RequestMapping("/user-activities")
@CrossOrigin(origins = {"*"}, methods = {RequestMethod.POST, RequestMethod.GET, RequestMethod.PUT, RequestMethod.DELETE})
public class UserActivityController {
    private final UserActivityService userActivityService;

    @Autowired
    public UserActivityController( UserActivityService userActivityService) {

        this.userActivityService = userActivityService;
    }

    //buscar todos
    @GetMapping("/findAll")
    public ResponseEntity<Message> getAllActivities() {
        return userActivityService.findAll();
    }

    @GetMapping("/findByActivity/{id}")
    public ResponseEntity<Message> findByActivity(@PathVariable Long id) {
        return userActivityService.findByActivity(id);
    }

    @GetMapping("/findByUser/{id}")
    public ResponseEntity<Message> findByUser(@PathVariable Long id) {
        return userActivityService.findByUser(id);
    }

    @GetMapping("/findByToken/{token}")
    public ResponseEntity<Message> findActivityByToken(@PathVariable String token) {
        return userActivityService.findByToken(token);
    }

    @PostMapping("/save")
    public ResponseEntity<Message> saveActivity(@Validated(UserActivityDTO.register.class)@RequestBody UserActivityDTO userActivityDTO) {
        return userActivityService.registerForEvent(userActivityDTO);
    }

    @PutMapping("/confirmate")
    public ResponseEntity<Message> confirmActivity(@RequestBody UserActivityDTO userActivityDTO) {
        return userActivityService.confirmateInvitation(userActivityDTO);
    }
}
