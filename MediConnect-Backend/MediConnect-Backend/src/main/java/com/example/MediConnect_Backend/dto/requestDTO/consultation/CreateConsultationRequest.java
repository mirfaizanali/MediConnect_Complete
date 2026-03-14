package com.example.MediConnect_Backend.dto.requestDTO.consultation;

import com.example.MediConnect_Backend.entity.Consultation;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateConsultationRequest {
    @NotBlank(message = "Symptoms are required")
    private String symptoms;

    private String bloodPressure;
    private Integer height;
    private Integer weight;

    @NotBlank(message = "Description/Diagnosis is required")
    private String description;

    private String notes;

    @NotNull(message = "Status is required")
    private Consultation.Status status;

    public CreateConsultationRequest() {
        this.symptoms = symptoms;
        this.bloodPressure = bloodPressure;
        this.height = height;
        this.weight = weight;
        this.description = description;
        this.notes = notes;
        this.status = status;
    }

    public String getSymptoms() {
        return symptoms;
    }

    public void setSymptoms(String symptoms) {
        this.symptoms = symptoms;
    }

    public String getBloodPressure() {
        return bloodPressure;
    }

    public void setBloodPressure(String bloodPressure) {
        this.bloodPressure = bloodPressure;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Consultation.Status getStatus() {
        return status;
    }

    public void setStatus(Consultation.Status status) {
        this.status = status;
    }
}
