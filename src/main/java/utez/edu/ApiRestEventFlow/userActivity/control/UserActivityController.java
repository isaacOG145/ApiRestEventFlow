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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/user-activities")
@CrossOrigin(origins = {"*"}, methods = {RequestMethod.POST, RequestMethod.GET, RequestMethod.PUT, RequestMethod.DELETE})
public class UserActivityController {
    private final UserActivityService userActivityService;
    private static final Logger logger = LoggerFactory.getLogger(UserActivityController.class);

    @Autowired
    public UserActivityController( UserActivityService userActivityService) {

        this.userActivityService = userActivityService;
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

    @GetMapping("/findByUserAndActivity")
    public ResponseEntity<Message> findByUserAndActivity(@RequestParam Long userId, @RequestParam Long activityId) {

        ResponseEntity<Message> response = userActivityService.findByUserAndActivity(userId, activityId);
        logger.info("Respuesta desde findByUserAndActivity: {}", response.getBody());
        return response;
    }


    @PostMapping("/save")
    public ResponseEntity<Message> saveActivity(@Validated(UserActivityDTO.register.class)@RequestBody UserActivityDTO userActivityDTO) {
        return userActivityService.registerForEvent(userActivityDTO);
    }

    @PostMapping("/workshop/save")
    public ResponseEntity<Message> saveWorkshop(@Validated(UserActivityDTO.register.class)@RequestBody UserActivityDTO userActivityDTO) {
        return userActivityService.registerForWorkshop(userActivityDTO);
    }


    @PutMapping("/confirm")
    public ResponseEntity<Message> confirmActivity(@RequestBody UserActivityDTO userActivityDTO) {
        return userActivityService.confirmInvitation(userActivityDTO);
    }

    @PutMapping("/cancel")
    public ResponseEntity<Message> cancelActivity(@RequestBody UserActivityDTO userActivityDTO) {
        return userActivityService.cancelInvitation(userActivityDTO);
    }
}
