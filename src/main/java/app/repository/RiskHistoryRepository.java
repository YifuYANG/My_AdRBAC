package app.repository;

import app.model.RiskHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RiskHistoryRepository extends JpaRepository<RiskHistory, Long> {
    @Query("select r from RiskHistory r where r.historyId = :historyId")
    RiskHistory findByHistoryId(@Param("historyId") Long historyId);

    @Query("select r from RiskHistory r where r.userId = :userId")
    List<RiskHistory> findByUserId(@Param("userId") Long userId);
}
