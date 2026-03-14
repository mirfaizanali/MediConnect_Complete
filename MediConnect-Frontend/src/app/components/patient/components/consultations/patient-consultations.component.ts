import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { PatientService } from '../../../../services/patient.service';
import { AuthService } from '../../../../services/auth.service';
import { PatientHistoryComponent } from '../../../doctor/components/patients/patient-history/patient-history.component';

@Component({
    selector: 'app-patient-consultations',
    standalone: true,
    imports: [CommonModule, PatientHistoryComponent],
    templateUrl: './patient-consultations.component.html',
    styleUrls: ['./patient-consultations.component.css']
})
export class PatientConsultationsComponent implements OnInit {
    patientId: number | null = null;
    loading = true;

    constructor(
        private patientService: PatientService,
    ) { }

    ngOnInit() {
        this.fetchPatientId();
    }

    fetchPatientId() {
        this.loading = true;
        this.patientService.getMyPatientProfile().subscribe({
            next: (profile) => {
                this.patientId = profile.patientId ? (typeof profile.patientId === 'string' ? parseInt(profile.patientId) : profile.patientId) : null;
                console.log("Fetched patient ID:", this.patientId);
                this.loading = false;
            },
            error: (err) => {
                console.error("Failed to fetch patient profile", err);
                this.loading = false;
            }
        });
    }


}
