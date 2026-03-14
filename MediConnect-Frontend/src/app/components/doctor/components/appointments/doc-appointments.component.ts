import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { AppointmentService } from '../../../../services/appointment.service';
import { Appointment, Prescription } from '../../../../models/types';
import { AuthService } from '../../../../services/auth.service';
import { DoctorService } from '../../../../services/doctor.service';

@Component({
    selector: 'app-doc-appointments',
    standalone: true,
    imports: [CommonModule, FormsModule],
    templateUrl: './doc-appointments.component.html',
    styleUrls: ['./doc-appointments.component.css']
})
export class DocAppointmentsComponent implements OnInit {
    appointments: Appointment[] = [];
    filteredAppointments: Appointment[] = [];
    paginatedAppointments: Appointment[] = [];

    searchTerm = '';
    statusFilter = 'ALL';
    isLoading = true;

    currentPage = 1;
    rowsPerPage = 4;
    totalPages = 0;
    pageNumbers: number[] = [];

    isPrescriptionModalOpen = false;
    currentAppointment: Appointment | null = null;
    prescriptionMedicines: any[] = [{ name: '', dosage: '', frequency: '' }];
    prescriptionNotes = '';
    isSavingPrescription = false;

    isVideoCallModalOpen = false;
    jitsiApi: any = null;

    constructor(
        private appointmentService: AppointmentService,
        private authService: AuthService,
        private doctorService: DoctorService,
        private http: HttpClient
    ) { }

    ngOnInit() {
        this.fetchAppointments();
    }

    fetchAppointments() {
        const user = this.authService.getCurrentUser();
        if (!user) return;

        this.isLoading = true;
        this.appointmentService.getDoctorAppointments().subscribe({
            next: (data) => {

                this.appointments = data;
                this.filterAppointments();
                this.isLoading = false;
            },
            error: (err) => {
                console.error('Error fetching appointments:', err);
                this.isLoading = false;
            }
        });
    }

    onSearchTermChange(term: string) {
        this.searchTerm = term;
        this.filterAppointments();
    }

    onStatusFilterChange(status: string) {
        this.statusFilter = status;
        this.filterAppointments();
    }

    filterAppointments() {
        let filtered = this.appointments.filter(appointment => {
            const lowerSearchTerm = this.searchTerm.toLowerCase();
            const matchesSearch =
                (appointment.patientName && appointment.patientName.toLowerCase().includes(lowerSearchTerm)) ||
                (appointment.reason && appointment.reason.toLowerCase().includes(lowerSearchTerm));

            const matchesStatus =
                this.statusFilter === 'ALL' || appointment.status === this.statusFilter;

            return matchesSearch && matchesStatus;
        });


        filtered.sort((a, b) => {
            const dateA = new Date(a.date + ' ' + a.timeSlot);
            const dateB = new Date(b.date + ' ' + b.timeSlot);
            return dateB.getTime() - dateA.getTime(); 
        });

        this.filteredAppointments = filtered;
        this.currentPage = 1;
        this.updatePagination();
    }

    updatePagination() {
        this.totalPages = Math.ceil(this.filteredAppointments.length / this.rowsPerPage);
        this.pageNumbers = Array.from({ length: this.totalPages }, (_, i) => i + 1);
        const startIndex = (this.currentPage - 1) * this.rowsPerPage;
        this.paginatedAppointments = this.filteredAppointments.slice(startIndex, startIndex + this.rowsPerPage);
    }

    goToPage(page: number) {
        if (page >= 1 && page <= this.totalPages) {
            this.currentPage = page;
            this.updatePagination();
        }
    }

    goToPrevious() {
        this.goToPage(this.currentPage - 1);
    }

    goToNext() {
        this.goToPage(this.currentPage + 1);
    }

    min(a: number, b: number) {
        return Math.min(a, b);
    }

    handleUpdateStatus(appointment: Appointment, newStatus: string) {
        if (newStatus === 'Completed') {
            this.openPrescriptionModal(appointment);
            return;
        }

        this.updateAppointmentStatus(appointment, newStatus);
    }

    updateAppointmentStatus(appointment: Appointment, newStatus: string) {

        const appointmentId = appointment.id || appointment.appointmentId;

        if (!appointmentId) {
            console.error('Appointment ID not found', appointment);
            alert('Appointment ID not found. Please refresh the page.');
            return;
        }

        this.appointmentService.updateAppointmentStatus(appointmentId, { status: newStatus }).subscribe({
            next: (updated) => {
                const index = this.appointments.findIndex(a => {
                    if (a.id && appointment.id) {
                        return a.id === appointment.id;
                    }
                    return a.appointmentId === appointment.appointmentId;
                });
                if (index !== -1) {
                    this.appointments[index] = { ...this.appointments[index], status: newStatus as any };
                    this.filterAppointments();
                } else {
                    this.fetchAppointments();
                }
            },
            error: (err) => {
                console.error('Error updating status', err);
                alert('Failed to update appointment status. Please try again.');
            }
        });
    }

