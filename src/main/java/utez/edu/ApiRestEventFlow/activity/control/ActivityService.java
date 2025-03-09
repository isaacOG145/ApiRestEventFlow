package utez.edu.ApiRestEventFlow.activity.control;

import jakarta.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.events.Event;
import utez.edu.ApiRestEventFlow.Role.Role;
import utez.edu.ApiRestEventFlow.Role.TypeActivity;
import utez.edu.ApiRestEventFlow.activity.model.Activity;
import utez.edu.ApiRestEventFlow.activity.model.ActivityDTO;
import utez.edu.ApiRestEventFlow.activity.model.ActivityRepository;
import utez.edu.ApiRestEventFlow.user.model.User;
import utez.edu.ApiRestEventFlow.user.model.UserDTO;
import utez.edu.ApiRestEventFlow.user.model.UserRepository;
import utez.edu.ApiRestEventFlow.utils.Message;
import utez.edu.ApiRestEventFlow.utils.TypesResponse;
import utez.edu.ApiRestEventFlow.validation.ErrorMessages;

import java.sql.SQLException;
import java.util.List;

@Service
public class ActivityService {

    private final ActivityRepository activityRepository;
    private final UserRepository userRepository;

    @Autowired
    public ActivityService(ActivityRepository activityRepository, UserRepository userRepository) {
        this.activityRepository = activityRepository;
        this.userRepository = userRepository;
    }

    private void validateAdmin(User owner) {
        if (!owner.getRole().equals(Role.ADMIN)) {
            throw new ValidationException(ErrorMessages.IS_NOT_ADMIM);
        }
    }

    private void validateEvent(Activity activity) {
        if (!activity.getTypeActivity().equals(TypeActivity.EVENT)) {
            throw new ValidationException(ErrorMessages.IS_NOT_EVENT);
        }
    }

