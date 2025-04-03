package utez.edu.ApiRestEventFlow.assignment.control;

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
import utez.edu.ApiRestEventFlow.assignment.model.Assignment;
import utez.edu.ApiRestEventFlow.assignment.model.AssignmentDTO;
import utez.edu.ApiRestEventFlow.assignment.model.AssignmentRepository;
import utez.edu.ApiRestEventFlow.user.model.User;
import utez.edu.ApiRestEventFlow.user.model.UserDTO;
import utez.edu.ApiRestEventFlow.user.model.UserRepository;
import utez.edu.ApiRestEventFlow.utils.Message;
import utez.edu.ApiRestEventFlow.utils.TypesResponse;
import utez.edu.ApiRestEventFlow.validation.ErrorMessages;

import java.sql.SQLException;
import java.util.List;

@Service
public class AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final UserRepository userRepository;
    private final ActivityRepository activityRepository;

    @Autowired
    public AssignmentService(AssignmentRepository assignmentRepository, UserRepository userRepository, ActivityRepository activityRepository) {
        this.assignmentRepository = assignmentRepository;
        this.userRepository = userRepository;
        this.activityRepository = activityRepository;
    }

    private void validateChecker(User user) {
        if (!user.getRole().equals(Role.CHECKER)) {
            throw new ValidationException(ErrorMessages.IS_NOT_CHECKER);
        }
    }

    private void validateAdmin(User owner) {
        if (!owner.getRole().equals(Role.ADMIN)) {
            throw new ValidationException(ErrorMessages.IS_NOT_ADMIM);
        }
    }

    @Transactional(readOnly = true)
    public ResponseEntity<Message> findAll() {
        try {
            List<Assignment> assignments = assignmentRepository.findAll();
            if (assignments.isEmpty()) {
                return new ResponseEntity<>(new Message(ErrorMessages.ASSIGNMENTS_NOT_FOUND, TypesResponse.WARNING), HttpStatus.OK);
            }
            return new ResponseEntity<>(new Message(assignments, "Lista de asignaciones", TypesResponse.SUCCESS), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new Message(ErrorMessages.INTERNAL_SERVER_ERROR, TypesResponse.ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional(readOnly = true)
    public ResponseEntity<Message> findByEvent(AssignmentDTO assignmentDTO) {
        try {

            if (activityRepository.findById(assignmentDTO.getActivityId()).isEmpty()) {
                throw new ValidationException(ErrorMessages.ACTIVITY_NOT_FOUND);
            }
            // Buscar asignaciones por ID de actividad
            List<Assignment> assignments = assignmentRepository.findByActivityId(assignmentDTO.getActivityId());

            if (assignments.isEmpty()) {
                return new ResponseEntity<>(new Message(ErrorMessages.ASSIGNMENTS_NOT_FOUND, TypesResponse.WARNING), HttpStatus.OK);
            }

            return new ResponseEntity<>(new Message(assignments, "Lista de asignaciones", TypesResponse.SUCCESS), HttpStatus.OK);
        } catch (ValidationException e) {
            return new ResponseEntity<>(new Message(e.getMessage(), TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(new Message(ErrorMessages.INTERNAL_SERVER_ERROR, TypesResponse.ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional(readOnly = true)
    public ResponseEntity<Message> findByChecker(AssignmentDTO assignmentDTO) {
        try {

            User checker = userRepository.findById(assignmentDTO.getUserId())
                    .orElseThrow(() -> new ValidationException(ErrorMessages.USER_NOT_FOUND));

            validateChecker(checker);

            List <Assignment> assignments = assignmentRepository.findByUserId(assignmentDTO.getUserId());

            if (assignments.isEmpty()) {
                return new ResponseEntity<>(new Message(ErrorMessages.ASSIGNMENTS_NOT_FOUND, TypesResponse.WARNING), HttpStatus.OK);
            }

            return new ResponseEntity<>(new Message(assignments, "Lista de asignaciones", TypesResponse.SUCCESS), HttpStatus.OK);
        } catch (ValidationException e) {
            return new ResponseEntity<>(new Message(e.getMessage(), TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(new Message(ErrorMessages.INTERNAL_SERVER_ERROR, TypesResponse.ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional(readOnly = true)
    public ResponseEntity<Message> findEventByOwnerId(Long id) {
        try {
            // Validar que el owner existe y es ADMIN
            User owner = userRepository.findById(id)
                    .orElseThrow(() -> new ValidationException(ErrorMessages.SENT_BY_USER_NOT_FOUND));
            validateAdmin(owner);

            // Obtener las actividades de tipo 'EVENT' del ownerId
            List<Activity> events = activityRepository.findEventsByOwner(id);

            // Si no se encontraron eventos
            if (events.isEmpty()) {
                return new ResponseEntity<>(new Message(ErrorMessages.ACTIVITIES_NOT_FOUND, TypesResponse.WARNING), HttpStatus.OK);
            }

            // Buscar las asignaciones para las actividades obtenidas
            List<Assignment> assignments = assignmentRepository.findByOwnerAndActivityIn(id, events);

            // Si no se encontraron asignaciones
            if (assignments.isEmpty()) {
                return new ResponseEntity<>(new Message(ErrorMessages.ASSIGNMENTS_NOT_FOUND, TypesResponse.WARNING), HttpStatus.OK);
            }

            return new ResponseEntity<>(new Message(assignments, "Lista de asignaciones para el owner", TypesResponse.SUCCESS), HttpStatus.OK);

        } catch (ValidationException e) {
            return new ResponseEntity<>(new Message(e.getMessage(), TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(new Message(ErrorMessages.INTERNAL_SERVER_ERROR, TypesResponse.ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional(readOnly = true)
    public ResponseEntity<Message> findWorkshopByOwnerId(Long id) {
        try {
            // Validar que el owner existe y es ADMIN
            User owner = userRepository.findById(id)
                    .orElseThrow(() -> new ValidationException(ErrorMessages.SENT_BY_USER_NOT_FOUND));
            validateAdmin(owner);

            // Obtener las actividades de tipo 'EVENT' del ownerId
            List<Activity> workshops = activityRepository.findWorkshopsByOwner(id);

            // Si no se encontraron eventos
            if (workshops.isEmpty()) {
                return new ResponseEntity<>(new Message(ErrorMessages.ACTIVITIES_NOT_FOUND, TypesResponse.WARNING), HttpStatus.OK);
            }

            // Buscar las asignaciones para las actividades obtenidas
            List<Assignment> assignments = assignmentRepository.findByOwnerAndActivityIn(id, workshops);

            // Si no se encontraron asignaciones
            if (assignments.isEmpty()) {
                return new ResponseEntity<>(new Message(ErrorMessages.ASSIGNMENTS_NOT_FOUND, TypesResponse.WARNING), HttpStatus.OK);
            }

            return new ResponseEntity<>(new Message(assignments, "Lista de asignaciones para el owner", TypesResponse.SUCCESS), HttpStatus.OK);

        } catch (ValidationException e) {
            return new ResponseEntity<>(new Message(e.getMessage(), TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(new Message(ErrorMessages.INTERNAL_SERVER_ERROR, TypesResponse.ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }





    public ResponseEntity<Message> saveAssignment(AssignmentDTO assignmentDTO) {
        try {
            User checker = userRepository.findById(assignmentDTO.getUserId())
                    .orElseThrow(() -> new ValidationException(ErrorMessages.USER_NOT_FOUND));

            Activity activityAssignment = activityRepository.findById(assignmentDTO.getActivityId())
                    .orElseThrow(() -> new ValidationException(ErrorMessages.ACTIVITY_NOT_FOUND));

            validateChecker(checker);

            Assignment newAssignment = new Assignment();
            newAssignment.setUser(checker);
            newAssignment.setActivity(activityAssignment);
            newAssignment.setOwner(activityAssignment.getOwnerActivity().getId());

            Assignment savedAssignment = assignmentRepository.save(newAssignment);

            AssignmentDTO responseDTO = new AssignmentDTO(
                    savedAssignment.getId(),
                    savedAssignment.getOwner(),
                    savedAssignment.getUser().getId(),
                    savedAssignment.getActivity().getId()
            );

            return new ResponseEntity<>(
                    new Message(responseDTO, ErrorMessages.SUCCESSFUL_REGISTRATION, TypesResponse.SUCCESS),
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

    @Transactional(rollbackFor = {SQLException.class})
    public ResponseEntity<Message> changeStatus(AssignmentDTO assignmentDTO) {
        try {

            Assignment assignment = assignmentRepository.findById(assignmentDTO.getAssignmentId())
                    .orElseThrow(() -> new ValidationException(ErrorMessages.ASSIGNMENTS_NOT_FOUND));

            boolean newStatus = !assignment.isStatus();
            assignment.setStatus(newStatus);

            String statusMessage = newStatus ? "Activo" : "Inactivo";
            String successMessage = ErrorMessages.SUCCESFUL_CHANGE_STATUS + statusMessage;

            assignment = assignmentRepository.save(assignment);

            return new ResponseEntity<>(new Message(assignment, successMessage, TypesResponse.SUCCESS), HttpStatus.OK);

        } catch (ValidationException e) {
            return new ResponseEntity<>(new Message(e.getMessage(), TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(new Message(ErrorMessages.INTERNAL_SERVER_ERROR, TypesResponse.ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
