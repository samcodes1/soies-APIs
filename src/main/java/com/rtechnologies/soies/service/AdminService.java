package com.rtechnologies.soies.service;

import com.rtechnologies.soies.model.Admin;
import com.rtechnologies.soies.model.Student;
import com.rtechnologies.soies.model.dto.StudentResponse;
import com.rtechnologies.soies.repository.AdminRepository;
import com.rtechnologies.soies.utilities.Utility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service

public class AdminService {

    @Autowired
    private AdminRepository adminRepository;

    public Admin createAdmin(Admin admin) {
        Utility.printDebugLogs("Admin creation request: " + admin.toString());
        StudentResponse studentResponse;
         if (admin == null) {
                Utility.printDebugLogs("Admin creation request is null");
                throw new IllegalArgumentException("Corrupt data received");
            }

            // Check for existing student
            Optional<Admin> exisOptionalAdmin = adminRepository.findByEmail(admin.getEmail());
            if (exisOptionalAdmin.isPresent()) {
                throw new RuntimeException("Admin with email " + admin.getEmail() + " already exists");
            }

            String hashedPassword = new BCryptPasswordEncoder().encode(admin.getPassword());
            admin.setPassword(hashedPassword);
            Admin createdAdmin = adminRepository.save(admin);
            Utility.printDebugLogs("Admin created successfully: " + createdAdmin);

            return admin;
    }
}
