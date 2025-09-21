package src.main.java.com.medicalservices.medicache.patientinfo.service;


import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PatientQueryService {

    private final PatientRepository patientRepository;

    @Transactional(readOnly = true)
    @Cacheable(key = "'visits_last_year_' + #patientId")
    public Long getVisitsLastYearCount(Long patientId) {
        LocalDate oneYearAgo = LocalDate.now().minusYears(1);
        return patientRepository.findById(patientId)
                .map(patient -> patient.getVisits().stream()
                        .filter(visit -> !visit.getVisitDate().isBefore(oneYearAgo))
                        .count())
                .orElse(0L);
    }

    @Transactional(readOnly = true)
    @Cacheable(key = "'recent_visits_' + #patientId")
    public List<VisitSummaryDto> getRecentVisitsSummary(Long patientId) {
        return patientRepository.findById(patientId)
                .map(patient -> patient.getVisits().stream()
                        .sorted((v1, v2) -> v2.getVisitDate().compareTo(v1.getVisitDate()))
                        .limit(5) // Last 5 visits
                        .map(visit -> VisitSummaryDto.builder()
                                .visitDate(visit.getVisitDate())
                                .visitType(visit.getVisitType())
                                .doctorName(visit.getDoctor().getName())
                                .doctorSpecialty(visit.getDoctor().getSpecialty())
                                .diagnoses(visit.getDiagnoses().stream()
                                        .map(diagnosis -> diagnosis.getCode() + " - " + diagnosis.getDescription())
                                        .collect(Collectors.toList()))
                                .build())
                        .collect(Collectors.toList()))
                .orElse(List.of());
    }
}
