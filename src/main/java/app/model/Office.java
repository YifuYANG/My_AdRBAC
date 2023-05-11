package app.model;

import app.constant.OfficeType;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Entity
@Table(name = "offices", uniqueConstraints = @UniqueConstraint(columnNames = "officeId"))
public class Office {
    @Id
    @GeneratedValue
    private Long officeId;
    @NotBlank
    private Long userId;
    private String officeName;
    private OfficeType officeType;

    public Office() {
        super();
    }
    public Office(Long officeId, Long userId, String officeName, OfficeType officeType) {
        this.officeId = officeId;
        this.userId = userId;
        this.officeName = officeName;
        this.officeType = officeType;
    }

    public Long getOfficeId() {
        return officeId;
    }

    public void setOfficeId(Long officeId) {
        this.officeId = officeId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getOfficeName() {
        return officeName;
    }

    public void setOfficeName(String officeName) {
        this.officeName = officeName;
    }

    public OfficeType getOfficeType() {
        return officeType;
    }

    public void setOfficeType(OfficeType officeType) {
        this.officeType = officeType;
    }
}
