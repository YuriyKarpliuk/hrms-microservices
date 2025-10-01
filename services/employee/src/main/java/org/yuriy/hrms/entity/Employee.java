package org.yuriy.hrms.entity;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.time.Period;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "employees", indexes = {@Index(name = "idx_emp_org", columnList = "org_id"),
        @Index(name = "idx_emp_dept", columnList = "dept_id")})
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long orgId;

    @Column(nullable = false, unique = true)
    private String userId;

    private Long deptId;

    private String position;

    private Long managerId;

    private Long hrId;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, length = 100)
    private String firstName;

    @Column(nullable = false, length = 100)
    private String lastName;

    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @Enumerated(EnumType.STRING)
    @Column(length = 16)
    private Gender gender;

    @Enumerated(EnumType.STRING)
    @Column(length = 16)
    private MaritalStatus maritalStatus;

    private String taxNumber;

    private String about;

    private String officeLocation;

    private LocalDate birthDate;

    private LocalDate hiredAt;

    private LocalDate terminatedAt;

    private String avatarUrl;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "languages_json", columnDefinition = "jsonb")
    private JsonNode languagesJson;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "address_json", columnDefinition = "jsonb")
    private JsonNode addressJson;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "education_json", columnDefinition = "jsonb")
    private JsonNode educationJson;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "work_experience_json", columnDefinition = "jsonb")
    private JsonNode workExperienceJson;

    @Column(name = "cv_key")
    private String cvKey;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "profile_json", columnDefinition = "jsonb")
    private JsonNode profileJson;

    @Transient
    public Integer getAge() {
        return birthDate == null ? null : Period.between(birthDate, LocalDate.now()).getYears();
    }

    public enum Gender {MALE, FEMALE}


    public enum MaritalStatus {SINGLE, MARRIED}


    public enum Status {ACTIVE, INACTIVE, ON_LEAVE, TERMINATED}
}
