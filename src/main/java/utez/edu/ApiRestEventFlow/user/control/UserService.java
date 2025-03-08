package utez.edu.ApiRestEventFlow.user.control;

import jakarta.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import utez.edu.ApiRestEventFlow.Role.Role;
import utez.edu.ApiRestEventFlow.user.model.User;
import utez.edu.ApiRestEventFlow.user.model.UserDTO;
import utez.edu.ApiRestEventFlow.user.model.UserRepository;
import utez.edu.ApiRestEventFlow.utils.Message;
import utez.edu.ApiRestEventFlow.utils.TypesResponse;
import utez.edu.ApiRestEventFlow.validation.ErrorMessages;

import java.sql.SQLException;
import java.util.List;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;


    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    private void validateEmailAndPhone(UserDTO userDTO) {

        if(userRepository.existsByEmail(userDTO.getEmail())) {
            throw new IllegalArgumentException(ErrorMessages.EMAIL_EXIST);
        }
        if(userRepository.existsByPhone(userDTO.getPhone())) {
            throw new IllegalArgumentException(ErrorMessages.PHONE_EXIST);
        }
    }

    private void validateSentByUser(User sentByUser) {
        if (sentByUser == null || sentByUser.getRole() != Role.ADMIN) {
            throw new ValidationException(ErrorMessages.INVALID_SENT_BY_USER);
        }
    }

    @Transactional(readOnly = true)
    public ResponseEntity<Message> findAll() {
        try{
            List<User> users = userRepository.findAll();
            if (users.isEmpty()) {
                return new ResponseEntity<>(new Message(ErrorMessages.USERS_NOT_FOUND, TypesResponse.WARNING), HttpStatus.OK);
            }

            return new ResponseEntity<>(new Message(users, "Lista de usuarios", TypesResponse.SUCCESS), HttpStatus.OK);
        }catch(IllegalArgumentException e){
            return new ResponseEntity<>(new Message(e.getMessage(), TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        }catch(Exception e){
            return new ResponseEntity<>(new Message(ErrorMessages.EXCEPTION, TypesResponse.ERROR), HttpStatus.BAD_REQUEST);
        }

    }

    @Transactional(readOnly = true)
    public ResponseEntity<Message> findAllAdmins() {
        try{
            List<User> users = userRepository.findByRole(Role.ADMIN);
            if (users.isEmpty()) {
                return new ResponseEntity<>(new Message(ErrorMessages.USERS_NOT_FOUND, TypesResponse.WARNING), HttpStatus.OK);
            }

            return new ResponseEntity<>(new Message(users, "Lista de administradores", TypesResponse.SUCCESS), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(new Message(e.getMessage(), TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(new Message(ErrorMessages.EXCEPTION, TypesResponse.ERROR), HttpStatus.BAD_REQUEST);
        }

    }

    @Transactional(readOnly = true)
    public ResponseEntity<Message> findByBoss(UserDTO userDTO) {
        try {

            User sentByUser = userRepository.findById(userDTO.getSentByUser().getId())
                    .orElseThrow(() -> new ValidationException(ErrorMessages.SENT_BY_USER_NOT_FOUND));

            List<User> checkers = userRepository.findBySentByUser(sentByUser);

            if (checkers.isEmpty()) {
                return new ResponseEntity<>(new Message(ErrorMessages.USERS_NOT_FOUND, TypesResponse.WARNING), HttpStatus.OK);
            }

            return new ResponseEntity<>(new Message(checkers, "Lista de checadores", TypesResponse.SUCCESS), HttpStatus.OK);
        }catch (IllegalArgumentException e) {
            return new ResponseEntity<>(new Message(e.getMessage(), TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(new Message(ErrorMessages.EXCEPTION, TypesResponse.ERROR), HttpStatus.BAD_REQUEST);
        }
    }


    @Transactional(rollbackFor = {SQLException.class})
    public ResponseEntity<Message> saveAdmin(UserDTO userDTO) {
        try{
            //validaciones extra
            validateEmailAndPhone(userDTO);

            User newUser = new User();

            newUser.setName(userDTO.getName());
            newUser.setLastName(userDTO.getLastName());
            newUser.setEmail(userDTO.getEmail());
            newUser.setPhone(userDTO.getPhone());
            newUser.setPassword(userDTO.getPassword());//esto se codifica despues
            newUser.setRole(Role.ADMIN);
            newUser.setCompany(userDTO.getCompany());
            newUser.setStatus(true);
            newUser = userRepository.saveAndFlush(newUser);

            return new ResponseEntity<>(new Message(newUser, ErrorMessages.SUCCESSFUL_REGISTRATION, TypesResponse.SUCCESS), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(new Message(e.getMessage(), TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(new Message(ErrorMessages.EXCEPTION, TypesResponse.ERROR), HttpStatus.BAD_REQUEST);
        }
    }

    @Transactional(rollbackFor = {SQLException.class})
    public ResponseEntity<Message> saveChecker(UserDTO userDTO) {
        try {
            // Validar que el correo y teléfono no estén registrados
            validateEmailAndPhone(userDTO);

            // Obtener el usuario que registra al checador (sentByUser)
            User sentByUser = userRepository.findById(userDTO.getSentByUser().getId())
                    .orElseThrow(() -> new ValidationException(ErrorMessages.SENT_BY_USER_NOT_FOUND));

            // Validar que el usuario que registra al checador sea un administrador
            validateSentByUser(sentByUser);

            // Crear y configurar el nuevo usuario (checador)
            User newUser = new User();
            newUser.setName(userDTO.getName());
            newUser.setLastName(userDTO.getLastName());
            newUser.setEmail(userDTO.getEmail());
            newUser.setPhone(userDTO.getPhone());
            newUser.setPassword(userDTO.getPassword()); // Esto se codificará después
            newUser.setRole(Role.CHECKER);
            newUser.setStatus(true);
            newUser.setSentByUser(sentByUser); // Asignar al administrador que lo registra


            newUser = userRepository.saveAndFlush(newUser);

            return new ResponseEntity<>(
                    new Message(newUser, ErrorMessages.SUCCESSFUL_REGISTRATION, TypesResponse.SUCCESS),
                    HttpStatus.OK
            );
        }catch (IllegalArgumentException e) {
            return new ResponseEntity<>(new Message(e.getMessage(), TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(new Message(ErrorMessages.EXCEPTION, TypesResponse.ERROR), HttpStatus.BAD_REQUEST);
        }
    }

    @Transactional(rollbackFor = {SQLException.class})
    public ResponseEntity<Message> changeStatus(UserDTO userDTO) {
        User user = userRepository.findById(userDTO.getId())
                .orElseThrow(() -> new RuntimeException(ErrorMessages.USER_NOT_FOUND));

        // Cambiar el estado del usuario
        boolean newStatus = !user.isStatus();
        user.setStatus(newStatus);

        // Crear un mensaje personalizado con el nuevo estado
        String statusMessage = newStatus ? "Activo" : "Inactivo";
        String successMessage = ErrorMessages.SUCCESFUL_CHANGE_STATUS + statusMessage;

        try {
            user = userRepository.saveAndFlush(user);
            return new ResponseEntity<>(new Message(user, successMessage, TypesResponse.SUCCESS), HttpStatus.OK);
        }catch (IllegalArgumentException e) {
            return new ResponseEntity<>(new Message(e.getMessage(), TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(new Message(ErrorMessages.EXCEPTION, TypesResponse.ERROR), HttpStatus.BAD_REQUEST);
        }
    }

}
