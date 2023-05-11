package app.repository;

import app.model.Office;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OfficeRepository extends JpaRepository<Office, Long> {
    @Query("select o from Office o where o.officeName = :officeName")
    Office findByOfficeName(@Param("officeName") String officeName);

    @Query("select o from Office o where o.userId = :userId")
    Office findByUserId(@Param("userId") Long userId);

    @Query("select o from Office o where o.officeId = :officeId")
    Office findByOfficeId(@Param("officeId") Long officeId);
}