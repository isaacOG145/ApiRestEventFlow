package utez.edu.ApiRestEventFlow.user.model;

import jakarta.persistence.*;
import utez.edu.ApiRestEventFlow.Role.Role;
import utez.edu.ApiRestEventFlow.assignment.model.Assignment;

import java.sql.Date;
import java.util.List;

@Entity
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "name", columnDefinition = "VARCHAR(50)")
    private String name;

    @Column(name = "last_name", columnDefinition = "VARCHAR(100)")
    private String lastName;

    @Column(name = "email", columnDefinition = "VARCHAR(50)")
    private String email;

    @Column(name = "password", columnDefinition = "VARCHAR(255)")
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", columnDefinition = "VARCHAR(10)")
    private Role role;

    @Column(name = "code", columnDefinition = "VARCHAR(10)")
    private String code;

    @Column(name = "status", columnDefinition = "BOOL DEFAULT TRUE")
    private boolean status;

    //admin
    @Column(name = "company", columnDefinition = "VARCHAR(50)")
    private String company;

    //checador
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Assignment> assignments;

    @ManyToOne
    @JoinColumn(name = "sent_by_user_id", referencedColumnName = "user_id")
    private User sentByUser;

    //usuario
    @Column(name ="gender", columnDefinition = "VARCHAR(50)")
    private String gender;

    @Column(name = "birthday", columnDefinition = "DATE")
    private Date birthday;

    @Column(name = "address", columnDefinition = "VARCHAR(100)")
    private String address;

    @Column(name = "how_found", columnDefinition = "VARCHAR(50)")
    private String howFound;

    @Column(name = "job", columnDefinition = "VARCHAR(50)")
    private String job;

    @Column(name = "work_place", columnDefinition = "VARCHAR(50)")
    private String workPlace;

    public User() {}

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

    public User getSentByUser() {
        return sentByUser;
    }

    public void setSentByUser(User sentByUser) {
        this.sentByUser = sentByUser;
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

    // Método para validar si el usuario que envía tiene el rol adecuado
    public boolean canSendUser(Role requiredRole) {
        return this.role == requiredRole;
    }
}