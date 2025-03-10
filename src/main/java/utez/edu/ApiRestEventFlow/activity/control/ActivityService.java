package utez.edu.ApiRestEventFlow.activity.control;

import jakarta.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import utez.edu.ApiRestEventFlow.Role.Role;
import utez.edu.ApiRestEventFlow.Role.TypeActivity;
import utez.edu.ApiRestEventFlow.activity.model.Activity;
import utez.edu.ApiRestEventFlow.activity.model.ActivityDTO;
import utez.edu.ApiRestEventFlow.activity.model.ActivityRepository;
import utez.edu.ApiRestEventFlow.user.model.User;
import utez.edu.ApiRestEventFlow.user.model.UserRepository;
import utez.edu.ApiRestEventFlow.utils.Message;
import utez.edu.ApiRestEventFlow.utils.TypesResponse;
import utez.edu.ApiRestEventFlow.validation.DateUtils;
import utez.edu.ApiRestEventFlow.validation.ErrorMessages;

import java.sql.Date;
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

    private void validateWorkshop(Activity activity) {
        if (!activity.getTypeActivity().equals(TypeActivity.WORKSHOP)) {
            throw new ValidationException(ErrorMessages.IS_NOT_WORKSHOP);
        }
    }

    //Esto no ha funcionado
    /*
    // private void validateFutureDate(Date date) {
    //        if (!DateUtils.isFutureDate(date)) {
    //            throw new ValidationException("La fecha debe ser futura");
    //        }
    //    }
    // */

    @Transactional(readOnly = true)
    public ResponseEntity<Message> findAll() {
        try {
            List<Activity> activities = activityRepository.findAll();
            if (activities.isEmpty()) {

                return new ResponseEntity<>(new Message(ErrorMessages.ACTIVITIES_NOT_FOUND, TypesResponse.WARNING), HttpStatus.OK);
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
            User owner = userRepository.findById(activityDTO.getOwnerActivity().getId())
                    .orElseThrow(() -> new ValidationException(ErrorMessages.SENT_BY_USER_NOT_FOUND));
            validateAdmin(owner);

            List<Activity> activities = activityRepository.findByOwnerActivity_Id(owner.getId());

            if (activities.isEmpty()) {
                return new ResponseEntity<>(new Message(ErrorMessages.ACTIVITIES_NOT_FOUND, TypesResponse.WARNING), HttpStatus.OK);
            }

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
            Activity fromActivity = activityRepository.findById(activityDTO.getFromActivity().getId())
                    .orElseThrow(() -> new ValidationException(ErrorMessages.ACTIVITY_NOT_FOUND));
            validateEvent(fromActivity);

            List<Activity> activities = activityRepository.findByFromActivity_Id(fromActivity.getId());

            if (activities.isEmpty()) {
                return new ResponseEntity<>(new Message(ErrorMessages.ACTIVITIES_NOT_FOUND, TypesResponse.WARNING), HttpStatus.OK);
            }

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

            validateAdmin(owner);


            Activity newActivity = new Activity();
            newActivity.setName(activityDTO.getName());
            newActivity.setDescription(activityDTO.getDescription());
            newActivity.setDate(activityDTO.getDate());
            newActivity.setTypeActivity(TypeActivity.EVENT);
            newActivity.setOwnerActivity(owner);
            newActivity.setStatus(true);

            newActivity = activityRepository.save(newActivity);

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

            validateEvent(fromActivity);

            if (activityDTO.getQuota() < 1) {
                throw new ValidationException("El cupo debe ser mayor a 0");
            }

            Activity newActivity = new Activity();
            newActivity.setName(activityDTO.getName());
            newActivity.setDescription(activityDTO.getDescription());
            newActivity.setQuota(activityDTO.getQuota());
            newActivity.setSpeaker(activityDTO.getSpeaker());
            newActivity.setTime(activityDTO.getTime());
            newActivity.setTypeActivity(TypeActivity.WORKSHOP);
            newActivity.setFromActivity(fromActivity);
            newActivity.setStatus(true);

            activityRepository.save(newActivity);

            return new ResponseEntity<>(new Message(newActivity, ErrorMessages.SUCCESSFUL_REGISTRATION, TypesResponse.SUCCESS), HttpStatus.OK);

        } catch (ValidationException e) {
            return new ResponseEntity<>(new Message(e.getMessage(), TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(new Message(ErrorMessages.INTERNAL_SERVER_ERROR, TypesResponse.ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional(rollbackFor = {SQLException.class})
    public ResponseEntity<Message> updateEvent(ActivityDTO activityDTO) {
        try {
            Activity activity = activityRepository.findById(activityDTO.getId())
                    .orElseThrow(() -> new ValidationException(ErrorMessages.ACTIVITY_NOT_FOUND));
            validateEvent(activity);

            activity.setName(activityDTO.getName());
            activity.setDescription(activityDTO.getDescription());
            activity.setDate(activityDTO.getDate());

            activity = activityRepository.save(activity);

            return new ResponseEntity<>(new Message(activity, ErrorMessages.SUCCESFUL_UPDATE, TypesResponse.SUCCESS), HttpStatus.OK);

        } catch (ValidationException e) {
            return new ResponseEntity<>(new Message(e.getMessage(), TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(new Message(ErrorMessages.INTERNAL_SERVER_ERROR, TypesResponse.ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional(rollbackFor = {SQLException.class})
    public ResponseEntity<Message> updateWorkshop(ActivityDTO activityDTO) {
        try {
            Activity activity = activityRepository.findById(activityDTO.getId())
                    .orElseThrow(() -> new ValidationException(ErrorMessages.ACTIVITY_NOT_FOUND));
            validateWorkshop(activity);

            if (activityDTO.getQuota() < 1) {
                throw new ValidationException("El cupo debe ser mayor a 0");
            }

            activity.setName(activityDTO.getName());
            activity.setDescription(activityDTO.getDescription());
            activity.setQuota(activityDTO.getQuota());
            activity.setDate(activityDTO.getDate());

            activity = activityRepository.save(activity);

            return new ResponseEntity<>(new Message(activity, ErrorMessages.SUCCESFUL_UPDATE, TypesResponse.SUCCESS), HttpStatus.OK);

        } catch (ValidationException e) {
            return new ResponseEntity<>(new Message(e.getMessage(), TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(new Message(ErrorMessages.INTERNAL_SERVER_ERROR, TypesResponse.ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional(rollbackFor = {SQLException.class})
    public ResponseEntity<Message> changeStatus(ActivityDTO activityDTO) {
        try {
            Activity activity = activityRepository.findById(activityDTO.getId())
                    .orElseThrow(() -> new ValidationException(ErrorMessages.ACTIVITY_NOT_FOUND));

            if (!activity.getTypeActivity().equals(TypeActivity.EVENT) && !activity.getTypeActivity().equals(TypeActivity.WORKSHOP)) {
                throw new ValidationException("Solo se puede cambiar el estado de eventos o talleres");
            }

            boolean newStatus = !activity.isStatus();
            activity.setStatus(newStatus);

            String statusMessage = newStatus ? "Activo" : "Inactivo";
            String successMessage = ErrorMessages.SUCCESFUL_CHANGE_STATUS + statusMessage;

            activity = activityRepository.saveAndFlush(activity);

            return new ResponseEntity<>(new Message(activity, successMessage, TypesResponse.SUCCESS), HttpStatus.OK);

        } catch (ValidationException e) {
            return new ResponseEntity<>(new Message(e.getMessage(), TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(new Message(ErrorMessages.INTERNAL_SERVER_ERROR, TypesResponse.ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
