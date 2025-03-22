package utez.edu.ApiRestEventFlow.cloud;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import utez.edu.ApiRestEventFlow.activity.model.Activity;
import utez.edu.ApiRestEventFlow.activity.model.ActivityRepository;
import utez.edu.ApiRestEventFlow.utils.Message;
import utez.edu.ApiRestEventFlow.utils.TypesResponse;
import utez.edu.ApiRestEventFlow.validation.ErrorMessages;
import utez.edu.ApiRestEventFlow.validation.ValidationException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ActivityImageService {

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private Cloudinary cloudinary;

    // Agregar imágenes a un evento
    @Transactional
    public ResponseEntity<Message> addImages(Long activityId, List<MultipartFile> images) {
        try {
            // Buscar el evento por ID
            Activity activity = activityRepository.findById(activityId)
                    .orElseThrow(() -> new ValidationException(ErrorMessages.ACTIVITY_NOT_FOUND));

            // Subir imágenes a Cloudinary y guardar las URLs
            List<String> imageUrls = new ArrayList<>();
            for (MultipartFile image : images) {
                Map uploadResult = cloudinary.uploader().upload(image.getBytes(), ObjectUtils.emptyMap());
                imageUrls.add(uploadResult.get("url").toString());
            }

            // Agregar las nuevas URLs a la lista de imágenes del evento
            activity.getImageUrls().addAll(imageUrls);

            // Guardar el evento actualizado
            activityRepository.save(activity);

            return new ResponseEntity<>(new Message(activity, "Imágenes agregadas correctamente", TypesResponse.SUCCESS), HttpStatus.OK);
        } catch (ValidationException e) {
            return new ResponseEntity<>(new Message(e.getMessage(), TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(new Message("Error al agregar imágenes", TypesResponse.ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Eliminar imágenes de un evento
    @Transactional
    public ResponseEntity<Message> deleteImages(Long eventId, List<String> imageUrls) {
        try {
            // Buscar el evento por ID
            Activity event = activityRepository.findById(eventId)
                    .orElseThrow(() -> new ValidationException("Evento no encontrado"));

            // Eliminar imágenes de Cloudinary
            for (String imageUrl : imageUrls) {
                String publicId = extractPublicIdFromUrl(imageUrl); // Extraer el public_id de la URL
                cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            }

            // Eliminar las URLs de la lista de imágenes del evento
            event.getImageUrls().removeAll(imageUrls);

            // Guardar el evento actualizado
            activityRepository.save(event);

            return new ResponseEntity<>(new Message(event, "Imágenes eliminadas correctamente", TypesResponse.SUCCESS), HttpStatus.OK);
        } catch (ValidationException e) {
            return new ResponseEntity<>(new Message(e.getMessage(), TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(new Message("Error al eliminar imágenes", TypesResponse.ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Actualizar imágenes de un evento
    @Transactional
    public ResponseEntity<Message> updateImages(Long eventId, List<MultipartFile> newImages) {
        try {
            // Buscar el evento por ID
            Activity event = activityRepository.findById(eventId)
                    .orElseThrow(() -> new ValidationException("Evento no encontrado"));

            // Eliminar las imágenes antiguas de Cloudinary
            for (String oldImageUrl : event.getImageUrls()) {
                String publicId = extractPublicIdFromUrl(oldImageUrl); // Extraer el public_id de la URL
                cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            }

            // Subir las nuevas imágenes a Cloudinary
            List<String> newImageUrls = new ArrayList<>();
            for (MultipartFile image : newImages) {
                Map uploadResult = cloudinary.uploader().upload(image.getBytes(), ObjectUtils.emptyMap());
                newImageUrls.add(uploadResult.get("url").toString());
            }

            // Reemplazar las URLs de las imágenes del evento
            event.setImageUrls(newImageUrls);

            // Guardar el evento actualizado
            activityRepository.save(event);

            return new ResponseEntity<>(new Message(event, "Imágenes actualizadas correctamente", TypesResponse.SUCCESS), HttpStatus.OK);
        } catch (ValidationException e) {
            return new ResponseEntity<>(new Message(e.getMessage(), TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(new Message("Error al actualizar imágenes", TypesResponse.ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Método para extraer el public_id de la URL de Cloudinary
    private String extractPublicIdFromUrl(String imageUrl) {
        // La URL de Cloudinary tiene el formato: https://res.cloudinary.com/cloud_name/image/upload/public_id
        String[] parts = imageUrl.split("/upload/");
        return parts[1].split("\\.")[0]; // Eliminar la extensión del archivo
    }
}