package app.repository;

import app.model.TimeTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TimeTableRepository extends JpaRepository<TimeTable, Long> {
    @Query("select t from TimeTable t where t.userId = :userId")
    TimeTable findByUserId(@Param("userId") Long userId);
}
