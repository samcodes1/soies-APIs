package com.rtechnologies.soies.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.opencsv.CSVReader;
import com.rtechnologies.soies.model.Student;
import com.rtechnologies.soies.model.Teacher;

import java.util.ArrayList;
import java.util.Arrays;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Row;

@Service
public class ExcelParser {

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
            Teacher teacher = new Teacher();
            // System.out.println("row data>>>>>>>>>>>>>> "+row.getCell(0).getStringCellValue());
            teacher.setCampusName(row.getCell(0).getStringCellValue());
            // System.err.println(row.getCell(0).getStringCellValue());
            teacher.setEmployeeName(row.getCell(1).getStringCellValue());
            // System.err.println(row.getCell(1).getStringCellValue());
            teacher.setEmail(row.getCell(2).getStringCellValue());
            // System.err.println(row.getCell(2).getStringCellValue());
            teacher.setPassword(new BCryptPasswordEncoder().encode(row.getCell(3).getStringCellValue()));
            // System.err.println(new BCryptPasswordEncoder().encode(row.getCell(3).getStringCellValue()));
            teacher.setDateOfBirth(row.getCell(4).getDateCellValue().toString());
            // System.err.println(row.getCell(4).getDateCellValue().toString());
            teacher.setGender(row.getCell(5).getStringCellValue());
            // System.err.println(row.getCell(5).getStringCellValue());
            teacher.setJoiningDate(row.getCell(6).getDateCellValue().toString());
            // System.err.println(row.getCell(6).getDateCellValue().toString());
            teacher.setPhoneNumber(row.getCell(7).getStringCellValue());
            // System.err.println(row.getCell(7).getStringCellValue());
            teacher.setAddress(row.getCell(8).getStringCellValue());
            // System.err.println(row.getCell(8).getStringCellValue());

            System.out.println("\n\n\n\n");
            teachers.add(teacher);
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
