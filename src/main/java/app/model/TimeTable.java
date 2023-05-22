package app.model;

import app.constant.UserLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "timeTable")
public class TimeTable {

    @Id
    private Long userId;
    @NotBlank
    private LocalTime startTime;
    private LocalTime endTime;
}