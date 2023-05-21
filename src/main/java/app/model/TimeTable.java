package app.model;

import app.constant.UserLevel;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.LocalTime;

@Entity
@Table(name = "timeTable", uniqueConstraints = @UniqueConstraint(columnNames = "userid"))
public class TimeTable {

    @Id
    private Long userId;
    @NotBlank
    private LocalTime startTime;
    private LocalTime endTime;
    public TimeTable() {
        super();
    }

    public TimeTable(Long userId, LocalTime startTime, LocalTime endTime) {
        this.userId = userId;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }
}
