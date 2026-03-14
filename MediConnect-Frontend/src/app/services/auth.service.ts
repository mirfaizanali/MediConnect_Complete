import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, map, tap, switchMap } from 'rxjs';
import { User } from '../models/user.model';
import { Router } from '@angular/router';
import { environment } from '../../environments/environment';



@Injectable({
    providedIn: 'root'
})
export class AuthService {
private apiUrl = environment.apiUrl;
    private userSubject = new BehaviorSubject<User | null>(null);
    public user$ = this.userSubject.asObservable();

    constructor(private http: HttpClient, private router: Router) {
        const savedUser = localStorage.getItem('user');
        if (savedUser) {
            this.userSubject.next(JSON.parse(savedUser));
        }
    }

login(email: string, password: string): Observable<User> {
    return this.http.post<any>(`${this.apiUrl}/auth/login`, { email, password })
        .pipe(
            map(response => {
                const userData: User = response.user;
                userData.token = response.token; 

                if (userData.role.includes('PATIENT')){
                    userData.role = 'patient';
                }  else{
                    userData.role = 'doctor';
                } 

                return userData;
            }),
            tap(user => {
                localStorage.setItem('user', JSON.stringify(user));
                this.userSubject.next(user);
                this.redirectBasedOnRole(user.role);
            })
        );
}

registerPatient(data: any): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/auth/register-patient`, data);
}



registerDoctor(data: any): Observable<any> {
    return this.http.post<any[]>(`${this.apiUrl}/auth/register-doctor`,data);
}

    logout() {
        localStorage.removeItem('user');
        this.userSubject.next(null);
        this.router.navigate(['/login']);
    }

    isAuthenticated(): boolean {
        return !!this.userSubject.value;
    }

    getUserRole(): string | undefined {
        return this.userSubject.value?.role;
    }

    getCurrentUser(): User | null {
        return this.userSubject.value;
    }

    private redirectBasedOnRole(role: string) {
        if (role === 'doctor') {
            this.router.navigate(['/dashboard/doctor']);
        } else if (role === 'patient') {
            this.router.navigate(['/dashboard/patient']);
        } else {
            this.router.navigate(['/']);
        }
    }
}
