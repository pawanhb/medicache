package src.main.java.com.medicalservices.medicache.patientinfo.repository;

import com.example.patientmanagement.entity.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

    Optional<Patient> findByMrn(String mrn);

    List<Patient> findByLastNameContainingIgnoreCase(String lastName);

    Page<Patient> findAll(Pageable pageable);

    @Query("SELECT p FROM Patient p WHERE p.dateOfBirth BETWEEN :startDate AND :endDate")
    List<Patient> findByDateOfBirthBetween(@Param("startDate") LocalDate startDate,
                                           @Param("endDate") LocalDate endDate);

    @Query("SELECT p FROM Patient p JOIN p.visits v WHERE v.visitDate >= :sinceDate")
    List<Patient> findPatientsWithVisitsSince(@Param("sinceDate") LocalDate sinceDate);

    @Query("SELECT p FROM Patient p WHERE SIZE(p.visits) > :minVisits")
    List<Patient> findPatientsWithMoreThanVisits(@Param("minVisits") int minVisits);
}
