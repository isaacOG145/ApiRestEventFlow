package utez.edu.ApiRestEventFlow.security.dto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import utez.edu.ApiRestEventFlow.security.JwtUtil;
import utez.edu.ApiRestEventFlow.security.UserDetailsServiceImpl;
import utez.edu.ApiRestEventFlow.user.model.User;
import utez.edu.ApiRestEventFlow.user.model.UserRepository;

@RestController
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
    public AuthResponse login(@RequestBody AuthRequest authRequest) throws Exception {
        try {
            // Autenticar al usuario
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword()));
        } catch (BadCredentialsException e) {
            throw new Exception("Usuario o contraseña incorrectos", e);
        }

        // Obtener los detalles del usuario
        final UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getEmail());
        final String jwt = jwtUtil.generateToken(userDetails);

        // Obtener el usuario desde la base de datos
        User user = userRepository.findByEmail(authRequest.getEmail())
                .orElseThrow(() -> new Exception("Usuario no encontrado"));

        // Obtener el tiempo de expiración del token
        long expirationTime = jwtUtil.getExpirationTime();

        // Crear y retornar la respuesta con el rol
        return new AuthResponse(jwt, user.getId(), user.getEmail(), expirationTime, user.getRole().name());
    }
}