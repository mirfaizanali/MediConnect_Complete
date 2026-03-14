import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map, switchMap } from 'rxjs';
import { Consultation, Appointment, Prescription } from '../models/types';
import { environment } from '../../environments/environment';


@Injectable({
    providedIn: 'root'
})
export class ConsultationService {
    private apiUrl = environment.apiUrl;

    constructor(
        private http: HttpClient
    ) { }

    getMyConsultations(): Observable<Consultation[]> {
        return this.http.get<any>(`${this.apiUrl}/patient-consultations`).pipe(
            map(response => response.data || [])
        );
    }

    getMyAppointments(): Observable<Appointment[]> {
        return this.http.get<any>(`${this.apiUrl}/appointments/patient`).pipe(
            map(response => response.data || [])
        );
    }

    getMyPrescriptions(): Observable<Prescription[]> {
        return this.http.get<any>(`${this.apiUrl}/prescription/patient`).pipe(
            map(response => {
                const data = response.data || [];
                return data.map((p: any) => ({
                    ...p,
                    medicines: p.medicines ? JSON.parse(p.medicines) : []
                }));
            })
        );
    }

    updatePrescription(prescriptionId: string | number, prescription: Partial<Prescription>): Observable<Prescription> {
        return this.http.patch<Prescription>(`${this.apiUrl}/prescriptions/${prescriptionId}`, prescription);
    }
}
