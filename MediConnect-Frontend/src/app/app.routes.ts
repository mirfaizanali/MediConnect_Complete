import { Routes } from '@angular/router';
import { LoginComponent } from './auth/login/login.component';
import { RegisterPatientComponent } from './auth/register-patient/register-patient.component';
import { RegisterDoctorComponent } from './auth/register-doctor/register-doctor.component';
import { authGuard } from './auth/auth.guard';

export const routes: Routes = [
    { path: '', component:LoginComponent },
    { path: 'login', component: LoginComponent },
    { path: 'register/patient', component: RegisterPatientComponent },
    { path: 'register/doctor', component: RegisterDoctorComponent },
    {
        path: 'dashboard/patient',
        loadComponent: () => import('./components/patient/dashboard/patient-dashboard.component').then(m => m.PatientDashboardComponent),
        canActivate: [authGuard],
        data: { role: 'patient' }
    },
    {
        path: 'dashboard/doctor',
        loadComponent: () => import('./components/doctor/dashboard/doctor-dashboard.component').then(m => m.DoctorDashboardComponent),
        canActivate: [authGuard],
        data: { role: 'doctor' }
    },
    { path: '**', redirectTo: '' }
];
