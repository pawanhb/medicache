package src.main.java.com.medicalservices.medicache.patientinfo.service;

import com.example.patientmanagement.dto.PatientDto;
import com.example.patientmanagement.entity.Patient;
import com.example.patientmanagement.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@CacheConfig(cacheNames = "patients")
public class PatientService {

    private final PatientRepository patientRepository;
    private final PatientQueryService patientQueryService;

    @Transactional(readOnly = true)
    @Cacheable(key = "'all_patients_page_' + #pageable.pageNumber + '_' + #pageable.pageSize")
    public Page<PatientDto> getAllPatients(Pageable pageable) {
        log.info("Fetching all patients with pagination: {}", pageable);
        return patientRepository.findAll(pageable)
                .map(this::convertToDtoWithBasicInfo);
    }

    @Transactional(readOnly = true)
    @Cacheable(key = "'patient_' + #id")
    public Optional<PatientDto> getPatientById(Long id) {
        log.info("Fetching patient by ID: {}", id);
        return patientRepository.findById(id)
                .map(this::convertToDetailedDto);
    }

    @Transactional(readOnly = true)
    @Cacheable(key = "'patient_mrn_' + #mrn")
    public Optional<PatientDto> getPatientByMrn(String mrn) {
        log.info("Fetching patient by MRN: {}", mrn);
        return patientRepository.findByMrn(mrn)
                .map(this::convertToDetailedDto);
    }

    @Transactional(readOnly = true)
    @Cacheable(key = "'patients_last_name_' + #lastName")
    public List<PatientDto> getPatientsByLastName(String lastName) {
        log.info("Fetching patients by last name: {}", lastName);
        return patientRepository.findByLastNameContainingIgnoreCase(lastName)
                .stream()
                .map(this::convertToDtoWithBasicInfo)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Cacheable(key = "'patients_frequent_' + #minVisits")
    public List<PatientDto> getFrequentPatients(int minVisits) {
        log.info("Fetching patients with more than {} visits", minVisits);
        return patientRepository.findPatientsWithMoreThanVisits(minVisits)
                .stream()
                .map(this::convertToDtoWithBasicInfo)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Cacheable(key = "'patients_recent_visits_since_' + #sinceDate")
    public List<PatientDto> getPatientsWithRecentVisits(LocalDate sinceDate) {
        log.info("Fetching patients with visits since: {}", sinceDate);
        return patientRepository.findPatientsWithVisitsSince(sinceDate)
                .stream()
                .map(this::convertToDtoWithBasicInfo)
                .collect(Collectors.toList());
    }

    private PatientDto convertToDtoWithBasicInfo(Patient patient) {
        return PatientDto.builder()
                .patientId(patient.getPatientId())
                .mrn(patient.getMrn())
                .firstName(patient.getFirstName())
                .lastName(patient.getLastName())
                .dateOfBirth(patient.getDateOfBirth())
                .gender(patient.getGender())
                .createdAt(patient.getCreatedAt())
                .build();
    }

    private PatientDto convertToDetailedDto(Patient patient) {
        PatientDto dto = convertToDtoWithBasicInfo(patient);

        // Add visit counts and other insights
        dto.setTotalVisits((long) patient.getVisits().size());
        dto.setVisitsLastYear(patientQueryService.getVisitsLastYearCount(patient.getPatientId()));

        // Add recent visits summary
        dto.setRecentVisits(patientQueryService.getRecentVisitsSummary(patient.getPatientId()));

        return dto;
    }
}