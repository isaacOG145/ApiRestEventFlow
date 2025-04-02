package utez.edu.ApiRestEventFlow.activity.control;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import utez.edu.ApiRestEventFlow.activity.model.ActivityDTO;
import utez.edu.ApiRestEventFlow.activity.model.ActivityRepository;
import utez.edu.ApiRestEventFlow.cloud.ActivityImageService;
import utez.edu.ApiRestEventFlow.user.model.UserDTO;
import utez.edu.ApiRestEventFlow.utils.Message;

import java.util.List;

@RestController
@RequestMapping("/activity")
@CrossOrigin(origins = {"*"}, methods = {RequestMethod.POST, RequestMethod.GET, RequestMethod.PUT, RequestMethod.DELETE})
public class ActivityController {

    private final ActivityService activityService;
    private final ActivityImageService activityImageService;

    @Autowired
    public ActivityController(ActivityService activityService, ActivityImageService activityImageService) {
        this.activityService = activityService;
        this.activityImageService = activityImageService;
    }

    //buscar todos
    @GetMapping("/findAll")
    public ResponseEntity<Message> getAllActivities() {
        return activityService.findAll();
    }
    //mostrar todos los eventos activos
    @GetMapping("/findActiveEvents")
    public ResponseEntity<Message> getAllEvents() {
        return activityService.findAllEvents();
    }
    //mostrar todos los talleres activos
    @GetMapping("/findActiveWorkshops")
    public ResponseEntity<Message> getAllWorkshops() {
        return activityService.findAllWorkshops();
    }

    //buscar eventos por id
    @GetMapping("event/findById/{id}")
    public ResponseEntity<Message> getEventById(@PathVariable Long id) {
        return activityService.findById(id);
    }
    //buscar talleres por id
    @GetMapping("workShop/findById/{id}")
    public ResponseEntity<Message> getWorkshopById(@PathVariable Long id) {
        return activityService.findWorkShopById(id);
    }
    //buscar todos los talleres de un usuario
    @GetMapping("/events/byOwner/{ownerId}")
    public ResponseEntity<Message> getEventsByOwner(@PathVariable Long ownerId) {
        return activityService.findEventsByOwner(ownerId);
    }
    //buscar todos los talleres de un usuario
    @GetMapping("/workshops/byOwner/{ownerId}")
    public ResponseEntity<Message> getWorkshopsByOwner(@PathVariable Long ownerId) {
        return activityService.findWorkshopsByOwner(ownerId);
    }
    //buscar talleres por evento asociado
    @GetMapping("/findByEvent/{id}")
    public ResponseEntity<Message> getByEvent(@PathVariable Long id) {
        return activityService.findByFromActivity(id);
    }

    @PostMapping("/saveEvent")
    public ResponseEntity<Message> saveEvent(
            @Valid @RequestPart("activity") ActivityDTO activityDTO, // Recibe el JSON
            @RequestPart(value = "images", required = false) List<MultipartFile> images) { // Recibe las imágenes
        activityDTO.setImages(images); // Asigna las imágenes al DTO
        return activityService.saveEvent(activityDTO);
    }

    @PostMapping("/saveWorkshop")
    public ResponseEntity<Message> saveWorkshop(
            @RequestPart("activity") ActivityDTO activityDTO,
            @RequestPart(value = "images", required = false) List<MultipartFile> images) {
        activityDTO.setImages(images); // Asignar las imágenes al DTO
        return activityService.saveWorkshop(activityDTO);
    }

    @PutMapping(value = "/updateEvent", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Message> updateEvent(
            @Validated(ActivityDTO.ModifyEvent.class) @RequestPart("activity") ActivityDTO activityDTO,
            @RequestPart(value = "images", required = false) List<MultipartFile> newImages) {

        return activityService.updateEvent(activityDTO, newImages);
    }

    @PutMapping("/updateWorkshop")
    public ResponseEntity<Message> updateWorkshop(
            @Validated @RequestPart("activity") ActivityDTO activityDTO,
            @RequestPart(value = "images", required = false) List<MultipartFile> newImages) {

        return activityService.updateWorkshop(activityDTO, newImages);
    }



    // Agregar imágenes a un evento
    @PostMapping("/{id}/addImages")
    public ResponseEntity<Message> addImages(
            @PathVariable Long id,
            @RequestPart(value = "images") List<MultipartFile> images) {
        return activityImageService.addImages(id, images);
    }

    // Eliminar imágenes de un evento
    @DeleteMapping("/{id}/deleteImages")
    public ResponseEntity<Message> deleteImages(
            @PathVariable Long id,
            @RequestBody List<String> imageUrls) {
        return activityImageService.deleteImages(id, imageUrls);
    }

    // Actualizar imágenes de un evento
    @PutMapping("/{id}/updateImages")
    public ResponseEntity<Message> updateImages(
            @PathVariable Long id,
            @RequestPart(value = "images") List<MultipartFile> images) {
        return activityImageService.updateImages(id, images);
    }

    @PutMapping("/change-status/{id}")
    public ResponseEntity<Message> changeStatus(@Validated @PathVariable Long id) {
        return activityService.changeStatus(id);
    }
}