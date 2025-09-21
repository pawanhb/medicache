package src.main.java.com.medicalservices.medicache.patientinfo.dto;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
class VisitSummaryDto {
    private LocalDate visitDate;
    private String visitType;
    private String doctorName;
    private String doctorSpecialty;
    private List<String> diagnoses;
}
