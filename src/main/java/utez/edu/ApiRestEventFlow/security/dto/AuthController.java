package utez.edu.ApiRestEventFlow.security.dto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import utez.edu.ApiRestEventFlow.security.JwtUtil;
import utez.edu.ApiRestEventFlow.security.UserDetailsServiceImpl;
import utez.edu.ApiRestEventFlow.user.model.User;
import utez.edu.ApiRestEventFlow.user.model.UserRepository;
import utez.edu.ApiRestEventFlow.utils.Message;
import utez.edu.ApiRestEventFlow.utils.TypesResponse;

import java.util.Optional;

@RestController
@CrossOrigin(origins = {"*"}, methods = {RequestMethod.POST, RequestMethod.GET, RequestMethod.PUT, RequestMethod.DELETE})
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsServiceImpl userDetailsService;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager, UserDetailsServiceImpl userDetailsService, JwtUtil jwtUtil, UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<Message> login(@RequestBody AuthRequest authRequest) {
        // Buscar al usuario por correo
        Optional<User> optionalUser = userRepository.findByEmail(authRequest.getEmail());

        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(401).body(
                    new Message("Credenciales incorrectas", TypesResponse.ERROR)
            );
        }

        User user = optionalUser.get();

        // Verificar si está activo
        if (!user.isStatus()) {
            return ResponseEntity.status(403).body(
                    new Message("Usuario inactivo. Contacta al administrador.", TypesResponse.WARNING)
            );
        }

        // Intentar autenticar con email y password
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword())
            );
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(401).body(
                    new Message("Credenciales incorrectas", TypesResponse.ERROR)
            );
        }

        // Generar token y respuesta
        UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getEmail());
        String jwt = jwtUtil.generateToken(userDetails);
        long expirationTime = jwtUtil.getExpirationTime();

        AuthResponse authResponse = new AuthResponse(jwt, user.getId(), user.getEmail(), expirationTime, user.getRole().name());

        return ResponseEntity.ok(
                new Message(authResponse, "Inicio de sesión exitoso", TypesResponse.SUCCESS)
        );
    }




    @GetMapping("/profile")
    public ResponseEntity<AuthResponse> getProfile(@RequestHeader("Authorization") String token) {
        // Remover el prefijo "Bearer "
        String jwt = token.replace("Bearer ", "");

        // Extraer el email desde el JWT
        String email = jwtUtil.extractUsername(jwt);

        // Buscar al usuario en la base de datos
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Crear la respuesta
        AuthResponse response = new AuthResponse(
                null,
                user.getId(),
                user.getEmail(),
                jwtUtil.getExpirationTime(),
                user.getRole().name()
        );

        return ResponseEntity.ok(response);
    }

}