import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../../services/auth.service';
import { PatientAppointmentsComponent } from '../components/appointments/patient-appointments.component';
import { FindDoctorComponent } from '../components/doctor-availability/find-doctor.component';
import { PatientConsultationsComponent } from '../components/consultations/patient-consultations.component';
import { PatientProfileComponent } from '../components/profile/patient-profile.component';
import { PatientNotificationsComponent } from '../components/notifications/patient-notifications.component';

@Component({
    selector: 'app-patient-dashboard',
    standalone: true,
    imports: [
        CommonModule,
        PatientAppointmentsComponent,
        FindDoctorComponent,
        PatientConsultationsComponent,
        PatientProfileComponent,
        PatientNotificationsComponent
    ],
    templateUrl: './patient-dashboard.component.html',
    styleUrls: ['./patient-dashboard.component.css']
})
export class PatientDashboardComponent implements OnInit {
    activeTab: 'appointments' | 'find-doctor' | 'consultations' | 'profile' | 'notifications' = 'appointments';
    userName = 'Patient';

    constructor(private authService: AuthService) { }

    ngOnInit() {
        const user = this.authService.getCurrentUser();

        if (user) {
            this.userName = user.name;
        }
    }

    setActiveTab(tab: any) {
        this.activeTab = tab;
    }

    getTabTitle() {
        switch (this.activeTab) {
            case 'appointments': return 'My Appointments';
            case 'find-doctor': return 'Find a Doctor';
            case 'consultations': return 'Consultations';
            case 'profile': return 'My Profile';
            case 'notifications': return 'Notifications';
            default: return 'Dashboard';
        }
    }

    getInitials(name: string) {
        return name.split(' ').map(n => n[0]).join('').toUpperCase().substring(0, 2);
    }

    logout() {
        this.authService.logout();
    }
}
