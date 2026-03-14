import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { DoctorService } from '../../../../services/doctor.service';
import { DoctorProfile } from '../../../../models/types';

@Component({
    selector: 'app-doctor-profile',
    standalone: true,
    imports: [CommonModule, FormsModule],
    templateUrl: './doctor-profile.component.html',
    styleUrls: ['./doctor-profile.component.css']
})
export class DoctorProfileComponent implements OnInit {
    doc: DoctorProfile | null = null;
    loading = true;
    error: string | null = null;

    editMode = false;
    showChangePassword = false;

    formData: Partial<DoctorProfile> = {};
    passwordData = {
        currentPassword: '',
        newPassword: '',
        confirmPassword: ''
    };

    updateLoading = false;
    updateError: string | null = null;
    updateSuccess = false;

    passwordError: string | null = null;
    passwordSuccess = false;

    constructor(private doctorService: DoctorService) { }

    ngOnInit() {
        this.fetchDoctor();
    }

    fetchDoctor() {
        this.loading = true;
        this.doctorService.getMyDoctorProfile().subscribe({
            next: (data) => {
                this.doc = data;
                this.loading = false;
            },
            error: (err) => {
                this.error = 'Failed to fetch doctor profile';
                this.loading = false;
                console.error(err);
            }
        });
    }

    handleEditClick() {
        if (this.doc) {
            this.formData = {
                name: this.doc.name,
                specialization: this.doc.specialization,
                qualification: this.doc.qualification,
                exp: this.doc.exp,
            };
            this.setEditMode(true);
        }
    }

    setEditMode(value: boolean) {
        this.editMode = value;
        if (value) {
            this.showChangePassword = false;
            this.updateSuccess = false;
        }
    }

    setShowChangePassword(value: boolean) {
        this.showChangePassword = value;
        if (value) {
            this.editMode = false;
            this.passwordData = {
                currentPassword: '',
                newPassword: '',
                confirmPassword: ''
            };
            this.passwordSuccess = false;
            this.passwordError = null;
        }
    }

    handleChangePasswordClick() {
        this.setShowChangePassword(true);
    }

    handleSubmit(e: Event) {
        e.preventDefault();
        this.updateLoading = true;
        this.updateError = null;

        this.doctorService.updateMyDoctorProfile(this.formData).subscribe({
            next: (updatedDoc) => {
                this.doc = updatedDoc;
                this.updateSuccess = true;
                this.setEditMode(false);
                this.updateLoading = false;
            },
            error: (err) => {
                this.updateError = 'Failed to update profile';
                this.updateLoading = false;
                console.error(err);
            }
        });
    }

    handlePasswordSubmit(e: Event) {
        e.preventDefault();
        this.passwordError = null;
        this.passwordSuccess = false;

        if (this.passwordData.newPassword !== this.passwordData.confirmPassword) {
            this.passwordError = "New passwords don't match";
            return;
        }

        this.updateLoading = true;
        this.doctorService.changeMyPassword(this.passwordData).subscribe({
            next: () => {
                this.passwordSuccess = true;
                this.updateLoading = false;
                setTimeout(() => {
                    this.setShowChangePassword(false);
                }, 2000);
            },
            error: (err) => {
                this.passwordError = 'Failed to change password';
                this.updateLoading = false;
                console.error(err);
            }
        });
    }
}
