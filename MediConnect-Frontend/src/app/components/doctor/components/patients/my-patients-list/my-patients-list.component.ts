import { Component, OnInit, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { DoctorService } from '../../../../../services/doctor.service';
import { AuthService } from '../../../../../services/auth.service';
import { PatientForDoctor } from '../../../../../models/types';

@Component({
    selector: 'app-my-patients-list',
    standalone: true,
    imports: [CommonModule, FormsModule],
    templateUrl: './my-patients-list.component.html',
    styleUrls: ['./my-patients-list.component.css']
})
export class MyPatientsListComponent implements OnInit {
    @Output() patientSelect = new EventEmitter<number>();

    patients: PatientForDoctor[] = [];
    sortedPatients: PatientForDoctor[] = [];

    searchTerm = '';
    searchEmail = '';
    loading = true;
    searching = false;
    showFilters = false;

    sortConfig: { key: keyof PatientForDoctor; direction: 'ascending' | 'descending' } | null = null;

    private searchTimer: any;

    constructor(
        private doctorService: DoctorService,
        private authService: AuthService
    ) { }

    ngOnInit() {
        this.fetchPatients();
    }

    fetchPatients() {
        const user = this.authService.getCurrentUser();
        if (!user) return;

        this.loading = true;
        this.doctorService.getAssociatedPatients().subscribe({
            next: (data) => {
                this.patients = data;
                this.applySort();
                this.loading = false;
            },
            error: (err) => {
                console.error(err);
                this.loading = false;
            }
        });
    }

    onSearchTermChange(term: string) {
        this.searchTerm = term;
        this.debouncedSearch();
    }

    onSearchEmailChange(email: string) {
        this.searchEmail = email;
        this.debouncedSearch();
    }

    debouncedSearch() {
        if (this.searchTimer) clearTimeout(this.searchTimer);
        this.searching = true;
        this.searchTimer = setTimeout(() => {
            this.performSearch();
        }, 500);
    }

    performSearch() {
        if (!this.searchTerm && !this.searchEmail) {
            this.searching = false;
            this.fetchPatients();
            return;
        }

        this.doctorService.searchPatients(this.searchTerm, this.searchEmail).subscribe({
            next: (data) => {
                this.patients = data;
                this.applySort();
                this.searching = false;
            },
            error: (err) => {
                console.error(err);
                this.searching = false;
            }
        });
    }

    toggleFilters() {
        this.showFilters = !this.showFilters;
    }

    clearFilters() {
        this.searchTerm = '';
        this.searchEmail = '';
        this.showFilters = false;
        this.performSearch();
    }

    get hasActiveFilters() {
        return !!this.searchTerm || !!this.searchEmail;
    }

    requestSort(key: keyof PatientForDoctor) {
        let direction: 'ascending' | 'descending' = 'ascending';
        if (this.sortConfig && this.sortConfig.key === key && this.sortConfig.direction === 'ascending') {
            direction = 'descending';
        }
        this.sortConfig = { key, direction };
        this.applySort();
    }

    applySort() {
        if (!this.sortConfig) {
            this.sortedPatients = [...this.patients];
            return;
        }

        const { key, direction } = this.sortConfig;

        this.sortedPatients = [...this.patients].sort((a, b) => {
            const valA = a[key];
            const valB = b[key];

            if (valA === undefined || valB === undefined) return 0;

            if (valA < valB) {
                return direction === 'ascending' ? -1 : 1;
            }
            if (valA > valB) {
                return direction === 'ascending' ? 1 : -1;
            }
            return 0;
        });
    }

    onSelect(id: number) {
        this.patientSelect.emit(id);
    }
}
