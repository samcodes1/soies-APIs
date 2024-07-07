package com.rtechnologies.soies.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

import javax.transaction.Transactional;

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

    @Transactional
    public List<Teacher> parseTeacherExcelFile(InputStream is) throws IOException {
        List<Teacher> teachers = new ArrayList<>();
        Workbook workbook = new XSSFWorkbook(is);
        Sheet sheet = workbook.getSheetAt(0);
        System.out.println("TEACHERS:> Number of rows in the sheet: " + (sheet.getLastRowNum() + 1)); // Adding 1 because index is 0-based
        System.out.println("TEACHER:> row data>>>>>>>>>>>>>> ");
        for (Row row : sheet) {
            System.out.println("While enter TEACHER");
            if (row.getRowNum() == 0) {
                continue; // Skip header row
            }

            String grade = row.getCell(6).getStringCellValue();
            System.err.println(grade);
            String section = row.getCell(8).getStringCellValue();
            System.err.println(section);

            String campusName = row.getCell(7).getStringCellValue();
            System.err.println(campusName);
            
            // check for campus existsnce if yes the go for grade and section existnace in that campus
            Optional<Campus> campusdata = campusRepositoryObj.findByCampusName(campusName);
            Long campusId = null;
            if(!campusdata.isPresent()){
                Campus campus = Campus.builder().campusName(campusName).build();
                campus = campusRepositoryObj.save(campus);
                campusId = campus.getId();
                System.err.println("DOESNOT EXISTS SO ENTERED DATA AGAIN: "+campusId);
            }else{

                campusId = campusdata.get().getId();
                System.err.println("EXISTS: "+campusId);
            }

            /////////////////////////////////////////////////////////////////////////////////////////////
            // Now check for grade and section against campus id
            Optional<Section> sectionGradeData = sectionRepositoryObj.findByCampusIdAndSectionNameIgnoreCaseAndGrade(campusId, section, grade);
            Long sectionGradeId = null;
            if(!sectionGradeData.isPresent()){
                Section s = Section.builder().campusId(campusId).grade(grade).sectionName(section).build();
                s = sectionRepositoryObj.save(s);
                sectionGradeId = s.getId();
                System.err.println("sectionGradeId DOESNOT EXISTS SO ENTERED DATA AGAIN: "+sectionGradeId);
            }else{
                sectionGradeId = sectionGradeData.get().getId();
                System.err.println("sectionGradeId EXISTS: "+sectionGradeId);
            }

            Teacher teacher = new Teacher();
             System.out.println("row data>>>>>>>>>>>>>> "+row.getCell(1).getStringCellValue());
            
            teacher.setUserName(row.getCell(1).getStringCellValue().toLowerCase());
             System.err.println(row.getCell(1).getStringCellValue().toLowerCase());

             
            teacher.setPassword(new BCryptPasswordEncoder().encode(row.getCell(2).getStringCellValue()));
             System.err.println(new BCryptPasswordEncoder().encode(row.getCell(2).getStringCellValue()));

            teacher.setEmployeeName(row.getCell(3).getStringCellValue()+" "+row.getCell(4).getStringCellValue());
             System.err.println(row.getCell(3).getStringCellValue()+" "+row.getCell(4).getStringCellValue());

            teacher.setEmail(row.getCell(5).getStringCellValue().toLowerCase());
             System.err.println(row.getCell(5).getStringCellValue().toLowerCase());

             Optional<Teacher> teacherTempdata = teacherRepositoryobj.findByEmail(row.getCell(5).getStringCellValue().toLowerCase());

            Long teacherId = null;
            if(!teacherTempdata.isPresent()){
                Teacher t = teacherRepositoryobj.save(teacher);
                teacherId = t.getTeacherId();
                TeacherCampusSectionGradeBranchRepoObj.save(TeacherCampusSectionGradeBranch.builder().teacheIdFk(teacherId).sectionIdFk(sectionGradeId).build());
            }else{
                Optional<TeacherCampusSectionGradeBranch> data = TeacherCampusSectionGradeBranchRepoObj.findByTeacheIdFkAndSectionIdFk(teacherTempdata.get().getTeacherId(), sectionGradeId);
                if(!data.isPresent()){
                    TeacherCampusSectionGradeBranchRepoObj.save(TeacherCampusSectionGradeBranch.builder().teacheIdFk(teacherTempdata.get().getTeacherId()).sectionIdFk(sectionGradeId).build());
                }
            }

            System.out.println("\n\n\n\n");
            // teachers.add(teacher);
        }
        workbook.close();
        return teachers;
    }

    public List<Student> csvParserStudent(MultipartFile file){
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

    public List<Teacher> csvParserTeacher(MultipartFile file){
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
                teacher.setEmployeeName(nextRecord[3].trim().toLowerCase()+" "+nextRecord[4].trim().toLowerCase());
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
