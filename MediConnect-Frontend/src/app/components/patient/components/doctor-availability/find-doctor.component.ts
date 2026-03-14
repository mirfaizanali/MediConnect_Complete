import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { PatientService } from '../../../../services/patient.service';
import { DoctorProfile, DoctorAvailability } from '../../../../models/types';
import { AuthService } from '../../../../services/auth.service';

@Component({
    selector: 'app-find-doctor',
    standalone: true,
    imports: [CommonModule, FormsModule],
    templateUrl: './find-doctor.component.html',
    styleUrls: ['./find-doctor.component.css']
})
export class FindDoctorComponent implements OnInit {
    doctors: DoctorProfile[] = [];
    filteredDoctors: DoctorProfile[] = [];

    searchQuery = '';
    sortBy: 'exp' | 'name' = 'exp'; // Default sort
    sortOrder: 'asc' | 'desc' = 'desc';

    selectedDoctor: DoctorProfile | null = null;
    selectedDate = '';
    selectedSlot = '';
    selectedAvailabilityId: number | null = null;
    reason = '';
    consultationMode: 'virtual' | 'in-person' = 'in-person';
    isBooking = false;

    availableDates: string[] = [];

    constructor(
        private patientService: PatientService    ) { }

    ngOnInit() {
        this.fetchDoctors();
    }

    fetchDoctors() {
        this.patientService.getAllDoctors().subscribe({
            next: (data) => {
                this.doctors = data;
                console.log('Fetched doctors:', this.doctors);
                this.filterAndSortDoctors();
            },
            error: (err) => console.error(err)
        });
    }

    onSearch() {
        this.filterAndSortDoctors();
    }

    onSortChange(value: string) {
        const [field, order] = value.split('-');
        this.sortBy = field as 'exp' | 'name';
        this.sortOrder = order as 'asc' | 'desc';
        this.filterAndSortDoctors();
    }

    toggleSort(field: 'exp' | 'name') {
        if (this.sortBy === field) {
            this.sortOrder = this.sortOrder === 'asc' ? 'desc' : 'asc';
        } else {
            this.sortBy = field;
            this.sortOrder = 'desc';
        }
        this.filterAndSortDoctors();
    }

    filterAndSortDoctors() {
        let filtered = this.doctors.filter(doc =>
            doc.name.toLowerCase().includes(this.searchQuery.toLowerCase()) ||
            doc.specialization.toLowerCase().includes(this.searchQuery.toLowerCase()) ||
            doc.qualification.toLowerCase().includes(this.searchQuery.toLowerCase())
        );

        filtered.sort((a, b) => {
            if (this.sortBy === 'exp') {
                return this.sortOrder === 'asc' ? a.exp - b.exp : b.exp - a.exp;
            } else {
                return this.sortOrder === 'asc'
                    ? a.name.localeCompare(b.name)
                    : b.name.localeCompare(a.name);
            }
        });

        this.filteredDoctors = filtered;
    }

    setSelectedDoctor(doc: DoctorProfile) {
        this.selectedDoctor = doc;
        this.selectedDate = '';
        this.selectedSlot = '';
        this.selectedAvailabilityId = null;
        this.reason = '';
        this.consultationMode = 'in-person';

        if (doc.availability) {
            const dates = new Set(doc.availability.map(a => a.date));
            this.availableDates = Array.from(dates).sort();
        } else {
            this.availableDates = [];
        }
    }

    closeModal() {
        this.selectedDoctor = null;
    }

    selectDate(date: string) {
        this.selectedDate = date;
        this.selectedSlot = '';
        this.selectedAvailabilityId = null;
    }

    getAvailableSlotsForDate(date: string) {
        if (!this.selectedDoctor || !this.selectedDoctor.availability) return [];
        return this.selectedDoctor.availability.filter(a => a.date === date);
    }

    selectSlot(slot: DoctorAvailability) {
        this.selectedSlot = slot.timeSlot;
        this.selectedAvailabilityId = slot.availabilityId;
    }

handleBookAppointment() {
    if (!this.selectedAvailabilityId || !this.reason.trim()) {
        alert('Please select a time slot and provide a reason.');
        return;
    }

    this.isBooking = true;

    const payload = {
        reason: this.reason.trim(),
        availabilityId: this.selectedAvailabilityId
    };

    this.patientService.bookAppointment(payload).subscribe({
        next: (response) => {
            this.isBooking = false;
            this.closeModal();
        
            this.fetchDoctors(); 
            
            this.reason = '';
            this.selectedAvailabilityId = null;
            
            alert('Appointment booked successfully!');
        },
        error: (err) => {
            this.isBooking = false;
            console.error('Booking Error:', err);
            
            const message = err.error?.message || 'Failed to book appointment.';
            alert(message);
        }
    });
}
}