    openPrescriptionModal(appointment: Appointment) {
        this.currentAppointment = appointment;
        this.prescriptionMedicines = [{ name: '', dosage: '', frequency: '' }];
        this.prescriptionNotes = '';
        this.isPrescriptionModalOpen = true;
    }

    closePrescriptionModal() {
        this.isPrescriptionModalOpen = false;
        this.currentAppointment = null;
        this.prescriptionMedicines = [{ name: '', dosage: '', frequency: '' }];
        this.prescriptionNotes = '';
    }

    addMedicine() {
        this.prescriptionMedicines.push({ name: '', dosage: '', frequency: '' });
    }

    removeMedicine(index: number) {
        if (this.prescriptionMedicines.length > 1) {
            this.prescriptionMedicines.splice(index, 1);
        }
    }

savePrescription() {
    if (!this.currentAppointment) return;

    const validMedicines = this.prescriptionMedicines.filter(m => 
        m.name?.trim() && m.dosage?.trim() && m.frequency?.trim()
    );

    if (validMedicines.length === 0) {
        alert('Please add at least one medicine.');
        return;
    }

    this.isSavingPrescription = true;

const unquote = (str: string) => str ? str.replace(/^["']|["']$/g, '').trim() : '';

const prescription: Prescription = {
    appointmentId: Number(this.currentAppointment.appointmentId),
    patientId: this.currentAppointment.patientId,
        
    medicines: unquote(validMedicines[0].name),
    notes: this.prescriptionNotes.trim(),
    dosage: unquote(validMedicines[0].dosage), 
    frequency: unquote(validMedicines[0].frequency),
    
    date: new Date().toISOString().split('T')[0]
};

    this.doctorService.createPrescription(prescription).subscribe({
        next: () => {
            this.updateAppointmentStatus(this.currentAppointment!, 'Completed');
            this.closePrescriptionModal();
            this.isSavingPrescription = false;
        },
        error: (err) => {
            console.error('Error saving prescription', err);
            this.isSavingPrescription = false;
        }
    });
}

    handleConsultation(appointment: Appointment) {
        const appointmentId = appointment.id || appointment.appointmentId;
        if (!appointmentId) {
            console.error('Appointment ID not found', appointment);
            return;
        }

        if (appointment.consultationMode !== 'virtual') {
            alert('Video consultation is only available for virtual appointments.');
            return;
        }

        this.currentAppointment = appointment;
        this.isVideoCallModalOpen = true;

        setTimeout(() => {
            this.initializeJitsiMeet(appointment);
        }, 100);

        this.sendVideoCallNotification(appointment);
    }

    sendVideoCallNotification(appointment: Appointment) {
        const notification = {
            userId: appointment.patientId,
            message: `Your video consultation with Dr. ${appointment.doctorName} is starting now.`,
            read: false,
            createdAt: new Date().toISOString(),
            type: 'video_call',
            roomName: `medi-connect-${appointment.id || appointment.appointmentId}`
        };

        this.http.post('/api/notifications', notification).subscribe({
            next: () => {
                console.log('Video call notification sent to patient');
            },
            error: (err) => {
                console.error('Failed to send notification:', err);
            }
        });
    }

    initializeJitsiMeet(appointment: Appointment) {
        const domain = '8x8.vc';
        const options = {
            roomName: `medi-connect-${appointment.id || appointment.appointmentId}`,
            width: '100%',
            height: 500,
            parentNode: document.querySelector('#jitsi-container'),
            userInfo: {
                displayName: `Dr. ${appointment.doctorName || 'Doctor'}`
            },
            configOverwrite: {
                startWithAudioMuted: false,
                startWithVideoMuted: false,
                enableWelcomePage: false,
                prejoinPageEnabled: false
            },
            interfaceConfigOverwrite: {
                SHOW_JITSI_WATERMARK: false,
                SHOW_WATERMARK_FOR_GUESTS: false,
                DEFAULT_BACKGROUND: '#f8f9fa'
            }
        };

        // @ts-ignore
        this.jitsiApi = new JitsiMeetExternalAPI(domain, options);

        this.jitsiApi.addEventListener('readyToClose', () => {
            this.closeVideoCallModal();
        });
    }

    closeVideoCallModal() {
        if (this.jitsiApi) {
            this.jitsiApi.dispose();
            this.jitsiApi = null;
        }
        this.isVideoCallModalOpen = false;
        this.currentAppointment = null;
    }
}
