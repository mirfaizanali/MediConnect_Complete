import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { PatientService } from '../../../../services/patient.service';
import { AuthService } from '../../../../services/auth.service';
import { PatientProfile, UpdatePatientProfilePayload } from '../../../../models/types';

@Component({
    selector: 'app-patient-profile',
    standalone: true,
    imports: [CommonModule, FormsModule],
    templateUrl: './patient-profile.component.html',
    styleUrls: ['./patient-profile.component.css']
})
export class PatientProfileComponent implements OnInit {
    patient: PatientProfile | null = null;
    loading = true;
    error: string | null = null;

    editMode = false;
    showChangePassword = false;

    formData: UpdatePatientProfilePayload | null = null;

    passwordData = {
        oldPassword: '',
        newPassword: '',
        confirmPassword: ''
    };

    updateLoading = false;
    passwordError: string | null = null;
    passwordSuccess = false;

    constructor(
        private patientService: PatientService,
        private authService: AuthService
    ) { }

    ngOnInit() {
        this.fetchPatient();
    }

    fetchPatient() {
        this.loading = true;
        this.patientService.getMyPatientProfile().subscribe({
            next: (data) => {
                this.patient = data;
                this.loading = false;
            },
            error: (err) => {
                this.error = 'Failed to fetch patient data';
                this.loading = false;
                console.error(err);
            }
        });
    }

    handleEditClick() {
        if (this.patient) {
            this.formData = {
                name: this.patient.name,
                age: this.patient.age,
                bloodGroup: this.patient.bloodGroup,
                phoneNumber: this.patient.phoneNumber,
                address: this.patient.address,
                gender: this.patient.gender,
            };
            this.editMode = true;
            this.showChangePassword = false;
        }
    }

    setEditMode(value: boolean) {
        this.editMode = value;
    }

    handleChangePasswordClick() {
        this.showChangePassword = true;
        this.editMode = false;
        this.passwordData = {
            oldPassword: '',
            newPassword: '',
            confirmPassword: ''
        };
        this.passwordError = null;
        this.passwordSuccess = false;
    }

    handleUpdate() {
        if (!this.formData) return;
        this.updateLoading = true;

        this.patientService.updateMyPatientProfile(this.formData).subscribe({
            next: (updated) => {
                this.patient = updated;
                this.editMode = false;
                this.updateLoading = false;
                alert("Profile updated successfully!");
            },
            error: (err) => {
                this.error = 'Failed to update profile';
                this.updateLoading = false;
                console.error(err);
            }
        });
    }

    handlePasswordSubmit() {
        this.passwordError = null;
        this.passwordSuccess = false;

        if (this.passwordData.newPassword !== this.passwordData.confirmPassword) {
            this.passwordError = "New passwords don't match";
            return;
        }

        if (this.passwordData.newPassword.length < 6) {
            this.passwordError = "New password must be at least 6 characters long";
            return;
        }

        
        console.log("Password change requested", this.passwordData);
        this.passwordSuccess = true;

        setTimeout(() => {
            this.showChangePassword = false;
            this.passwordSuccess = false;
        }, 2000);

        
    }
}
