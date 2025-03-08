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
        try {
            List<User> users = userRepository.findAll();
            if (users.isEmpty()) {
                return new ResponseEntity<>(new Message(ErrorMessages.USERS_NOT_FOUND, TypesResponse.WARNING), HttpStatus.OK);
            }
            return new ResponseEntity<>(new Message(users, "Lista de usuarios", TypesResponse.SUCCESS), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new Message(ErrorMessages.INTERNAL_SERVER_ERROR, TypesResponse.ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional(readOnly = true)
    public ResponseEntity<Message> findAllAdmins() {
        try {
            List<User> users = userRepository.findByRole(Role.ADMIN);
            if (users.isEmpty()) {
                return new ResponseEntity<>(new Message(ErrorMessages.USERS_NOT_FOUND, TypesResponse.WARNING), HttpStatus.OK);
            }
            return new ResponseEntity<>(new Message(users, "Lista de administradores", TypesResponse.SUCCESS), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new Message(ErrorMessages.INTERNAL_SERVER_ERROR, TypesResponse.ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
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
        } catch (ValidationException e) {
            return new ResponseEntity<>(new Message(e.getMessage(), TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(new Message(ErrorMessages.INTERNAL_SERVER_ERROR, TypesResponse.ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @Transactional(rollbackFor = {SQLException.class})
    public ResponseEntity<Message> saveAdmin(UserDTO userDTO) {
        try {
            validateEmailAndPhone(userDTO);

            User newUser = new User();
            newUser.setName(userDTO.getName());
            newUser.setLastName(userDTO.getLastName());
            newUser.setEmail(userDTO.getEmail());
            newUser.setPhone(userDTO.getPhone());
            newUser.setPassword(userDTO.getPassword());
            newUser.setRole(Role.ADMIN);
            newUser.setCompany(userDTO.getCompany());
            newUser.setStatus(true);

            newUser = userRepository.saveAndFlush(newUser);

            return new ResponseEntity<>(new Message(newUser, ErrorMessages.SUCCESSFUL_REGISTRATION, TypesResponse.SUCCESS), HttpStatus.OK);
        } catch (ValidationException e) {
            return new ResponseEntity<>(new Message(e.getMessage(), TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(new Message(ErrorMessages.INTERNAL_SERVER_ERROR, TypesResponse.ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional(rollbackFor = {SQLException.class})
    public ResponseEntity<Message> saveChecker(UserDTO userDTO) {
        try {
            validateEmailAndPhone(userDTO);

            User sentByUser = userRepository.findById(userDTO.getSentByUser().getId())
                    .orElseThrow(() -> new ValidationException(ErrorMessages.SENT_BY_USER_NOT_FOUND));

            validateSentByUser(sentByUser);

            User newUser = new User();
            newUser.setName(userDTO.getName());
            newUser.setLastName(userDTO.getLastName());
            newUser.setEmail(userDTO.getEmail());
            newUser.setPhone(userDTO.getPhone());
            newUser.setPassword(userDTO.getPassword());
            newUser.setRole(Role.CHECKER);
            newUser.setStatus(true);
            newUser.setSentByUser(sentByUser);

            newUser = userRepository.saveAndFlush(newUser);

            return new ResponseEntity<>(new Message(newUser, ErrorMessages.SUCCESSFUL_REGISTRATION, TypesResponse.SUCCESS), HttpStatus.OK);
        } catch (ValidationException e) {
            return new ResponseEntity<>(new Message(e.getMessage(), TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(new Message(ErrorMessages.INTERNAL_SERVER_ERROR, TypesResponse.ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional(rollbackFor = {SQLException.class})
    public ResponseEntity<Message> updateUser(UserDTO userDTO) {
        try {
            User user = userRepository.findById(userDTO.getId())
                    .orElseThrow(() -> new ValidationException(ErrorMessages.USER_NOT_FOUND));

            validateEmailAndPhone(userDTO);

            user.setPhone(userDTO.getPhone());

            user = userRepository.saveAndFlush(user);

            return new ResponseEntity<>(new Message(user, ErrorMessages.SUCCESFUL_UPDATE, TypesResponse.SUCCESS), HttpStatus.OK);
        } catch (ValidationException e) {
            return new ResponseEntity<>(new Message(e.getMessage(), TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(new Message(ErrorMessages.INTERNAL_SERVER_ERROR, TypesResponse.ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional(rollbackFor = {SQLException.class})
    public ResponseEntity<Message> updatePassword(UserDTO userDTO) {
        try {
            User user = userRepository.findById(userDTO.getId())
                    .orElseThrow(() -> new ValidationException(ErrorMessages.USER_NOT_FOUND));

            if (user.getPassword().equals(userDTO.getPassword())) {
                throw new ValidationException(ErrorMessages.SAME_PASSWORD);
            }

            user.setPassword(userDTO.getPassword());

            user = userRepository.saveAndFlush(user);

            return new ResponseEntity<>(new Message(user, ErrorMessages.SUCCESSFUL_PASSWORD_UPDATE, TypesResponse.SUCCESS), HttpStatus.OK);
        } catch (ValidationException e) {
            return new ResponseEntity<>(new Message(e.getMessage(), TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(new Message(ErrorMessages.INTERNAL_SERVER_ERROR, TypesResponse.ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



    @Transactional(rollbackFor = {SQLException.class})
    public ResponseEntity<Message> changeStatus(UserDTO userDTO) {
        try {
            User user = userRepository.findById(userDTO.getId())
                    .orElseThrow(() -> new ValidationException(ErrorMessages.USER_NOT_FOUND));

            boolean newStatus = !user.isStatus();
            user.setStatus(newStatus);

            String statusMessage = newStatus ? "Activo" : "Inactivo";
            String successMessage = ErrorMessages.SUCCESFUL_CHANGE_STATUS + statusMessage;

            user = userRepository.saveAndFlush(user);

            return new ResponseEntity<>(new Message(user, successMessage, TypesResponse.SUCCESS), HttpStatus.OK);
        } catch (ValidationException e) {
            return new ResponseEntity<>(new Message(e.getMessage(), TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(new Message(ErrorMessages.INTERNAL_SERVER_ERROR, TypesResponse.ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
