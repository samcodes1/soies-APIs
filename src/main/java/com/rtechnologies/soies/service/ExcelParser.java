package com.rtechnologies.soies.service;

import java.io.*;
import java.util.List;

import javax.transaction.Transactional;

import com.opencsv.exceptions.CsvValidationException;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.NotOfficeXmlFileException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.opencsv.CSVReader;
import com.rtechnologies.soies.model.Campus;
import com.rtechnologies.soies.model.Section;
import com.rtechnologies.soies.model.Student;
import com.rtechnologies.soies.model.Teacher;
import com.rtechnologies.soies.model.association.TeacherCampusSectionGradeBranch;
import com.rtechnologies.soies.repository.CampusRepository;
import com.rtechnologies.soies.repository.SectionRepository;
import com.rtechnologies.soies.repository.TeacherCampusSectionGradeBranchRepo;
import com.rtechnologies.soies.repository.TeacherRepository;

import java.util.Optional;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Row;

@Service
public class ExcelParser {
    @Autowired
    CampusRepository campusRepositoryObj;

    @Autowired
    SectionRepository sectionRepositoryObj;

    @Autowired
    TeacherCampusSectionGradeBranchRepo TeacherCampusSectionGradeBranchRepoObj;

    @Autowired
    TeacherRepository teacherRepositoryobj;

    public List<Student> parseStudentExcelFile(InputStream is) throws IOException {
        List<Student> students = new ArrayList<>();
        Workbook workbook = new XSSFWorkbook(is);
        Sheet sheet = workbook.getSheetAt(0);
        System.out.println("row data>>>>>>>>>>>>>> ");
        for (Row row : sheet) {
            System.out.println("While enter ");
            if (row.getRowNum() == 0) {
                continue; // Skip header row
            }
            Student student = new Student();
            //Roll Number	First Name	Middle Name	Last Name	Campus Name	Class Name	Section Name	Email Ids	password	
            // System.out.println("row data>>>>>>>>>>>>>> "+row.getCell(0).getStringCellValue());
            student.setRollNumber(row.getCell(0).getStringCellValue());

            String fullName = new StringBuilder().append(row.getCell(1).getStringCellValue())
                    .append(" ")
                    .append(row.getCell(2).getStringCellValue())
                    .append(" ")
                    .append(row.getCell(3).getStringCellValue()).toString();

            student.setStudentName(fullName);
            student.setCampusName(row.getCell(4).getStringCellValue());
            student.setGrade(row.getCell(5).getStringCellValue());
            student.setSectionName(row.getCell(6).getStringCellValue());
            student.setGuardianEmail(row.getCell(7).getStringCellValue());
            student.setPassword(new BCryptPasswordEncoder().encode(row.getCell(6).getStringCellValue()));
            students.add(student);
        }
        workbook.close();
        return students;
    }

    public List<Teacher> parseFile(MultipartFile file) throws IOException {
        String fileName = file.getOriginalFilename();
        if (fileName != null && fileName.toLowerCase().endsWith(".csv")) {
            return parseCsvFile(file.getInputStream());
        } else if (fileName != null && fileName.toLowerCase().endsWith(".xlsx")) {
            return parseExcelFile(file.getInputStream());
        } else {
            throw new IllegalArgumentException("Unsupported file type. Only CSV and Excel files are supported.");
        }
    }

