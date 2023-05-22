package app.model;

import app.constant.OfficeType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "offices")
public class Office {
    @Id
    @GeneratedValue
    private Long officeId;
    @NotBlank
    private Long userId;
    private String officeName;
    private OfficeType officeType;
}
