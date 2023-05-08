package app.model;


import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Entity
@Table(name = "riskhistory", uniqueConstraints = @UniqueConstraint(columnNames = "userid"))
public class RiskHistory {

    @Id
    @GeneratedValue
    private Long historyId;
    @NotBlank
    private Long userId;
    private LocalDateTime timeOfOccurrence;
    private String riskDetail;

    public RiskHistory() {
        super();
    }

    public RiskHistory(Long historyId, Long userId, LocalDateTime timeOfOccurrence, String riskDetail) {
        this.historyId = historyId;
        this.userId = userId;
        this.timeOfOccurrence = timeOfOccurrence;
        this.riskDetail = riskDetail;
    }
}
