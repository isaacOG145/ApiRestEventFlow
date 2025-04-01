package utez.edu.ApiRestEventFlow.cloud;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/images")
public class ImageController {

    @Autowired
    private CloudinaryService cloudinaryService;

    @PostMapping("/upload")
    public String uploadImage(@RequestParam("file") MultipartFile file) throws IOException {
        return cloudinaryService.uploadImage(file);
    }

    @DeleteMapping("/delete/{publicId}")
    public void deleteImage(@PathVariable String publicId) throws IOException {
        cloudinaryService.deleteImage(publicId);
    }
}