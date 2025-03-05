package utez.edu.ApiRestEventFlow.user.control;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.http.client.ClientHttpRequestFactorySettings;
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

import java.sql.SQLException;
import java.util.List;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final ClientHttpRequestFactorySettings clientHttpRequestFactorySettings;

    @Autowired
    public UserService(UserRepository userRepository, ClientHttpRequestFactorySettings clientHttpRequestFactorySettings) {
        this.userRepository = userRepository;
        this.clientHttpRequestFactorySettings = clientHttpRequestFactorySettings;
    }

    @Transactional(readOnly = true)
    public ResponseEntity<Message> findAll() {
        List<User> users = userRepository.findAll();
        if (users.isEmpty()) {
            return new ResponseEntity<>(new Message("No se encontraron usuarios", TypesResponse.WARNING), HttpStatus.OK);
        }

        return new ResponseEntity<>(new Message(users, "Lista de usuarios", TypesResponse.SUCCESS), HttpStatus.OK);
    }

    @Transactional(readOnly = true)
    public ResponseEntity<Message> findAllAdmins() {
        List<User> users = userRepository.findByRole(Role.ADMIN);
        if (users.isEmpty()) {
            return new ResponseEntity<>(new Message("No se encontraron usuarios", TypesResponse.WARNING), HttpStatus.OK);
        }

        return new ResponseEntity<>(new Message(users, "Lista de administradores", TypesResponse.SUCCESS), HttpStatus.OK);
    }


    @Transactional(rollbackFor = {SQLException.class})
    public ResponseEntity<Message> saveAdmin(UserDTO userDTO) {
        try{
            //validaciones extra

            User newUser = new User();

            newUser.setName(userDTO.getName());
            newUser.setLastName(userDTO.getLastName());
            newUser.setEmail(userDTO.getEmail());
            newUser.setPhone(userDTO.getPhone());
            newUser.setPassword(userDTO.getPassword());//esto se codifica despues
            newUser.setRole(Role.ADMIN);
            newUser.setCompany(userDTO.getCompany());
            newUser = userRepository.saveAndFlush(newUser);

            return new ResponseEntity<>(new Message(newUser, "Administrador guardado exitosamente", TypesResponse.SUCCESS), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(new Message(e.getMessage(), TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(new Message("Revise los datos e inténtelo de nuevo", TypesResponse.ERROR), HttpStatus.BAD_REQUEST);
        }
    }
}
