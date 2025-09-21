package src.main.java.com.medicalservices.medicache.patientinfo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.medicalservices.medicache.patientinfo.dto.PatientDto;
import com.medicalservices.medicache.patientinfo.service.PatientService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;

    @GetMapping
    public ResponseEntity<Page<PatientDto>> getAllPatients(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            @RequestParam(defaultValue = "lastName") String sortBy) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return ResponseEntity.ok(patientService.getAllPatients(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PatientDto> getPatientById(@PathVariable Long id) {
        Optional<PatientDto> patient = patientService.getPatientById(id);
        return patient.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/mrn/{mrn}")
    public ResponseEntity<PatientDto> getPatientByMrn(@PathVariable String mrn) {
        Optional<PatientDto> patient = patientService.getPatientByMrn(mrn);
        return patient.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search/last-name")
    public ResponseEntity<List<PatientDto>> getPatientsByLastName(@RequestParam String lastName) {
        return ResponseEntity.ok(patientService.getPatientsByLastName(lastName));
    }

    @GetMapping("/frequent")
    public ResponseEntity<List<PatientDto>> getFrequentPatients(
            @RequestParam(defaultValue = "5") int minVisits) {
        return ResponseEntity.ok(patientService.getFrequentPatients(minVisits));
    }

    @GetMapping("/recent-visits")
    public ResponseEntity<List<PatientDto>> getPatientsWithRecentVisits(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate since) {
        return ResponseEntity.ok(patientService.getPatientsWithRecentVisits(since));
    }
}
