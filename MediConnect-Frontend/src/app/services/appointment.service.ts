import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map, switchMap } from 'rxjs';
import { Appointment } from '../models/types';
import { AuthService } from './auth.service';
import { PatientService } from './patient.service';
import { environment } from '../../environments/environment';


@Injectable({
    providedIn: 'root'
})
export class AppointmentService {
    private apiUrl = environment.apiUrl;

    constructor(
        private http: HttpClient,
        private authService: AuthService,
        private patientService: PatientService
    ) { }


    getDoctorAppointments(): Observable<Appointment[]> {
        return this.http.get<any>(`${this.apiUrl}/appointments/doctor`).pipe(
            map(response => {
                return response.data || [];
            })
        );
    }

    updateAppointmentStatus(id: number | string, status: { status: string }): Observable<Appointment> {

        return this.http.patch<any>(`${this.apiUrl}/appointments/${id}/status`, status).pipe(
            map(response => {
                return response.data || response;
            })
        );
    }


    getUpcomingPatientAppointments(): Observable<Appointment[]> {
        return this.http.get<any>(`${this.apiUrl}/patient-appointments/upcoming`).pipe(
            map(response => {
                return response.data || [];
            }),);
    }

    getPatientAppointmentHistory(): Observable<Appointment[]> {
        return this.http.get<any>(`${this.apiUrl}/patient-appointments/history`).pipe(
            map(response => {
                return response.data || [];
            }),);
    }

    getAllPatientAppointments(): Observable<Appointment[]> {
        return this.http.get<any>(`${this.apiUrl}/appointments/patient`).pipe(
            map(response => response.data || [])
        );
    }

    cancelPatientAppointment(id: number | string): Observable<any> {
        return this.http.patch(`${this.apiUrl}/patient-appointments/${id}/cancel`, { status: 'Cancelled' });
    }

    updateAppointmentReason(id: number | string, data: { reason: string }): Observable<any> {
        return this.http.patch(`${this.apiUrl}/patient-appointments/${id}/reason`, data);
    }
}
