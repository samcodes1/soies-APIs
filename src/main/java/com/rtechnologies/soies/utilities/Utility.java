package com.rtechnologies.soies.utilities;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVReader;
import com.rtechnologies.soies.model.Student;

public class Utility {

    private final static Logger log = LoggerFactory.getLogger(Utility.class);

    private final static Logger exceptionLog = LoggerFactory.getLogger("exceptionLogger");

    public static void printInfoLogs(String msg) {
        log.info("Date Time: " + new Date() + " => " + msg + "\n");
//     System.out.println(msg);
    }

    public static void printDebugLogs(String msg) {
        log.debug("Date Time: " + new Date() + " => " + msg + "\n");
//     System.out.println(msg);
    }

    public static void printTraceLogs(String msg) {
        log.trace("Date Time: " + new Date() + " => " + msg + "\n");
//     System.out.println(msg);
    }

    public static void printErrorLogs(String msg) {
        exceptionLog.error(msg);
        // System.out.println(msg);
    }

    public static String convertStackTraceToString(Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }

    public static String objectToJSON(Object object) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(object);
    }

    public static <T> T JsonToObject(String json, Class<T> type) throws Exception, IOException {
        return new ObjectMapper().readValue(json, type);
    }

    public static String getTransactionLandPayloadController(String transactionName, Object request)
            throws JsonProcessingException {

        return "Request landed at " + transactionName + " controller-" + Utility.objectToJSON(request);
    }

    public static String getTransactionResponseBackController(String transactionName, Object responseObject)
            throws JsonProcessingException {

        return "Response sending back from  " + transactionName + " controller to requestor  -> "
                + Utility.objectToJSON(responseObject);
    }

    public static String getTransactionLandPayloadService(String transactionName, Object payload)
            throws JsonProcessingException {

        return "Request landed at " + transactionName + " service with payload " + Utility.objectToJSON(payload);
    }

    public static String getTransactionResponseBackService(String transactionName, Object responseObject)
            throws JsonProcessingException {

        return "Response sending back from " + transactionName + " service to controller  -> "
                + Utility.objectToJSON(responseObject);
    }

    public static long getCurrentTimeInMills() {

        return System.currentTimeMillis();
    }

    public static LocalDateTime getCurrentDateTime() {
        // Current Date
        return LocalDateTime.now();
    }

    public static String getTransactionId(String msisdn) {

        return System.currentTimeMillis() + msisdn;
    }

    public static String getCurrentDate(String formatter) {
        // Create formatter
        DateTimeFormatter FOMATTER = DateTimeFormatter.ofPattern(formatter);

        // Local date time instance
        LocalDateTime localDateTime = LocalDateTime.now();

        // Get formatted String
        return FOMATTER.format(localDateTime);
    }

    public static String addHoursInCurrentDate(String formatter, int expiryHours) {

        // Create formatter
        DateTimeFormatter FOMATTER = DateTimeFormatter.ofPattern(formatter);

        // Local date time instance
        LocalDateTime localDateTime = LocalDateTime.now();

        // Get formatted String
        return FOMATTER.format(localDateTime.plusHours(expiryHours));
    }

    public static String addDaysInCurrentDate(String formatter, int days) {
        // Create formatter
        DateTimeFormatter FOMATTER = DateTimeFormatter.ofPattern(formatter);

        // Local date time instance
        LocalDateTime localDateTime = LocalDateTime.now();

        // Get formatted String
        return FOMATTER.format(localDateTime.plusDays(days));
    }

    public static String addMonthsInCurrentDate(String formatter, int months) {
        // Create formatter
        DateTimeFormatter FOMATTER = DateTimeFormatter.ofPattern(formatter);

        // Local date time instance
        LocalDateTime localDateTime = LocalDateTime.now();

        // Get formatted String
        return FOMATTER.format(localDateTime.plusMonths(months));
    }

    public static boolean isCSV(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        return fileName != null && fileName.toLowerCase().endsWith(".csv");
    }

    public static boolean isExcel(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        return fileName != null && (fileName.toLowerCase().endsWith(".xls")
                                     || fileName.toLowerCase().endsWith(".xlsx")
                                     || fileName.toLowerCase().endsWith(".xlsm"));
    }
}
