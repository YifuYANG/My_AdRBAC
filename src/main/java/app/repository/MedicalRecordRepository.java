package app.repository;

import app.model.MedicalRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Long> {
    @Query("select r from MedicalRecord r where r.recordId = :recordId")
    MedicalRecord findRecordById(@Param("recordId") Long RecordId);
}
