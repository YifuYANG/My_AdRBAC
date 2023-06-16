package app.model;

import app.constant.AccessLevel;
import app.constant.UserLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users", uniqueConstraints = @UniqueConstraint(columnNames = "email"))
public class User {
    @Id
    @GeneratedValue
    private Long userId;
    @NotBlank
    private String first_name, last_name, password, email;
    private UserLevel userLevel;
    private AccessLevel accessLevel;
}
