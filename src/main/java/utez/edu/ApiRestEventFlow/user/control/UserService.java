package utez.edu.ApiRestEventFlow.user.control;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
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
import java.util.Optional;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
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

    @Transactional(rollbackFor = {SQLException.class})
    public ResponseEntity<Message> saveUser(UserDTO userDTO) {
        try {



            if (userRepository.existsByEmail(userDTO.getEmail())) {

                Optional<User> existingUserOpt = userRepository.findByEmail(userDTO.getEmail());
                if (existingUserOpt.isPresent()) {
                    User existingUser = existingUserOpt.get();
                    return updateUser(existingUser, userDTO);
                } else {
                    // Si no se encuentra el usuario a pesar de que existe (caso muy raro), respondemos con error.
                    return new ResponseEntity<>(new Message("User not found, please contact support", TypesResponse.ERROR), HttpStatus.NOT_FOUND);
                }
            } else {
                // Si el usuario no existe, lo creamos
                return registerNewUser(userDTO);
            }
        } catch (ValidationException e) {
            return new ResponseEntity<>(new Message(e.getMessage(), TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(new Message(ErrorMessages.INTERNAL_SERVER_ERROR, TypesResponse.ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private ResponseEntity<Message> registerNewUser(UserDTO userDTO) {
        // Crear un nuevo usuario y guardarlo en la base de datos
        User newUser = new User();
        newUser.setName(userDTO.getName());
        newUser.setLastName(userDTO.getLastName());
        newUser.setEmail(userDTO.getEmail());
        newUser.setPhone(userDTO.getPhone());
        newUser.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        newUser.setRole(Role.USER);
        newUser.setBirthday(userDTO.getBirthday());
        newUser.setAddress(userDTO.getAddress());
        newUser.setHowFound(userDTO.getHowFound());
        newUser.setJob(userDTO.getJob());
        newUser.setWorkPlace(userDTO.getWorkPlace());
        newUser.setGender(userDTO.getGender());
        newUser.setStatus(true);

        newUser = userRepository.saveAndFlush(newUser);

        return new ResponseEntity<>(new Message(newUser, ErrorMessages.SUCCESSFUL_REGISTRATION, TypesResponse.SUCCESS), HttpStatus.OK);
    }

    private ResponseEntity<Message> updateUser(User existingUser, UserDTO userDTO) {
        // Actualizar los datos del usuario existente
        existingUser.setName(userDTO.getName());
        existingUser.setLastName(userDTO.getLastName());
        existingUser.setPhone(userDTO.getPhone());
        existingUser.setPassword(passwordEncoder.encode(userDTO.getPassword())); // Si se actualiza la contraseña
        existingUser.setBirthday(userDTO.getBirthday());
        existingUser.setAddress(userDTO.getAddress());
        existingUser.setHowFound(userDTO.getHowFound());
        existingUser.setJob(userDTO.getJob());
        existingUser.setWorkPlace(userDTO.getWorkPlace());
        existingUser.setGender(userDTO.getGender());
        existingUser.setStatus(true); // Puede cambiar si la lógica lo requiere

        userRepository.saveAndFlush(existingUser);

        return new ResponseEntity<>(new Message(existingUser, ErrorMessages.SUCCESFUL_UPDATE, TypesResponse.SUCCESS), HttpStatus.OK);
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
    public ResponseEntity<Message> getUserById(Long userId) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

            // Si el usuario es encontrado, se responde con sus datos.
            return new ResponseEntity<>(new Message(user, "Datos del usuario obtenidos exitosamente", TypesResponse.SUCCESS), HttpStatus.OK);
        } catch (EntityNotFoundException e) {

            return new ResponseEntity<>(new Message("Usuario no encontrado", TypesResponse.WARNING), HttpStatus.NOT_FOUND);
        } catch (Exception e) {

            return new ResponseEntity<>(new Message("Revise los datos e inténtelo de nuevo", TypesResponse.ERROR), HttpStatus.BAD_REQUEST);
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
    public ResponseEntity<Message> findByBossId(Long bossId) {
        try {
            // Verificamos que el jefe exista
            User sentByUser = userRepository.findById(bossId)
                    .orElseThrow(() -> new ValidationException(ErrorMessages.SENT_BY_USER_NOT_FOUND));

            // Buscamos los checadores asociados a este jefe
            List<User> checkers = userRepository.findBySentByUser(sentByUser);

            if (checkers.isEmpty()) {
                return new ResponseEntity<>(
                        new Message(ErrorMessages.USERS_NOT_FOUND, TypesResponse.WARNING),
                        HttpStatus.OK
                );
            }

            return new ResponseEntity<>(
                    new Message(checkers, "Lista de checadores", TypesResponse.SUCCESS),
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
    public ResponseEntity<Message> saveAdmin(UserDTO userDTO) {
        try {
            validateEmailAndPhone(userDTO);

            User newUser = new User();
            newUser.setName(userDTO.getName());
            newUser.setLastName(userDTO.getLastName());
            newUser.setEmail(userDTO.getEmail());
            newUser.setPhone(userDTO.getPhone());
            newUser.setPassword(passwordEncoder.encode(userDTO.getPassword()));
            newUser.setRole(Role.ADMIN);
            newUser.setCompany(userDTO.getCompany());
            newUser.setStatus(true);

            newUser = userRepository.save(newUser);

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
            newUser.setPassword(passwordEncoder.encode(userDTO.getPassword()));
            newUser.setRole(Role.CHECKER);
            newUser.setStatus(true);
            newUser.setSentByUser(sentByUser);

            newUser = userRepository.save(newUser);

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

            if(userDTO.getPhone() != null) {
                user.setPhone(userDTO.getPhone());
            }

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

            user.setPassword(passwordEncoder.encode(userDTO.getPassword()));

            user = userRepository.saveAndFlush(user);

            return new ResponseEntity<>(new Message(user, ErrorMessages.SUCCESSFUL_PASSWORD_UPDATE, TypesResponse.SUCCESS), HttpStatus.OK);
        } catch (ValidationException e) {
            return new ResponseEntity<>(new Message(e.getMessage(), TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(new Message(ErrorMessages.INTERNAL_SERVER_ERROR, TypesResponse.ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



    @Transactional(rollbackFor = {SQLException.class})
    public ResponseEntity<Message> changeStatus(Long userId) {
        try {
            // Buscar el usuario por ID
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ValidationException(ErrorMessages.USER_NOT_FOUND));

            // Cambiar el estado del usuario
            boolean newStatus = !user.isStatus();
            user.setStatus(newStatus);

            // Crear mensaje de éxito
            String statusMessage = newStatus ? "Activo" : "Inactivo";
            String successMessage = ErrorMessages.SUCCESFUL_CHANGE_STATUS + statusMessage;

            // Guardar el usuario con el nuevo estado
            user = userRepository.saveAndFlush(user);

            // Retornar respuesta exitosa
            return new ResponseEntity<>(new Message(user, successMessage, TypesResponse.SUCCESS), HttpStatus.OK);
        } catch (ValidationException e) {
            return new ResponseEntity<>(new Message(e.getMessage(), TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(new Message(ErrorMessages.INTERNAL_SERVER_ERROR, TypesResponse.ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
