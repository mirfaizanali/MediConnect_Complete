import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, throwError, map, forkJoin, switchMap } from 'rxjs';
import {
  DoctorAvailability,
  GenerateSchedulePayload,
  MarkBreakPayload,
  UpdateSlotStatusPayload,
  PatientForDoctor,
  PatientHistory,
  DoctorProfile,
  Prescription,
} from '../models/types';
import { AuthService } from './auth.service';
import { catchError } from 'rxjs/operators';
import { HttpErrorResponse, HttpParams } from '@angular/common/http';
import { environment } from '../../environments/environment';


@Injectable({
  providedIn: 'root',
})
export class DoctorService {
  private apiUrl = environment.apiUrl;

  constructor(
    private http: HttpClient,
    private authService: AuthService,
  ) { }

  getDoctorAvailabilityForDate(date: string): Observable<DoctorAvailability[]> {
    return this.http.get<any>(`${this.apiUrl}/doctors/availability/date/${date}`).pipe(
      map((response) => response.data || []), 
    );
  }

  getAllDoctorAvailability(): Observable<DoctorAvailability[]> {
    return this.http
      .get<any>(`${this.apiUrl}/doctors/availability`)
      .pipe(map((response) => response.data || []));
  }
  generateDoctorSchedule(payload: GenerateSchedulePayload): Observable<any> {
    const user = this.authService.getCurrentUser();
    if (!user) {
      return throwError(() => new Error('User not authenticated'));
    }

    return this.http.post<any>(`${this.apiUrl}/doctors/availability/generate-schedule`, payload);
  }

  markDoctorBreak(payload: { date: string; startTime: string; endTime: string }): Observable<any> {
    return this.http.put<any>(`${this.apiUrl}/doctors/availability/mark-break`, payload);
  }
  updateSlotStatus(slotId: number): Observable<any> {
    return this.http.patch(`${this.apiUrl}/doctors/availability/${slotId}/toggle-status`, {});
  }

  clearDoctorAvailabilityForDate(date: string): Observable<any> {
    return this.http.delete(`${this.apiUrl}/doctors/availability/clear/${date}`, {});
  }

  getAssociatedPatients(): Observable<PatientForDoctor[]> {
    return this.http.get<any>(`${this.apiUrl}/doctor-panel/patients`).pipe(
      map((response) => {
        return response.data || [];
      }),
    );
  }

  searchPatients(name?: string, email?: string): Observable<PatientForDoctor[]> {
    let params = new HttpParams();

    if (name) {
      params = params.set('name', name);
    }
    if (email) {
      params = params.set('email', email);
    }

    return this.http
      .get<any>(`${this.apiUrl}/doctor-panel/patients/search`, { params })
      .pipe(map((response) => response.data || response));
  }

  getPatientHistory(patientId: number): Observable<PatientHistory> {

    return this.http.get<any>(`${this.apiUrl}/doctor-panel/patients/${patientId}/history`).pipe(
      map(response => {
        const history = response.data;

        return {
          patientProfile: history.patientProfile || {},
          appointments: history.appointments || [],
          consultations: history.consultations || [],
          prescriptions: history.prescriptions || []
        } as PatientHistory;
      }),
      catchError((error: HttpErrorResponse) => {
        console.error('Error fetching patient history:', error);
        return throwError(() => error);
      })
    );
  }

  getMyDoctorProfile(): Observable<DoctorProfile> {

    return this.http.get<DoctorProfile>(`${this.apiUrl}/doctors/user`).pipe(
      map((profile) => {
        if (!profile) throw new Error('Doctor profile not found');
        return profile;
      }),
    );
  }

  updateMyDoctorProfile(data: Partial<DoctorProfile>): Observable<DoctorProfile> {
    const user = this.authService.getCurrentUser();
    return this.http.put<DoctorProfile>(`${this.apiUrl}/doctors/user`, data);
  }

  changeMyPassword(data: any): Observable<any> {
    const user = this.authService.getCurrentUser();
    return this.http.put<any>(`${this.apiUrl}/doctors/user/change-password`, {
      password: data.newPassword,
    });
  }
createPrescription(prescription: Partial<Prescription>): Observable<Prescription> {
  const appointmentId = prescription.appointmentId;
  

  const payload = {
    medicines: JSON.stringify(prescription.medicines),
    notes: prescription.notes,
    date: prescription.date,
    dosage: prescription.dosage,   
    frequency: prescription.frequency 
  };

  return this.http.post<any>(`${this.apiUrl}/prescription/appointment/${appointmentId}`, payload).pipe(
    map(response => {
      const result = response.data || response;
      
      return {
        ...result,
        medicines: typeof result.medicines === 'string' 
          ? JSON.parse(result.medicines) 
          : (result.medicines || []),
        dosage: result.dosage || '',
        frequency: result.frequency || ''
      } as Prescription;
    })
  );
}
}
