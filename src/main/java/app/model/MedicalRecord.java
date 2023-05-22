package app.model;

import app.constant.ResourceSensitivity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "medicalRecords")
public class MedicalRecord {
    @Id
    @GeneratedValue
    private Long recordId;
    @NotBlank
    private String patient_first_name, patient_last_name, description;
    private Boolean ispatientincriticalcondition;
    private ResourceSensitivity resourceSensitivity;
}