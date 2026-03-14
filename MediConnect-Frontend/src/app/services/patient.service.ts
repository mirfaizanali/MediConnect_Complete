import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, forkJoin, map, switchMap, of } from 'rxjs';
import {
  DoctorProfile,
  PatientProfile,
  BookAppointmentPayload,
  UpdatePatientProfilePayload,
  DoctorAvailability,
  PatientHistory
} from '../models/types';
import { AuthService } from './auth.service';
import { environment } from '../../environments/environment';


@Injectable({
  providedIn: 'root',
})
export class PatientService {
  private apiUrl = environment.apiUrl;

  constructor(
    private http: HttpClient,
    private authService: AuthService,
  ) { }

  getAllDoctors(): Observable<DoctorProfile[]> {
    return this.http
      .get<any>(`${this.apiUrl}/doctors/all`)
      .pipe(map((response) => response.data || []));
  }

  bookAppointment(payload: { reason: string; availabilityId: number }): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/appointments/book`, payload).pipe(
      map((response) => {
        return response.data;
      }),
    );
  }

  getMyPatientProfile(): Observable<PatientProfile> {
    return this.http.get<PatientProfile>(`${this.apiUrl}/patients/user`).pipe(
      map((profile) => {
        if (!profile) throw new Error('Doctor profile not found');
        return profile;
      }),
    );
  }

  updateMyPatientProfile(data: UpdatePatientProfilePayload): Observable<PatientProfile> {
    return this.http.put<PatientProfile>(`${this.apiUrl}/patients/user`, data);
  }

  getPatientHistory(): Observable<PatientHistory> {
    return this.http.get<any>(`${this.apiUrl}/patients/history`).pipe(
      map((response) => {
        const history = response.data;
        return {
          patientProfile: history.patientProfile || {},
          appointments: history.appointments || [],
          consultations: history.consultations || [],
          prescriptions: history.prescriptions || []
        } as PatientHistory;
      })
    );
  }
}
