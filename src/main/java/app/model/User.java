package app.model;

import app.constant.OfficeLocations;
import app.constant.UserLevel;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Entity
@Table(name = "users", uniqueConstraints = @UniqueConstraint(columnNames = "email"))
public class User {
    @Id
    @GeneratedValue
    private Long userId;
    @NotBlank
    private String first_name, last_name, password, email;
    private UserLevel userLevel;
    private OfficeLocations officeLocations;

    public User() {
        super();
    }

    public User(long userId, String first_name, String last_name, String password,
                String email_address, UserLevel userLevel,
                OfficeLocations officeLocations){
        super();
        this.userId = userId;
        this.first_name = first_name;
        this.last_name = last_name;
        this.password = password;
        this.email = email_address;
        this.userLevel = userLevel;
        this.officeLocations = officeLocations;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public UserLevel getUserLevel() {
        return userLevel;
    }

    public void setUserLevel(UserLevel userLevel) {
        this.userLevel = userLevel;
    }

    public OfficeLocations getOfficeLocations() {
        return officeLocations;
    }

    public void setOfficeLocations(OfficeLocations officeLocations) {
        this.officeLocations = officeLocations;
    }
}
