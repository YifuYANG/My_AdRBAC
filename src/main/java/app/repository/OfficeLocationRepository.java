package app.repository;

import app.model.OfficeLocation;
import app.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OfficeLocationRepository extends JpaRepository<OfficeLocation, Long> {
    @Query("select o from OfficeLocation o where o.officeName = :officeName")
    User findByOfficeName(@Param("officeName") String officeName);
}