package utez.edu.ApiRestEventFlow.user.control;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import utez.edu.ApiRestEventFlow.user.model.UserDTO;
import utez.edu.ApiRestEventFlow.utils.Message;

@RestController
@RequestMapping("/user")
@CrossOrigin(origins = {"*"}, methods = {RequestMethod.POST, RequestMethod.GET, RequestMethod.PUT, RequestMethod.DELETE})
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/all")
    public ResponseEntity<Message> getAllUsers() {
        return userService.findAll();
    }

    @GetMapping("/allAdmins")
    public ResponseEntity<Message> getAllAdmins() {
        return userService.findAllAdmins();
    }

    @PostMapping("/saveAdmin")
    public ResponseEntity<Message> saveAdmin(@RequestBody UserDTO userDTO) {
        return userService.saveAdmin(userDTO);
    }

}
