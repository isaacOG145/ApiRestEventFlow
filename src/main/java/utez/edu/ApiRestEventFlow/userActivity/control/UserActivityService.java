package utez.edu.ApiRestEventFlow.userActivity.control;

import jakarta.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import utez.edu.ApiRestEventFlow.activity.model.Activity;
import utez.edu.ApiRestEventFlow.activity.model.ActivityDTO;
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
    public ResponseEntity<Message> registerForEvent(UserActivityDTO userActivityDTO) {
        try {
            // Verificamos que el usuario existe
            User user = userRepository.findById(userActivityDTO.getUserId())
                    .orElseThrow(() -> new ValidationException(ErrorMessages.USER_NOT_FOUND));

            // Verificamos que la actividad existe
            Activity activity = activityRepository.findById(userActivityDTO.getActivityId())
                    .orElseThrow(() -> new ValidationException(ErrorMessages.ACTIVITY_NOT_FOUND));

            // Verificamos si el usuario ya está inscrito a la actividad
            Optional<UserActivity> existingUserActivity = userActivityRepository.findByUserIdAndActivityId(userActivityDTO.getUserId(), userActivityDTO.getActivityId());

            if (existingUserActivity.isPresent()) {
                return new ResponseEntity<>(new Message(ErrorMessages.USER_ALREADY_REGISTERED, TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
            }

            // Crear un nuevo objeto UserActivity para registrar la inscripción
            UserActivity userActivity = new UserActivity();
            userActivity.setUser(user);
            userActivity.setActivity(activity);
            userActivity.setVerified(false);  // Estado por defecto: no verificado
            userActivity.setStatus(true);     // Estado de inscripción activo

            // Generar un token único para la inscripción
            String token = UUID.randomUUID().toString();
            userActivity.setToken(token);

            // Guardamos la inscripción en la base de datos
            UserActivity savedUserActivity = userActivityRepository.saveAndFlush(userActivity);

            return new ResponseEntity<>(new Message(savedUserActivity, ErrorMessages.SUCCESSFUL_REGISTRATION, TypesResponse.SUCCESS), HttpStatus.OK);

        } catch (ValidationException e) {
            return new ResponseEntity<>(new Message(e.getMessage(), TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(new Message(ErrorMessages.INTERNAL_SERVER_ERROR, TypesResponse.ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @Transactional(readOnly = true)
    public ResponseEntity<Message> findByUser(Long userId) {
        try {
            List<UserActivity> userActivities = userActivityRepository.findAllByUserId(userId);

            if (userActivities.isEmpty()) {
                return new ResponseEntity<>(
                        new Message(ErrorMessages.INVITATION_NOT_FOUND, TypesResponse.WARNING),
                        HttpStatus.NOT_FOUND
                );
            }

            return new ResponseEntity<>(
                    new Message(userActivities, "Actividades de usuario encontradas", TypesResponse.SUCCESS),
                    HttpStatus.OK
            );
        } catch (ValidationException e) {
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

    @Transactional(readOnly = true)
    public ResponseEntity<Message> findByActivity(Long activityId) {
        try {
            List<UserActivity> activityUsers = userActivityRepository.findAllByActivityId(activityId);

            if (activityUsers.isEmpty()) {
                return new ResponseEntity<>(
                        new Message(ErrorMessages.INVITATION_NOT_FOUND, TypesResponse.WARNING),
                        HttpStatus.NOT_FOUND
                );
            }

            return new ResponseEntity<>(
                    new Message(activityUsers, "Usuarios de actividad encontrados", TypesResponse.SUCCESS),
                    HttpStatus.OK
            );
        } catch (ValidationException e) {
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

    @Transactional(readOnly = true)
    public ResponseEntity<Message> findByToken(String token) {
        try {
            Optional<UserActivity> userActivityOptional = userActivityRepository.findByToken(token);

            if (userActivityOptional.isEmpty()) {
                return new ResponseEntity<>(
                        new Message(ErrorMessages.INVITATION_NOT_FOUND, TypesResponse.WARNING),
                        HttpStatus.NOT_FOUND
                );
            }

            UserActivity userActivity = userActivityOptional.get();
            return new ResponseEntity<>(
                    new Message(userActivity, "Actividad de usuario encontrada", TypesResponse.SUCCESS),
                    HttpStatus.OK
            );
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

    @Transactional(rollbackFor = {SQLException.class})
    public ResponseEntity<Message> confirmateInvitation(UserActivityDTO userActivityDTO) {
        try{

            UserActivity invitation = userActivityRepository.findByToken(userActivityDTO.getToken())
                    .orElseThrow(() -> new ValidationException(ErrorMessages.INVITATION_NOT_FOUND));

            if(invitation.isVerified()){
                return new ResponseEntity<>(new Message("Esta invitación ya fue usada", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
            }

            invitation.setVerified(true);

            invitation = userActivityRepository.save(invitation);

            return new ResponseEntity<>(new Message(invitation, ErrorMessages.SUCCESFUL_UPDATE, TypesResponse.SUCCESS), HttpStatus.OK);

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
