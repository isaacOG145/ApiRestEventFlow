package utez.edu.ApiRestEventFlow.security.dto;

public class AuthResponse {
    private String jwt;
    private Long userId;
    private String email;
    private long expiration;
    private String role; // Nuevo campo para el rol

    public AuthResponse(String jwt, Long userId, String email, long expiration, String role) {
        this.jwt = jwt;
        this.userId = userId;
        this.email = email;
        this.expiration = expiration;
        this.role = role; // Inicializar el nuevo campo
    }

    // Getters y Setters
    public String getJwt() {
        return jwt;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public long getExpiration() {
        return expiration;
    }

    public void setExpiration(long expiration) {
        this.expiration = expiration;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}