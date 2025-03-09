package utez.edu.ApiRestEventFlow.activity.control;

import jakarta.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import utez.edu.ApiRestEventFlow.Role.Role;
import utez.edu.ApiRestEventFlow.Role.TypeActivity;
import utez.edu.ApiRestEventFlow.activity.model.Activity;
import utez.edu.ApiRestEventFlow.activity.model.ActivityDTO;
import utez.edu.ApiRestEventFlow.user.model.User;
import utez.edu.ApiRestEventFlow.user.model.UserDTO;
import utez.edu.ApiRestEventFlow.utils.Message;
import utez.edu.ApiRestEventFlow.utils.TypesResponse;
import utez.edu.ApiRestEventFlow.validation.ErrorMessages;

import java.sql.SQLException;

@RestController
@RequestMapping("/activity")
@CrossOrigin(origins = {"*"}, methods = {RequestMethod.POST, RequestMethod.GET, RequestMethod.PUT, RequestMethod.DELETE})
public class ActivityController {

    private ActivityService activityService;

    @Autowired
    public ActivityController(ActivityService activityService) {
        this.activityService = activityService;
    }

    @Transactional(rollbackFor = {SQLException.class})
    public ResponseEntity<Message> saveEvent(ActivityDTO activityDTO) {
        try{

            //validaciones extra

            Activity newActivity = new Activity();

            newActivity.setName(activityDTO.getName());
            newActivity.setDescription(activityDTO.getDescription());
            newActivity.setSpeaker(activityDTO.getSpeaker());
            newActivity.setDate(activityDTO.getDate());
            newActivity.setTypeActivity(TypeActivity.EVENT);
            newActivity.setStatus(true);

            return new ResponseEntity<>(new Message(newActivity, ErrorMessages.SUCCESSFUL_REGISTRATION, TypesResponse.SUCCESS), HttpStatus.OK);

        } catch (ValidationException e) {
            return new ResponseEntity<>(new Message(e.getMessage(), TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(new Message(ErrorMessages.INTERNAL_SERVER_ERROR, TypesResponse.ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }


}
