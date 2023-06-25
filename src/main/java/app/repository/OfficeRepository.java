package app.repository;

import app.model.Office;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OfficeRepository extends JpaRepository<Office, Long> {
    @Query("select o from Office o where o.officeName = :officeName")
    Office findByOfficeName(@Param("officeName") String officeName);
    @Query("select o from Office o where o.userId = :userId")
    Office findByUserId(@Param("userId") Long userId);
    @Query("select o from Office o where o.officeId = :officeId")
    Office findByOfficeId(@Param("officeId") Long officeId);
    @Query("select o from Office o where o.officeId = :officeId")
    List<Office> findAllOfficesByOfficeId(@Param("officeId") Long officeId);
    @Query("select o from Office o where o.officeName = :officeName and o.userId = :userId")
    Office findOfficeByOfficeNameAndUserId(@Param("officeName") String officeName, @Param("userId") Long userId);
    @Query("SELECT o FROM Office o WHERE o.userId = :userId")
    Optional<Office> findByUserID(@Param("userId") Long userId);
}