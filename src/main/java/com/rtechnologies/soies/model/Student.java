package com.rtechnologies.soies.model;

import com.rtechnologies.soies.model.association.StudentAttendance;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(uniqueConstraints={@UniqueConstraint(columnNames={"roll_number"})})
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long studentId;

    @Column(name = "roll_number")
    private String rollNumber;
    private String password;
    private String studentName;
    private String gender;
    private String campusName;
    private String grade;
    private String sectionName;
    private String dateOfBirth;
    private String guardianName;
    private String guardianPhoneNumber;
    private String guardianEmail;
    private String address;
    private String city;

    @OneToOne(mappedBy = "student", cascade = CascadeType.ALL, fetch = FetchType.EAGER,orphanRemoval = true)
    private StudentAttendance studentAttendance;
}
