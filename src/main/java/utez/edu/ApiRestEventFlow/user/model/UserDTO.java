package utez.edu.ApiRestEventFlow.user.model;


import jakarta.validation.constraints.*;
import utez.edu.ApiRestEventFlow.Role.Role;

import java.sql.Date;

public class UserDTO {

    @NotNull(groups = {ChangeStatus.class}, message = "El ID es obligatorio")
    private Long id;
    @NotBlank(groups = {RegisterAdmin.class, RegisterChecker.class}, message = "El nombre es obligatorio")
    @Size(max = 50, message = "El nombre no puede tener más de 50 caracteres")
    private String name;
    @NotBlank(groups = {RegisterAdmin.class, RegisterChecker.class}, message = "El apellido es obligatorio")
    @Size(max = 50, message = "Los apellidos no puede tener más de 100 caracteres")
    private String lastName;
    @NotBlank(groups = {RegisterAdmin.class, RegisterChecker.class}, message = "El correo electrónico es obligatorio")
    @Email(message = "El correo electrónico no es válido")
    private String email;
    @NotBlank(groups = {RegisterAdmin.class, RegisterChecker.class}, message = "El teléfono es obligatorio")
    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "El teléfono no es válido")
    private String phone;
    @NotBlank(groups = {RegisterAdmin.class, RegisterChecker.class}, message = "La contraseña es obligatoria")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    private String password;
    private Role role;
    private String code;
    private boolean status;
    @NotBlank(groups = {RegisterAdmin.class}, message = "El nombre de la compañia es obligatorio")
    private String company;
    private String gender;
    private Date birthday;
    private String address;
    private String howFound;
    private String job;
    private String workPlace;

    @NotNull(message = "El usuario que registra al checador es obligatorio")
    private User sentByUser;

    public UserDTO() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getHowFound() {
        return howFound;
    }

    public void setHowFound(String howFound) {
        this.howFound = howFound;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getWorkPlace() {
        return workPlace;
    }

    public void setWorkPlace(String workPlace) {
        this.workPlace = workPlace;
    }

    public User getSentByUser() {
        return sentByUser;
    }

    public void setSentByUser(User sentByUser) {
        this.sentByUser = sentByUser;
    }

    public interface RegisterAdmin {
    }

    public interface RegisterChecker {}

    public interface Modify {
    }

    public interface ChangeStatus {
    }
}