    @Transactional(readOnly = true)
    public ResponseEntity<Message> findAll() {
        try {
            List<Activity> activities = activityRepository.findAll();
            if (activities.isEmpty()) {
                return new ResponseEntity<>(new Message(ErrorMessages.ACTIVITY_NOT_FOUND, TypesResponse.WARNING), HttpStatus.OK);
            }
            return new ResponseEntity<>(new Message(activities, "Lista de actividades", TypesResponse.SUCCESS), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new Message(ErrorMessages.INTERNAL_SERVER_ERROR, TypesResponse.ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional(readOnly = true)
    public ResponseEntity<Message> findAllEvents() {
        try {
            List<Activity> activities = activityRepository.findByTypeActivity(TypeActivity.EVENT);
            if (activities.isEmpty()) {
                return new ResponseEntity<>(new Message(ErrorMessages.ACTIVITIES_NOT_FOUND, TypesResponse.WARNING), HttpStatus.OK);
            }
            return new ResponseEntity<>(new Message(activities, "Lista de eventos", TypesResponse.SUCCESS), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new Message(ErrorMessages.INTERNAL_SERVER_ERROR, TypesResponse.ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @Transactional(readOnly = true)
    public ResponseEntity<Message> findByOwner(ActivityDTO activityDTO) {
        try {
            // Buscar al dueño en la base de datos
            User owner = userRepository.findById(activityDTO.getOwnerActivity().getId())
                    .orElseThrow(() -> new ValidationException(ErrorMessages.SENT_BY_USER_NOT_FOUND));
            // Validar que el dueño sea administrador
            validateAdmin(owner);

            // Buscar actividades por ID del dueño
            List<Activity> activities = activityRepository.findByOwnerActivity_Id(owner.getId());

            if (activities.isEmpty()) {
                return new ResponseEntity<>(new Message(ErrorMessages.ACTIVITIES_NOT_FOUND, TypesResponse.WARNING), HttpStatus.OK);
            }

            // Retornar la lista de actividades
            return new ResponseEntity<>(new Message(activities, "Actividades encontradas", TypesResponse.SUCCESS), HttpStatus.OK);

        } catch (ValidationException e) {
            return new ResponseEntity<>(new Message(e.getMessage(), TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(new Message(ErrorMessages.INTERNAL_SERVER_ERROR, TypesResponse.ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional(readOnly = true)
    public ResponseEntity<Message> findByFromActivity(ActivityDTO activityDTO) {
        try {

            // Buscar la actividad padre en la base de datos
            Activity fromActivity = activityRepository.findById(activityDTO.getFromActivity().getId())
                    .orElseThrow(() -> new ValidationException(ErrorMessages.ACTIVITY_NOT_FOUND));

            // Validar que la actividad padre sea un evento
            validateEvent(fromActivity);

            // Buscar actividades vinculadas por ID de la actividad padre
            List<Activity> activities = activityRepository.findByFromActivity_Id(fromActivity.getId());

            // Verificar si se encontraron actividades
            if (activities.isEmpty()) {
                return new ResponseEntity<>(new Message(ErrorMessages.ACTIVITIES_NOT_FOUND, TypesResponse.WARNING), HttpStatus.OK);
            }

            // Retornar la lista de actividades
            return new ResponseEntity<>(new Message(activities, "Actividades encontradas", TypesResponse.SUCCESS), HttpStatus.OK);

        } catch (ValidationException e) {
            return new ResponseEntity<>(new Message(e.getMessage(), TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(new Message(ErrorMessages.INTERNAL_SERVER_ERROR, TypesResponse.ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional(rollbackFor = {SQLException.class})
    public ResponseEntity<Message> saveEvent(ActivityDTO activityDTO) {
        try {
            User owner = userRepository.findById(activityDTO.getOwnerActivity().getId())
                    .orElseThrow(() -> new ValidationException(ErrorMessages.SENT_BY_USER_NOT_FOUND));

            validateAdmin(owner); // Validar que el usuario sea administrador

            Activity newActivity = new Activity();
            newActivity.setName(activityDTO.getName());
            newActivity.setDescription(activityDTO.getDescription());
            newActivity.setSpeaker(activityDTO.getSpeaker());
            newActivity.setDate(activityDTO.getDate()); // Asignar fecha para eventos
            newActivity.setTypeActivity(TypeActivity.EVENT); // Tipo de actividad: EVENT
            newActivity.setOwnerActivity(owner); // Asignar el dueño de la actividad
            newActivity.setStatus(true); // Establecer estado activo

            activityRepository.save(newActivity); // Guardar la actividad en la base de datos

            return new ResponseEntity<>(new Message(newActivity, ErrorMessages.SUCCESSFUL_REGISTRATION, TypesResponse.SUCCESS), HttpStatus.OK);

        } catch (ValidationException e) {
            return new ResponseEntity<>(new Message(e.getMessage(), TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(new Message(ErrorMessages.INTERNAL_SERVER_ERROR, TypesResponse.ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional(rollbackFor = {SQLException.class})
    public ResponseEntity<Message> saveWorkshop(ActivityDTO activityDTO) {
        try {
            Activity fromActivity = activityRepository.findById(activityDTO.getFromActivity().getId())
                    .orElseThrow(() -> new ValidationException(ErrorMessages.ACTIVITY_NOT_FOUND));

            validateEvent(fromActivity); // Validar que la actividad padre sea un evento

            Activity newActivity = new Activity();
            newActivity.setName(activityDTO.getName());
            newActivity.setDescription(activityDTO.getDescription());
            newActivity.setSpeaker(activityDTO.getSpeaker());
            newActivity.setTime(activityDTO.getTime()); // Asignar hora para talleres
            newActivity.setTypeActivity(TypeActivity.WORKSHOP); // Tipo de actividad: WORKSHOP
            newActivity.setFromActivity(fromActivity); // Asignar la actividad padre (evento)
            newActivity.setStatus(true); // Establecer estado activo

            activityRepository.save(newActivity); // Guardar la actividad en la base de datos

            return new ResponseEntity<>(new Message(newActivity, ErrorMessages.SUCCESSFUL_REGISTRATION, TypesResponse.SUCCESS), HttpStatus.OK);

        } catch (ValidationException e) {
            return new ResponseEntity<>(new Message(e.getMessage(), TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(new Message(ErrorMessages.INTERNAL_SERVER_ERROR, TypesResponse.ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
