package com.rtechnologies.soies.model.dto;

import java.util.Date;

public class OgaDTO {
    private Long ogaId;
    private String ogaTitle;
    private String description;
    private Date dueDate;
    private int totalMarks;
    private boolean visibility;
    private String term;
    private String time;
    private boolean hasAttempted;  // Field to track if the OGA has been attempted

    // Getters and setters
    public Long getOgaId() {
        return ogaId;
    }

    public void setOgaId(Long ogaId) {
        this.ogaId = ogaId;
    }

    public String getOgaTitle() {
        return ogaTitle;
    }

    public void setOgaTitle(String ogaTitle) {
        this.ogaTitle = ogaTitle;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public int getTotalMarks() {
        return totalMarks;
    }

    public void setTotalMarks(int totalMarks) {
        this.totalMarks = totalMarks;
    }

    public boolean isVisibility() {
        return visibility;
    }

    public void setVisibility(boolean visibility) {
        this.visibility = visibility;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public boolean isHasAttempted() {
        return hasAttempted;
    }

    public void setHasAttempted(boolean hasAttempted) {
        this.hasAttempted = hasAttempted;
    }
}
