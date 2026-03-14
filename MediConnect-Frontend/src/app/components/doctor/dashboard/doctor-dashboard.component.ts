import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../../services/auth.service';
import { DocAppointmentsComponent } from '../components/appointments/doc-appointments.component';
import { DocAvailabilityComponent } from '../components/availability/doc-availability.component';
import { DoctorPatientPanelComponent } from '../components/patients/doctor-patient-panel.component';
import { DocNotificationComponent } from '../components/notifications/doc-notification.component';
import { DoctorProfileComponent } from '../components/profile/doctor-profile.component';
import { Router } from '@angular/router';

@Component({
  selector: 'app-doctor-dashboard',
  standalone: true,
  imports: [
    CommonModule,
    DocAppointmentsComponent,
    DocAvailabilityComponent,
    DoctorPatientPanelComponent,
    DocNotificationComponent,
    DoctorProfileComponent
  ],
  templateUrl: './doctor-dashboard.component.html',
  styleUrls: ['./doctor-dashboard.css']
})
export class DoctorDashboardComponent {
  activeTab = 'appointments';
  doctorName = '';

  constructor(private authService: AuthService, private router: Router) {
    const user = this.authService.getCurrentUser();
    this.doctorName = user ? user.name : 'Doctor';
  }

  setActiveTab(tab: string) {
    this.activeTab = tab;
  }

  getTabTitle() {
    switch (this.activeTab) {
      case 'appointments': return 'Manage Appointments';
      case 'patients': return 'My Patients';
      case 'availability': return 'Schedule & Availability';
      case 'profile': return 'My Profile';
      default: return 'Dashboard';
    }
  }

  getInitials() {
    if (!this.doctorName) return 'Dr';
    const name = this.doctorName.replace(/^Dr\.\s+/, '');
    return name.charAt(0).toUpperCase();
  }

  logout() {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
