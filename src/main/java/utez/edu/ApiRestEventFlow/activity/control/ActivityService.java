package utez.edu.ApiRestEventFlow.activity.control;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import jakarta.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import utez.edu.ApiRestEventFlow.Role.Role;
import utez.edu.ApiRestEventFlow.Role.TypeActivity;
import utez.edu.ApiRestEventFlow.activity.model.*;
import utez.edu.ApiRestEventFlow.user.model.User;
import utez.edu.ApiRestEventFlow.user.model.UserRepository;
import utez.edu.ApiRestEventFlow.userActivity.model.UserActivity;
import utez.edu.ApiRestEventFlow.userActivity.model.UserActivityRepository;
import utez.edu.ApiRestEventFlow.utils.Message;
import utez.edu.ApiRestEventFlow.utils.TypesResponse;
import utez.edu.ApiRestEventFlow.validation.ErrorMessages;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ActivityService {

    private final ActivityRepository activityRepository;
    private final UserRepository userRepository;
    private final UserActivityRepository userActivityRepository;

    @Autowired
    private Cloudinary cloudinary;

    @Autowired
    public ActivityService(ActivityRepository activityRepository, UserRepository userRepository, UserActivityRepository userActivityRepository) {
        this.activityRepository = activityRepository;
        this.userRepository = userRepository;
        this.userActivityRepository = userActivityRepository;
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

    private void validateImg(ActivityDTO activityDTO) {
        if (activityDTO.getImages() != null && !activityDTO.getImages().isEmpty()) {
            for (MultipartFile image : activityDTO.getImages()) {
                if (image.getSize() > 10 * 1024 * 1024) { // 10 MB
                    throw new ValidationException("El tamaño de la imagen no debe exceder 10 MB");
                }

            }
        }
    }

    // Método para extraer el public_id de la URL de Cloudinary
    private String extractPublicIdFromUrl(String imageUrl) {
        // La URL de Cloudinary tiene el formato: https://res.cloudinary.com/cloud_name/image/upload/v<version>/public_id.jpg
        // Dividir la URL para obtener la parte con el public_id
        String[] parts = imageUrl.split("/upload/");

        if (parts.length > 1) {
            // Obtener la parte después de "/upload/" y eliminar la extensión del archivo
            String publicIdWithVersion = parts[1].split("\\?")[0]; // Eliminar parámetros si existen
            String[] publicIdParts = publicIdWithVersion.split("\\.");

            return publicIdParts[0]; // Devolver solo el public_id sin la extensión
        }

        return null;
    }



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
            List<Activity> activities = activityRepository.findActiveEvents();  // Usamos el método que obtiene solo eventos activos
            if (activities.isEmpty()) {
                return new ResponseEntity<>(new Message(ErrorMessages.ACTIVITIES_NOT_FOUND, TypesResponse.WARNING), HttpStatus.OK);
            }
            return new ResponseEntity<>(new Message(activities, "Lista de eventos activos", TypesResponse.SUCCESS), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new Message(ErrorMessages.INTERNAL_SERVER_ERROR, TypesResponse.ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional(readOnly = true)
    public ResponseEntity<Message> findAllWorkshops() {
        try {
            List<Activity> activities = activityRepository.findActiveWorkshops(); // Usamos el método que obtiene solo talleres activos
            if (activities.isEmpty()) {
                return new ResponseEntity<>(new Message(ErrorMessages.ACTIVITIES_NOT_FOUND, TypesResponse.WARNING), HttpStatus.OK);
            }
            return new ResponseEntity<>(new Message(activities, "Lista de talleres activos", TypesResponse.SUCCESS), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new Message(ErrorMessages.INTERNAL_SERVER_ERROR, TypesResponse.ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional(readOnly = true)
    public ResponseEntity<Message> findAllActiveByOwner(Long ownerId) {
        try{
            // Validar que el owner existe y es ADMIN
            User owner = userRepository.findById(ownerId)
                    .orElseThrow(() -> new ValidationException(ErrorMessages.SENT_BY_USER_NOT_FOUND));
            validateAdmin(owner);

            List<Activity> activities = activityRepository.findByOwnerActivityIdAndActive(ownerId);

            if (activities.isEmpty()) {
                return new ResponseEntity<>(
                        new Message(ErrorMessages.ACTIVITIES_NOT_FOUND, TypesResponse.WARNING),
                        HttpStatus.OK
                );
            }

            return new ResponseEntity<>(
                    new Message(activities, "Talleres encontrados", TypesResponse.SUCCESS),
                    HttpStatus.OK
            );



        }catch (ValidationException e) {
            return new ResponseEntity<>(
                    new Message(e.getMessage(), TypesResponse.WARNING),
                    HttpStatus.BAD_REQUEST
            );
        } catch (Exception e) {
            return new ResponseEntity<>(
                    new Message("Error interno del servidor", TypesResponse.ERROR),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    @Transactional(readOnly = true)
    public ResponseEntity<Message> findById(Long id) {
        try {
            // Buscar el evento por su ID
            Activity activity = activityRepository.findById(id)
                    .orElseThrow(() -> new ValidationException("Evento no encontrado"));

            return new ResponseEntity<>(
                    new Message(activity, "Evento encontrado", TypesResponse.SUCCESS),
                    HttpStatus.OK
            );
        } catch (ValidationException e) {
            return new ResponseEntity<>(
                    new Message(e.getMessage(), TypesResponse.WARNING),
                    HttpStatus.BAD_REQUEST
            );
        } catch (Exception e) {
            return new ResponseEntity<>(
                    new Message("Error interno del servidor", TypesResponse.ERROR),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    @Transactional(readOnly = true)
    public ResponseEntity<Message> findActivitiesByUserAssignments(Long  userId) {
        try{
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ValidationException(ErrorMessages.USER_NOT_FOUND));

            List<Activity> activities = activityRepository.findActivitiesByUserAssignments(userId);

            return new ResponseEntity<>(
                    new Message(activities, "Eventos encontrado", TypesResponse.SUCCESS),
                    HttpStatus.OK
            );

        }catch (ValidationException e) {
            return new ResponseEntity<>(
                    new Message(e.getMessage(), TypesResponse.WARNING),
                    HttpStatus.BAD_REQUEST
            );
        } catch (Exception e) {
            return new ResponseEntity<>(
                    new Message("Error interno del servidor", TypesResponse.ERROR),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    @Transactional(readOnly = true)
    public ResponseEntity<Message> findByEventId(Long eventId) {
        try {
            // Buscar el evento por su ID
            Activity event = activityRepository.findById(eventId)
                    .orElseThrow(() -> new ValidationException("Evento no encontrado"));

            // Verificar si el evento es del tipo EVENT
            if (!event.getTypeActivity().equals(TypeActivity.EVENT)) {
                return new ResponseEntity<>(
                        new Message("El evento no es del tipo correcto", TypesResponse.WARNING),
                        HttpStatus.BAD_REQUEST
                );
            }

            return new ResponseEntity<>(
                    new Message(event, "Evento encontrado", TypesResponse.SUCCESS),
                    HttpStatus.OK
            );
        } catch (ValidationException e) {
            return new ResponseEntity<>(
                    new Message(e.getMessage(), TypesResponse.WARNING),
                    HttpStatus.BAD_REQUEST
            );
        } catch (Exception e) {
            return new ResponseEntity<>(
                    new Message("Error interno del servidor", TypesResponse.ERROR),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    @Transactional(readOnly = true)
    public ResponseEntity<Message> findWorkShopById(Long workshopId) {
        try {
            // Buscar el evento por su ID
            Activity event = activityRepository.findById(workshopId)
                    .orElseThrow(() -> new ValidationException("Evento no encontrado"));

            // Verificar si el evento es del tipo EVENT
            if (!event.getTypeActivity().equals(TypeActivity.WORKSHOP)) {
                return new ResponseEntity<>(
                        new Message("El evento no es del tipo correcto", TypesResponse.WARNING),
                        HttpStatus.BAD_REQUEST
                );
            }

            return new ResponseEntity<>(
                    new Message(event, "Taller encontrado", TypesResponse.SUCCESS),
                    HttpStatus.OK
            );
        } catch (ValidationException e) {
            return new ResponseEntity<>(
                    new Message(e.getMessage(), TypesResponse.WARNING),
                    HttpStatus.BAD_REQUEST
            );
        } catch (Exception e) {
            return new ResponseEntity<>(
                    new Message("Error interno del servidor", TypesResponse.ERROR),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }


    @Transactional(readOnly = true)
    public ResponseEntity<Message> findEventsByOwner(Long ownerId) {
        try {
            // Validar que el owner existe y es ADMIN
            User owner = userRepository.findById(ownerId)
                    .orElseThrow(() -> new ValidationException(ErrorMessages.SENT_BY_USER_NOT_FOUND));
            validateAdmin(owner);

            // Buscar solo eventos (TypeActivity.EVENT)
            List<Activity> events = activityRepository.findEventsByOwner(ownerId);

            if (events.isEmpty()) {
                return new ResponseEntity<>(
                        new Message(ErrorMessages.ACTIVITIES_NOT_FOUND, TypesResponse.WARNING),
                        HttpStatus.OK
                );
            }

            return new ResponseEntity<>(
                    new Message(events, "Eventos encontrados", TypesResponse.SUCCESS),
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
    public ResponseEntity<Message> findWorkshopsByOwner(Long ownerId) {
        try {
            // Validar que el owner existe y es ADMIN
            User owner = userRepository.findById(ownerId)
                    .orElseThrow(() -> new ValidationException(ErrorMessages.SENT_BY_USER_NOT_FOUND));
            validateAdmin(owner);

            // Buscar solo talleres (TypeActivity.WORKSHOP)
            List<Activity> workshops = activityRepository.findWorkshopsByOwner(ownerId);

            if (workshops.isEmpty()) {
                return new ResponseEntity<>(
                        new Message(ErrorMessages.ACTIVITIES_NOT_FOUND, TypesResponse.WARNING),
                        HttpStatus.OK
                );
            }

            return new ResponseEntity<>(
                    new Message(workshops, "Talleres encontrados", TypesResponse.SUCCESS),
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
    public ResponseEntity<Message> findByFromActivity(Long id) {
        try {
            Activity fromActivity = activityRepository.findById(id)
                    .orElseThrow(() -> new ValidationException(ErrorMessages.ACTIVITY_NOT_FOUND));
            validateEvent(fromActivity);

            List<Activity> activities = activityRepository.findByFromActivity_Id(id);

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
            validateImg(activityDTO);

            Activity newActivity = new Activity();
            newActivity.setName(activityDTO.getName());
            newActivity.setDescription(activityDTO.getDescription());
            newActivity.setDate(activityDTO.getDate());
            newActivity.setTypeActivity(TypeActivity.EVENT);
            newActivity.setOwnerActivity(owner);
            newActivity.setStatus(true);

            // Subir imágenes a Cloudinary y guardar las URLs
            if (activityDTO.getImages() != null && !activityDTO.getImages().isEmpty()) {
                List<String> imageUrls = new ArrayList<>();
                for (MultipartFile image : activityDTO.getImages()) {
                    Map uploadResult = cloudinary.uploader().upload(image.getBytes(), ObjectUtils.emptyMap());
                    imageUrls.add(uploadResult.get("url").toString());
                }
                newActivity.setImageUrls(imageUrls);
            }

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
            // 1. Validar y obtener el evento padre
            Activity fromActivity = activityRepository.findById(activityDTO.getFromActivity().getId())
                    .orElseThrow(() -> new ValidationException(ErrorMessages.ACTIVITY_NOT_FOUND));

            validateEvent(fromActivity); // Asegura que fromActivity es un EVENT

            // 2. Validaciones adicionales
            if (activityDTO.getQuota() < 1) {
                throw new ValidationException("El cupo debe ser mayor a 0");
            }

            // 3. Crear el nuevo taller
            Activity newActivity = new Activity();
            newActivity.setName(activityDTO.getName());
            newActivity.setDescription(activityDTO.getDescription());
            newActivity.setQuota(activityDTO.getQuota());
            newActivity.setSpeaker(activityDTO.getSpeaker());
            newActivity.setTime(activityDTO.getTime());
            newActivity.setTypeActivity(TypeActivity.WORKSHOP);
            newActivity.setFromActivity(fromActivity);

            // 4. Asignar el mismo dueño y fecha que el evento padre
            newActivity.setOwnerActivity(fromActivity.getOwnerActivity());

            newActivity.setStatus(true);

            // 5. Subir imágenes a Cloudinary
            if (activityDTO.getImages() != null && !activityDTO.getImages().isEmpty()) {
                List<String> imageUrls = new ArrayList<>();
                for (MultipartFile image : activityDTO.getImages()) {
                    Map uploadResult = cloudinary.uploader().upload(image.getBytes(), ObjectUtils.emptyMap());
                    imageUrls.add(uploadResult.get("url").toString());
                }
                newActivity.setImageUrls(imageUrls);
            }

            // 6. Guardar y retornar respuesta
            newActivity = activityRepository.save(newActivity);
            return new ResponseEntity<>(
                    new Message(newActivity, ErrorMessages.SUCCESSFUL_REGISTRATION, TypesResponse.SUCCESS),
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
    public ResponseEntity<Message> updateEvent(ActivityDTO activityDTO, List<MultipartFile> newImages) {
        try {
            Activity activity = activityRepository.findById(activityDTO.getId())
                    .orElseThrow(() -> new ValidationException(ErrorMessages.ACTIVITY_NOT_FOUND));

            validateEvent(activity);

            // Actualización de campos básicos
            if (activityDTO.getName() != null) activity.setName(activityDTO.getName());
            if (activityDTO.getDescription() != null) activity.setDescription(activityDTO.getDescription());
            if (activityDTO.getDate() != null) activity.setDate(activityDTO.getDate());
            if (activityDTO.getSpeaker() != null) activity.setSpeaker(activityDTO.getSpeaker());

            // 1. Inicializar con imágenes existentes que se conservan
            List<String> finalImageUrls = new ArrayList<>(activityDTO.getExistingImages());

            // 2. Eliminar imágenes marcadas para borrado
            if (activityDTO.getDeletedImages() != null) {
                for (String imageToDelete : activityDTO.getDeletedImages()) {
                    try {
                        String publicId = extractPublicIdFromUrl(imageToDelete);
                        if (publicId != null) {
                            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
                        }
                    } catch (Exception e) {
                        // Continuar aunque falle la eliminación en Cloudinary
                    }
                }
            }

            // 3. Agregar nuevas imágenes
            if (newImages != null && !newImages.isEmpty()) {
                for (MultipartFile image : newImages) {
                    try {
                        Map uploadResult = cloudinary.uploader().upload(image.getBytes(), ObjectUtils.emptyMap());
                        finalImageUrls.add(uploadResult.get("url").toString());
                    } catch (Exception e) {

                        throw new ValidationException("Error al procesar las imágenes nuevas");
                    }
                }
            }

            // Validar que haya al menos una imagen
            if (finalImageUrls.isEmpty()) {
                throw new ValidationException("El evento debe tener al menos una imagen");
            }

            activity.setImageUrls(finalImageUrls);
            activity = activityRepository.save(activity);

            return new ResponseEntity<>(new Message(activity, ErrorMessages.SUCCESFUL_UPDATE, TypesResponse.SUCCESS), HttpStatus.OK);

        } catch (ValidationException e) {
            return new ResponseEntity<>(new Message(e.getMessage(), TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(new Message(ErrorMessages.INTERNAL_SERVER_ERROR, TypesResponse.ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional(rollbackFor = {SQLException.class})
    public ResponseEntity<Message> updateWorkshop(ActivityDTO activityDTO, List<MultipartFile> newImages) {
        try {
            // Buscar la actividad existente en la base de datos
            Activity activity = activityRepository.findById(activityDTO.getId())
                    .orElseThrow(() -> new ValidationException(ErrorMessages.ACTIVITY_NOT_FOUND));

            // Validar que la actividad es un taller
            validateWorkshop(activity);

            // Actualizar los campos básicos si se proporciona algún valor en el DTO
            if (activityDTO.getName() != null) {
                activity.setName(activityDTO.getName());
            }
            if (activityDTO.getDescription() != null) {
                activity.setDescription(activityDTO.getDescription());
            }
            if (activityDTO.getTime() != null) {
                activity.setTime(activityDTO.getTime());
            }
            if (activityDTO.getSpeaker() != null) {
                activity.setSpeaker(activityDTO.getSpeaker());
            }
            if (activityDTO.getQuota() != null) {
                if (activityDTO.getQuota() < 1) {
                    throw new ValidationException("El cupo debe ser mayor a 0");
                }
                activity.setQuota(activityDTO.getQuota());
            }

            // 1. Inicializar con imágenes existentes que se conservan
            List<String> finalImageUrls = new ArrayList<>(activityDTO.getExistingImages());

            // 2. Eliminar imágenes marcadas para borrado
            if (activityDTO.getDeletedImages() != null) {
                for (String imageToDelete : activityDTO.getDeletedImages()) {
                    try {
                        String publicId = extractPublicIdFromUrl(imageToDelete);
                        if (publicId != null) {
                            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
                        }
                    } catch (Exception e) {
                        // Continuar aunque falle la eliminación en Cloudinary
                    }
                }
            }

            // 3. Agregar nuevas imágenes
            if (newImages != null && !newImages.isEmpty()) {
                for (MultipartFile image : newImages) {
                    try {
                        Map uploadResult = cloudinary.uploader().upload(image.getBytes(), ObjectUtils.emptyMap());
                        finalImageUrls.add(uploadResult.get("url").toString());
                    } catch (Exception e) {

                        throw new ValidationException("Error al procesar las imágenes nuevas");
                    }
                }
            }

            // Validar que haya al menos una imagen
            if (finalImageUrls.isEmpty()) {
                throw new ValidationException("El evento debe tener al menos una imagen");
            }

            activity.setImageUrls(finalImageUrls);
            activity = activityRepository.save(activity);





            // Retornar respuesta exitosa
            return new ResponseEntity<>(new Message(activity, ErrorMessages.SUCCESFUL_UPDATE, TypesResponse.SUCCESS), HttpStatus.OK);

        } catch (ValidationException e) {
            // Manejar excepciones de validación
            return new ResponseEntity<>(new Message(e.getMessage(), TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            // Manejar excepciones inesperadas
            return new ResponseEntity<>(new Message(ErrorMessages.INTERNAL_SERVER_ERROR, TypesResponse.ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional(readOnly = true)
    public ResponseEntity<Message> findAssignedStatusByOwner(Long ownerId) {
        try {
            User owner = userRepository.findById(ownerId)
                    .orElseThrow(() -> new ValidationException(ErrorMessages.SENT_BY_USER_NOT_FOUND));
            validateAdmin(owner);

            List<Object[]> rawData = activityRepository.findActivityAssignmentStatusByOwner(ownerId);

            if (rawData.isEmpty()) {
                return new ResponseEntity<>(
                        new Message(ErrorMessages.ACTIVITIES_NOT_FOUND, TypesResponse.WARNING),
                        HttpStatus.OK
                );
            }

            List<ActivityAssignmentDTO> dtos = rawData.stream()
                    .map(row -> new ActivityAssignmentDTO((Long) row[0], (Boolean) row[1]))
                    .toList();

            return new ResponseEntity<>(
                    new Message(dtos, "Estado de asignaciones encontrado", TypesResponse.SUCCESS),
                    HttpStatus.OK
            );

        } catch (ValidationException e) {
            return new ResponseEntity<>(new Message(e.getMessage(), TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(new Message(ErrorMessages.INTERNAL_SERVER_ERROR, TypesResponse.ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional(readOnly = true)
    public ResponseEntity<Message> findInscriptionsByUserId(Long userId) {
        try{
            // 1. Validar usuario existente y activo
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ValidationException(ErrorMessages.USER_NOT_FOUND));

            if (!user.isStatus()) {
                throw new ValidationException("El usuario está inactivo");
            }

            List<UserActivity> userActivities = userActivityRepository.findAllActivityInscriptionByUserId(userId,true);
            if (userActivities.isEmpty()) {
                return new ResponseEntity<>(
                        new Message("No esta registrado a eventos", TypesResponse.WARNING),
                        HttpStatus.OK
                );
            }
            // 3. Extraer las actividades desde las inscripciones
            List<Activity> activities = userActivities.stream()
                    .map(UserActivity::getActivity)
                    .filter(Activity::isStatus)    // <— aquí filtras por status == true
                    .toList();

            // 4. Devolver actividades en la respuesta
            return new ResponseEntity<>(
                    new Message(activities, "Inscripciones encontradas", TypesResponse.SUCCESS),
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

    @Transactional(readOnly = true)
    public ResponseEntity<Message> getWorkshopsForRegisteredUser(Long userId) {
        try {
            // 1. Validar usuario existente y activo
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ValidationException(ErrorMessages.USER_NOT_FOUND));

            if (!user.isStatus()) {
                throw new ValidationException("El usuario está inactivo");
            }

            // 2. Obtener eventos donde el usuario está registrado (activos y verificados)
            List<UserActivity> userEvents = userActivityRepository.findAllByUserIdAndStatusAndVerified(
                    userId,
                    true
            );

            if (userEvents.isEmpty()) {
                return new ResponseEntity<>(
                        new Message("No esta registrado a eventos", TypesResponse.WARNING),
                        HttpStatus.OK
                );
            }

            // 3. Obtener IDs de eventos y buscar talleres asociados
            List<Long> eventIds = userEvents.stream()
                    .map(ua -> ua.getActivity().getId())
                    .toList();

            List<Activity> workshops = activityRepository.findWorkshopsByEventIdsAndActive(eventIds);

            // 4. Construir respuesta con cupos disponibles
            List<WorkshopResponseDTO> response = workshops.stream()
                    .map(workshop -> {
                        int registered = userActivityRepository.countValidRegistrationsByActivityId (workshop.getId());
                        int available = workshop.getQuota() - registered;
                        return new WorkshopResponseDTO(workshop, available);
                    })
                    .toList();

            return new ResponseEntity<>(
                    new Message(response, "Talleres disponibles", TypesResponse.SUCCESS),
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
    public ResponseEntity<Message> changeStatus(Long activityId) {
        try {
            // Buscar la actividad por el ID
            Activity activity = activityRepository.findById(activityId)
                    .orElseThrow(() -> new ValidationException(ErrorMessages.ACTIVITY_NOT_FOUND));

            // Cambiar el estado
            boolean newStatus = !activity.isStatus();
            activity.setStatus(newStatus);

            // Crear mensaje de éxito
            String statusMessage = newStatus ? "Activo" : "Inactivo";
            String successMessage = ErrorMessages.SUCCESFUL_CHANGE_STATUS + statusMessage;

            // Guardar la actividad con el nuevo estado
            activity = activityRepository.saveAndFlush(activity);

            // Retornar respuesta exitosa
            return new ResponseEntity<>(new Message(activity, successMessage, TypesResponse.SUCCESS), HttpStatus.OK);

        } catch (ValidationException e) {
            return new ResponseEntity<>(new Message(e.getMessage(), TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(new Message(ErrorMessages.INTERNAL_SERVER_ERROR, TypesResponse.ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
