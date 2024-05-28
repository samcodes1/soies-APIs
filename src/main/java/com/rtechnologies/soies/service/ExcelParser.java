package com.rtechnologies.soies.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.rtechnologies.soies.model.Student;
import com.rtechnologies.soies.model.Teacher;

import java.util.ArrayList;
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
            // System.out.println("row data>>>>>>>>>>>>>> "+row.getCell(0).getStringCellValue());
            student.setRollNumber(Double.toString(row.getCell(0).getNumericCellValue()));
            student.setStudentName(row.getCell(1).getStringCellValue());
            student.setGender(row.getCell(2).getStringCellValue());
            student.setGrade(row.getCell(3).getStringCellValue());
            student.setCampusName(row.getCell(4).getStringCellValue());
            student.setSectionName(row.getCell(5).getStringCellValue());
            student.setDateOfBirth(row.getCell(6).getDateCellValue().toString());
            student.setGuardianName(row.getCell(7).getStringCellValue());
            student.setGuardianPhoneNumber(row.getCell(8).getStringCellValue());
            student.setPassword(new BCryptPasswordEncoder().encode(row.getCell(9).getStringCellValue()));
            student.setGuardianEmail(row.getCell(10).getStringCellValue());
            student.setAddress(row.getCell(11).getStringCellValue());
            student.setCity(row.getCell(12).getStringCellValue());
            students.add(student);
        }
        workbook.close();
        return students;
    }

    public List<Teacher> parseTeacherExcelFile(InputStream is) throws IOException {
        List<Teacher> teachers = new ArrayList<>();
        Workbook workbook = new XSSFWorkbook(is);
        Sheet sheet = workbook.getSheetAt(0);
        System.out.println("row data>>>>>>>>>>>>>> ");
        for (Row row : sheet) {
            System.out.println("While enter ");
            if (row.getRowNum() == 0) {
                continue; // Skip header row
            }
            Teacher teacher = new Teacher();
            // System.out.println("row data>>>>>>>>>>>>>> "+row.getCell(0).getStringCellValue());
            teacher.setCampusName(row.getCell(0).getStringCellValue());
            teacher.setEmployeeName(row.getCell(1).getStringCellValue());
            teacher.setEmail(row.getCell(2).getStringCellValue());
            teacher.setPassword(new BCryptPasswordEncoder().encode(row.getCell(3).getStringCellValue()));
            teacher.setDateOfBirth(row.getCell(4).getDateCellValue().toString());
            teacher.setGender(row.getCell(5).getStringCellValue());
            teacher.setJoiningDate(row.getCell(6).getDateCellValue().toString());
            teacher.setPhoneNumber(row.getCell(7).getStringCellValue());
            teacher.setAddress(row.getCell(8).getStringCellValue());
            teachers.add(teacher);
        }
        workbook.close();
        return teachers;
    }
}
