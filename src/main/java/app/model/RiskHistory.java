package app.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "riskHistories")
public class RiskHistory {

    @Id
    @GeneratedValue
    private Long historyId;
    @NotBlank
    private Long userId;
    private LocalDateTime timeOfOccurrence;
    private String riskDetail;
}
