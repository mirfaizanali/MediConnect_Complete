import { Component, Input, Output, EventEmitter, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DoctorService } from '../../../../../services/doctor.service';
import { PatientService } from '../../../../../services/patient.service';
import { PatientHistory, Appointment, Consultation, Prescription } from '../../../../../models/types';
import { Observable } from 'rxjs';

@Component({
    selector: 'app-patient-history',
    standalone: true,
    imports: [CommonModule],
    templateUrl: './patient-history.component.html',
    styleUrls: ['./patient-history.component.css']
})
export class PatientHistoryComponent implements OnInit {
    @Input() patientId: number | null = null;
    @Input() showBackButton: boolean = true;
    @Input() mode: 'doctor' | 'patient' = 'doctor';
    @Output() back = new EventEmitter<void>();

    history: PatientHistory | null = null;
    loading = true;
    activeTab: 'timeline' | 'appointments' | 'consultations' | 'prescriptions' = 'timeline';
    timelineEvents: any[] = [];

    constructor(
        private doctorService: DoctorService,
        private patientService: PatientService
    ) { }

    ngOnInit() {
        if (this.mode === 'patient') {
            this.fetchHistory(0);
        } else if (this.patientId) {
            this.fetchHistory(this.patientId);
        }
    }

    fetchHistory(id: number) {
        this.loading = true;

        let fetchObs: Observable<PatientHistory>;

        if (this.mode === 'patient') {
            fetchObs = this.patientService.getPatientHistory();
        } else {
            fetchObs = this.doctorService.getPatientHistory(id);
        }

        fetchObs.subscribe({
            next: (data) => {
                this.history = data;
                this.prepareTimeline();
                this.loading = false;
            },
            error: (err) => {
                console.error("Failed to fetch history", err);
                this.loading = false;
            }
        });
    }

    prepareTimeline() {
        if (!this.history) return;

        const appointments = this.history.appointments.map(a => ({
            ...a,
            type: 'appointment',
            id: `appointment-${a.appointmentId}`
        }));

        const consultations = this.history.consultations.map(c => ({
            ...c,
            type: 'consultation',
            id: `consultation-${c.consultationId}`
        }));

        const prescriptions = (this.history.prescriptions || []).map(p => ({
            ...p,
            console: console.log('Processing prescription:', p),
            type: 'prescription',
            id: `prescription-${p.prescriptionId}`,
            date: p.date || p.appointmentId?.toString() || new Date().toISOString()
        }));

        this.timelineEvents = [...appointments, ...consultations, ...prescriptions].sort((a, b) =>
            new Date(b.date).getTime() - new Date(a.date).getTime()
        );
    }

    setActiveTab(tab: 'timeline' | 'appointments' | 'consultations' | 'prescriptions') {
        this.activeTab = tab;
    }

    goBack() {
        this.back.emit();
    }

    getStatusColor(status: string) {
        if (!status) return '#6b7280';
        switch (status.toLowerCase()) {
            case 'completed': return '#10b981';
            case 'waiting': return '#f59e0b';
            case 'cancelled': return '#ef4444';
            default: return '#6b7280';
        }
    }

    getCompletedCount() {
        return this.history?.appointments.filter(a => a.status === 'Completed').length || 0;
    }

    getUniqueVisits() {
        if (!this.history) return 0;
        const dates = new Set([
            ...this.history.appointments.map(a => a.date),
            ...this.history.consultations.map(c => c.date)
        ]);
        return dates.size;
    }
}
