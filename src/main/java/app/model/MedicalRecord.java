package app.model;

import app.constant.ResourceSensitivity;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Entity
@Table(name = "medicalrecord", uniqueConstraints = @UniqueConstraint(columnNames = "recordId"))
public class MedicalRecord {
    @Id
    @GeneratedValue
    private Long recordId;
    @NotBlank
    private String patient_first_name, patient_last_name, description;
    private Boolean ispatientincriticalcondition;

    private ResourceSensitivity resourceSensitivity;

    public MedicalRecord() {
        super();
    }

    public MedicalRecord(Long recordId, String patient_first_name, String patient_last_name, String description,
                         Boolean ispatientincriticalcondition, ResourceSensitivity resourceSensitivity) {
        this.recordId = recordId;
        this.patient_first_name = patient_first_name;
        this.patient_last_name = patient_last_name;
        this.description = description;
        this.ispatientincriticalcondition = ispatientincriticalcondition;
        this.resourceSensitivity = resourceSensitivity;
    }

    public Long getRecordId() {
        return recordId;
    }

    public void setRecordId(Long recordId) {
        this.recordId = recordId;
    }

    public String getPatient_first_name() {
        return patient_first_name;
    }

    public void setPatient_first_name(String patient_first_name) {
        this.patient_first_name = patient_first_name;
    }

    public String getPatient_last_name() {
        return patient_last_name;
    }

    public void setPatient_last_name(String patient_last_name) {
        this.patient_last_name = patient_last_name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getIspatientincriticalcondition() {
        return ispatientincriticalcondition;
    }

    public void setIspatientincriticalcondition(Boolean ispatientincriticalcondition) {
        this.ispatientincriticalcondition = ispatientincriticalcondition;
    }

    public ResourceSensitivity getResourceSensitivity() {
        return resourceSensitivity;
    }

    public void setResourceSensitivity(ResourceSensitivity resourceSensitivity) {
        this.resourceSensitivity = resourceSensitivity;
    }
}
