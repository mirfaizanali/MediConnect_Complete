import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AppointmentService } from '../../../../services/appointment.service';
import { AuthService } from '../../../../services/auth.service';
import { Appointment } from '../../../../models/types';

@Component({
    selector: 'app-patient-appointments',
    standalone: true,
    imports: [CommonModule, FormsModule],
    templateUrl: './patient-appointments.component.html',
    styleUrls: ['./patient-appointments.component.css']
})
export class PatientAppointmentsComponent implements OnInit {
    selectedTab: 'upcoming' | 'past' = 'upcoming';
    appointments: Appointment[] = [];
    paginatedAppointments: Appointment[] = [];
    loading = true;
    error: string | null = null;

    // Pagination
    currentPage = 1;
    itemsPerPage = 5;
    totalItems = 0;
    totalPages = 0;
    pageNumbers: number[] = [];

    // Modal
    isModalOpen = false;
    editingAppointment: Appointment | null = null;
    newReason = '';

    constructor(
        private appointmentService: AppointmentService,
        private authService: AuthService
    ) { }

    ngOnInit() {
        this.fetchAppointments();
    }

    fetchAppointments() {
        const user = this.authService.getCurrentUser();
        if (!user) return;

        this.loading = true;
        this.error = null;

        let fetchObservable;
        if (this.selectedTab === 'upcoming') {
            fetchObservable = this.appointmentService.getUpcomingPatientAppointments();
        } else {
            fetchObservable = this.appointmentService.getPatientAppointmentHistory();
        }

        fetchObservable.subscribe({
            next: (data) => {
                
                this.appointments = data.sort((a, b) => {
                    const dateA = new Date(a.date + ' ' + (a.timeSlot || '00:00'));
                    const dateB = new Date(b.date + ' ' + (b.timeSlot || '00:00'));
                    return dateB.getTime() - dateA.getTime(); 
                });
                this.totalItems = this.appointments.length;
                this.currentPage = 1;
                this.updatePagination();
                this.loading = false;
            },
            error: (err) => {
                console.error(err);
                this.error = 'Failed to fetch appointments';
                this.loading = false;
            }
        });
    }

    setSelectedTab(tab: 'upcoming' | 'past') {
        this.selectedTab = tab;
        this.fetchAppointments();
    }

    updatePagination() {
        this.totalPages = Math.ceil(this.totalItems / this.itemsPerPage);
        this.pageNumbers = Array.from({ length: this.totalPages }, (_, i) => i + 1);

        const startIndex = (this.currentPage - 1) * this.itemsPerPage;
        this.paginatedAppointments = this.appointments.slice(startIndex, startIndex + this.itemsPerPage);
    }

    onItemsPerPageChange(newVal: number) {
        this.itemsPerPage = newVal;
        this.currentPage = 1;
        this.updatePagination();
    }

    goToPage(page: number) {
        if (page >= 1 && page <= this.totalPages) {
            this.currentPage = page;
            this.updatePagination();
        }
    }

    min(a: number, b: number) {
        return Math.min(a, b);
    }

    getStatusDetails(status: string) {
        switch (status) {
            case 'Booked':
                return { color: 'var(--hc-success)', text: 'Confirmed', bgColor: 'var(--hc-success-light)' };
            case 'Waiting':
                return { color: 'var(--hc-orange)', text: 'Pending', bgColor: 'rgba(245, 158, 11, 0.1)' };
            case 'Completed':
                return { color: 'var(--hc-gray-light)', text: 'Completed', bgColor: 'var(--hc-green)' };
            case 'Cancelled':
                return { color: 'var(--hc-alert)', text: 'Cancelled', bgColor: 'rgba(220, 53, 69, 0.1)' };
            default:
                return { color: 'var(--hc-gray-text)', text: status, bgColor: 'var(--hc-gray-light)' };
        }
    }

    handleCancel(appointment: Appointment) {
        const appointmentId = appointment.id || appointment.appointmentId;
        if (!appointmentId) {
            console.error('Appointment ID not found', appointment);
            alert('Appointment ID not found. Please refresh the page.');
            return;
        }
        if (confirm('Are you sure you want to cancel this appointment?')) {
            this.appointmentService.cancelPatientAppointment(appointmentId).subscribe({
                next: () => {
                    this.fetchAppointments();
                },
                error: (err) => {
                    console.error("Cancellation failed", err);
                    alert('Failed to cancel appointment. Please try again.');
                }
            });
        }
    }

    openUpdateModal(app: Appointment) {
        this.editingAppointment = app;
        this.newReason = app.reason;
        this.isModalOpen = true;
    }

    closeUpdateModal() {
        this.isModalOpen = false;
        this.editingAppointment = null;
        this.newReason = '';
    }

    handleUpdateReason() {
        if (!this.editingAppointment || !this.newReason.trim()) return;
        if (this.newReason.trim().length < 5) {
            alert("Reason must be at least 5 characters long.");
            return;
        }

        const appointmentId = this.editingAppointment.id || this.editingAppointment.appointmentId;
        if (!appointmentId) {
            console.error('Appointment ID not found', this.editingAppointment);
            alert('Appointment ID not found. Please refresh the page.');
            return;
        }

        this.appointmentService.updateAppointmentReason(appointmentId, { reason: this.newReason.trim() }).subscribe({
            next: () => {
                this.closeUpdateModal();
                this.fetchAppointments();
            },
            error: (err) => {
                console.error("Update failed", err);
                alert('Failed to update appointment reason. Please try again.');
            }
        });
    }
}
