package utez.edu.ApiRestEventFlow.userActivity.control;

import jakarta.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import utez.edu.ApiRestEventFlow.activity.model.Activity;
import utez.edu.ApiRestEventFlow.activity.model.ActivityRepository;
import utez.edu.ApiRestEventFlow.user.model.User;
import utez.edu.ApiRestEventFlow.user.model.UserRepository;
import utez.edu.ApiRestEventFlow.userActivity.model.UserActivity;
import utez.edu.ApiRestEventFlow.userActivity.model.UserActivityDTO;
import utez.edu.ApiRestEventFlow.userActivity.model.UserActivityRepository;
import utez.edu.ApiRestEventFlow.utils.Message;
import utez.edu.ApiRestEventFlow.utils.TypesResponse;
import utez.edu.ApiRestEventFlow.validation.ErrorMessages;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserActivityService {

    private final UserActivityRepository userActivityRepository;
    private final ActivityRepository activityRepository;
    private final UserRepository userRepository;

    @Autowired

    public UserActivityService(UserActivityRepository userActivityRepository, ActivityRepository activityRepository, UserRepository userRepository) {
        this.userActivityRepository = userActivityRepository;
        this.activityRepository = activityRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public ResponseEntity<Message> findAll() {
        try {
            List<UserActivity> invitations = userActivityRepository.findAll();
            if (invitations.isEmpty()) {

                return new ResponseEntity<>(new Message(ErrorMessages.INVITATIONS_NOT_FOUND, TypesResponse.WARNING), HttpStatus.OK);
            }
            return new ResponseEntity<>(new Message(invitations, "Lista de invitaciones", TypesResponse.SUCCESS), HttpStatus.OK);
        } catch (Exception e) {

            return new ResponseEntity<>(new Message(ErrorMessages.INTERNAL_SERVER_ERROR, TypesResponse.ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional(rollbackFor = {SQLException.class})
    public ResponseEntity<Message> saveInvitation(UserActivityDTO userActivityDTO) {
        try {

            User user = userRepository.findById(userActivityDTO.getUserId())
                    .orElseThrow(() -> new ValidationException(ErrorMessages.USER_NOT_FOUND));

            Activity activity = activityRepository.findById(userActivityDTO.getActivityId())
                    .orElseThrow(() -> new ValidationException(ErrorMessages.ACTIVITY_NOT_FOUND));

            // Crear un nuevo objeto UserActivity
            UserActivity userActivity = new UserActivity();
            userActivity.setUser(user);
            userActivity.setActivity(activity);
            userActivity.setFromActivityId(activity.getFromActivity().getId());
            userActivity.setVerified(false);

            String token = UUID.randomUUID().toString();  // Genera un token único
            userActivity.setToken(token);


            UserActivity savedUserActivity = userActivityRepository.save(userActivity);


            return new ResponseEntity<>(new Message(savedUserActivity, ErrorMessages.SUCCESSFUL_REGISTRATION, TypesResponse.SUCCESS), HttpStatus.OK);
        }catch (ValidationException e) {
            return new ResponseEntity<>(
                    new Message(e.getMessage(), TypesResponse.WARNING),
                    HttpStatus.BAD_REQUEST
            );
        } catch (Exception e) {
            return new ResponseEntity<>(
                    new Message(ErrorMessages.INTERNAL_SERVER_ERROR, TypesResponse.ERROR),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }



}
