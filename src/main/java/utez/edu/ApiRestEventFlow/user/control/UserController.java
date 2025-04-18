package utez.edu.ApiRestEventFlow.user.control;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
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

    @GetMapping("/findAll")
    public ResponseEntity<Message> getAllUsers() {
        return userService.findAll();
    }

    @GetMapping("/findId/{userId}")
    public ResponseEntity<Message> getUserById(@PathVariable Long userId) {
        return userService.getUserById(userId);
    }


    @GetMapping("/findAllAdmins")
    public ResponseEntity<Message> getAllAdmins() {
        return userService.findAllAdmins();
    }

    @GetMapping("/findByBoss/{bossId}")
    public ResponseEntity<Message> getByBoss(@PathVariable Long bossId) {
        return userService.findByBossId(bossId);
    }

    @PostMapping("/saveAdmin")
    public ResponseEntity<Message> saveAdmin(@Validated(UserDTO.RegisterAdmin.class)@RequestBody UserDTO userDTO) {
        return userService.saveAdmin(userDTO);
    }

    @PostMapping("/saveChecker")
    public  ResponseEntity<Message> saveChecker(@Validated(UserDTO.RegisterChecker.class)@RequestBody UserDTO userDTO) {
        return userService.saveChecker(userDTO);
    }

    @PostMapping("/saveUser")
    public ResponseEntity<Message> saveUser(@Validated(UserDTO.RegisterUser.class)@RequestBody UserDTO userDTO) {
        return userService.saveUser(userDTO);
    }

    @PutMapping("/updateUser")
    public ResponseEntity<Message> updateUser(@Validated(UserDTO.Modify.class)@RequestBody UserDTO userDTO) {
        return userService.updateUser(userDTO);
    }

    @PutMapping("/updateChecker")
    public ResponseEntity<Message> updateChecker(@Validated(UserDTO.ModifyChecker.class)@RequestBody UserDTO userDTO) {
        return userService.updateChecker(userDTO);
    }

    @PutMapping("/updatePassword")
    public ResponseEntity<Message> updatePassword(@Validated(UserDTO.UpdatePassword.class)@RequestBody UserDTO userDTO) {
        return userService.updatePassword(userDTO);
    }

    @PutMapping("/change-status/{id}")
    public ResponseEntity<Message> changeStatus(@Validated(UserDTO.ChangeStatus.class) @PathVariable Long id) {
        return userService.changeStatus(id);
    }



}