    private List<Teacher> parseCsvFile(InputStream is) throws IOException {
        List<Teacher> teachers = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            String line;
            String[] headers = reader.readLine().split(","); // Read headers

            // Initialize column indices
            int usernameIdx = -1, passwordIdx = -1, firstnameIdx = -1, lastnameIdx = -1, emailIdx = -1, gradesAssignedIdx = -1, campusNameIdx = -1;

            // Identify column indices
            for (int i = 0; i < headers.length; i++) {
                String headerValue = headers[i].trim().toLowerCase();
                switch (headerValue) {
                    case "username":
                        usernameIdx = i;
                        break;
                    case "password":
                        passwordIdx = i;
                        break;
                    case "firstname":
                        firstnameIdx = i;
                        break;
                    case "lastname":
                        lastnameIdx = i;
                        break;
                    case "email":
                        emailIdx = i;
                        break;
                    case "grades assigned":
                        gradesAssignedIdx = i;
                        break;
                    case "campus":
                        campusNameIdx = i;
                        break;
                    default:
                        // Handle unexpected headers if needed
                        break;
                }
            }

            // Ensure all necessary columns are found
            if (usernameIdx == -1 || passwordIdx == -1 || firstnameIdx == -1 || lastnameIdx == -1 || emailIdx == -1 || gradesAssignedIdx == -1 || campusNameIdx == -1) {
                throw new IllegalStateException("Required columns are missing in the CSV file.");
            }

            // Process data rows
            while ((line = reader.readLine()) != null) {
                String[] values = line.split(","); // Split by comma

                String username = getValue(values, usernameIdx);
                String password = getValue(values, passwordIdx);
                String firstname = getValue(values, firstnameIdx);
                String lastname = getValue(values, lastnameIdx);
                String email = getValue(values, emailIdx);
                String gradesAssigned = getValue(values, gradesAssignedIdx);
                String campusName = getValue(values, campusNameIdx);

                // Process the row data
                processTeacherData(username, password, firstname, lastname, email, gradesAssigned, campusName, teachers);
            }
        }
        return teachers;
    }

    private List<Teacher> parseExcelFile(InputStream is) throws IOException {
        List<Teacher> teachers = new ArrayList<>();
        Workbook workbook = new XSSFWorkbook(is);
        Sheet sheet = workbook.getSheetAt(0);

        // Initialize column indices
        int usernameIdx = -1, passwordIdx = -1, firstnameIdx = -1, lastnameIdx = -1, emailIdx = -1, gradesAssignedIdx = -1, campusNameIdx = -1;

        // Read header row
        Row headerRow = sheet.getRow(0);
        if (headerRow != null) {
            for (Cell cell : headerRow) {
                String headerValue = cell.getStringCellValue().trim().toLowerCase();
                switch (headerValue) {
                    case "username":
                        usernameIdx = cell.getColumnIndex();
                        break;
                    case "password":
                        passwordIdx = cell.getColumnIndex();
                        break;
                    case "firstname":
                        firstnameIdx = cell.getColumnIndex();
                        break;
                    case "lastname":
                        lastnameIdx = cell.getColumnIndex();
                        break;
                    case "email":
                        emailIdx = cell.getColumnIndex();
                        break;
                    case "grades assigned":
                        gradesAssignedIdx = cell.getColumnIndex();
                        break;
                    case "campus":
                        campusNameIdx = cell.getColumnIndex();
                        break;
                    default:
                        // Handle unexpected headers if needed
                        break;
                }
            }
        }

        // Ensure all necessary columns are found
        if (usernameIdx == -1 || passwordIdx == -1 || firstnameIdx == -1 || lastnameIdx == -1 || emailIdx == -1 || gradesAssignedIdx == -1 || campusNameIdx == -1) {
            throw new IllegalStateException("Required columns are missing in the Excel file.");
        }

        // Process data rows
        for (Row row : sheet) {
            if (row.getRowNum() == 0) {
                continue; // Skip header row
            }

            String username = getCellValue(row, usernameIdx);
            String password = getCellValue(row, passwordIdx);
            String firstname = getCellValue(row, firstnameIdx);
            String lastname = getCellValue(row, lastnameIdx);
            String email = getCellValue(row, emailIdx);
            String gradesAssigned = getCellValue(row, gradesAssignedIdx);
            String campusName = getCellValue(row, campusNameIdx);

            // Process the row data
            processTeacherData(username, password, firstname, lastname, email, gradesAssigned, campusName, teachers);
        }

        workbook.close();
        return teachers;
    }

    private String getValue(String[] values, int index) {
        return index >= 0 && index < values.length ? values[index].trim() : "";
    }

    private String getCellValue(Row row, int columnIndex) {
        Cell cell = row.getCell(columnIndex);
        return cell != null ? cell.getStringCellValue().trim() : "";
    }

    private void processTeacherData(String username, String password, String firstname, String lastname, String email, String gradesAssigned, String campusName, List<Teacher> teachers) {
        // Check for campus existence
        Optional<Campus> campusOptional = campusRepositoryObj.findByCampusName(campusName);
        Long campusId;
        if (campusOptional.isEmpty()) {
            Campus campus = Campus.builder().campusName(campusName).build();
            campus = campusRepositoryObj.save(campus);
            campusId = campus.getId();
        } else {
            campusId = campusOptional.get().getId();
        }

        // Parsing grades
        List<Section> sections = parseSections(gradesAssigned, campusId);

        // Create teacher object
        Teacher teacher = new Teacher();
        teacher.setUserName(username.toLowerCase());
        teacher.setPassword(new BCryptPasswordEncoder().encode(password));
        teacher.setEmployeeName(firstname + " " + lastname);
        teacher.setEmail(email.toLowerCase());

        // Save teacher and its associations
        Optional<Teacher> existingTeacher = teacherRepositoryobj.findByEmail(email.toLowerCase());
        Long teacherId;
        if (existingTeacher.isEmpty()) {
            Teacher savedTeacher = teacherRepositoryobj.save(teacher);
            teacherId = savedTeacher.getTeacherId();
        } else {
            teacherId = existingTeacher.get().getTeacherId();
        }

        // Save associations with sections
        for (Section section : sections) {
            Optional<TeacherCampusSectionGradeBranch> existingAssociation = TeacherCampusSectionGradeBranchRepoObj.findByTeacheIdFkAndSectionIdFk(teacherId, section.getId());
            if (existingAssociation.isEmpty()) {
                TeacherCampusSectionGradeBranchRepoObj.save(TeacherCampusSectionGradeBranch.builder()
                        .teacheIdFk(teacherId)
                        .sectionIdFk(section.getId())
                        .build());
            }
        }

        teachers.add(teacher); // Add teacher to the list
    }

    private List<Section> parseSections(String gradesAssigned, Long campusId) {
        List<Section> sections = new ArrayList<>();
        // Implement logic to parse the gradesAssigned field and create or find Section entities
        // For example:
        String[] gradeSections = gradesAssigned.split(","); // Adjust delimiter if needed
        for (String gradeSection : gradeSections) {
            String[] parts = gradeSection.split("\\("); // Example split
            String grade = parts[0].trim();
            String section = parts.length > 1 ? parts[1].replace(")", "").trim() : "";

            Optional<Section> sectionOptional = sectionRepositoryObj.findByCampusIdAndSectionNameIgnoreCaseAndGrade(campusId, section, grade);
            if (sectionOptional.isEmpty()) {
                Section newSection = Section.builder()
                        .campusId(campusId)
                        .grade(grade)
                        .sectionName(section)
                        .build();
                Section savedSection = sectionRepositoryObj.save(newSection);
                sections.add(savedSection);
            } else {
                sections.add(sectionOptional.get());
            }
        }
        return sections;
    }


    public List<Student> csvParserStudent(MultipartFile file) {
        List<Student> students = new ArrayList<>();
        if (file.isEmpty()) {
            throw new IllegalArgumentException("No Record Found in csv");
        }

        try (Reader reader = new InputStreamReader(file.getInputStream())) {
            // Create a CSVReader using OpenCSV
            CSVReader csvReader = new CSVReader(reader);
            // Skip the header row
            csvReader.skip(1);
            // Read the CSV data line by line
            String[] nextRecord;
            while ((nextRecord = csvReader.readNext()) != null) {
                if (nextRecord.length == 0) {
                    continue;
                }
                // Check if the first field is empty (This may indicate the end of the file)
                if (nextRecord[0].isEmpty()) {
                    break;
                }
                Student student = new Student();
                System.out.println("Record: " + Arrays.toString(nextRecord));
                student.setRollNumber(nextRecord[0].trim());
                String fullName = new StringBuilder().append(nextRecord[1])
                        .append(" ")
                        .append(nextRecord[2])
                        .append(" ")
                        .append(nextRecord[3]).toString();

                student.setStudentName(fullName.trim().toLowerCase());
                student.setCampusName(nextRecord[4].trim().toLowerCase());
                student.setGrade(nextRecord[5].trim().toLowerCase());
                student.setSectionName(nextRecord[6].trim().toLowerCase());
                student.setGuardianEmail(nextRecord[7].trim().toLowerCase());
                student.setPassword(new BCryptPasswordEncoder().encode(nextRecord[8].trim()));
                students.add(student);
            }
            System.out.println("FILE READ COMPLETE RETURNING");
            return students;
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException("parsing error");
        }
    }

    public List<Teacher> csvParserTeacher(MultipartFile file) {
        List<Teacher> teachers = new ArrayList<>();
        if (file.isEmpty()) {
            throw new IllegalArgumentException("No Record Found in csv");
        }

        try (Reader reader = new InputStreamReader(file.getInputStream())) {
            // Create a CSVReader using OpenCSV
            CSVReader csvReader = new CSVReader(reader);
            // Skip the header row
            csvReader.skip(1);
            // Read the CSV data line by line
            String[] nextRecord;
            while ((nextRecord = csvReader.readNext()) != null) {
                if (nextRecord.length == 0) {
                    continue;
                }
                // Check if the first field is empty (This may indicate the end of the file)
                if (nextRecord[0].isEmpty()) {
                    break;
                }
                Teacher teacher = new Teacher();
                System.out.println("Record: " + Arrays.toString(nextRecord));
                teacher.setUserName(nextRecord[1].trim());
                teacher.setPassword(new BCryptPasswordEncoder().encode(nextRecord[2].trim()));
                teacher.setEmployeeName(nextRecord[3].trim().toLowerCase() + " " + nextRecord[4].trim().toLowerCase());
                teacher.setEmail(nextRecord[5].trim().toLowerCase());
                teacher.setGrade(nextRecord[6].trim().toLowerCase());
                teacher.setCampusName(nextRecord[7].trim().toLowerCase());
                teachers.add(teacher);
            }
            System.out.println("FILE READ COMPLETE RETURNING");
            return teachers;
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException("parsing error");
        }
    }
}
