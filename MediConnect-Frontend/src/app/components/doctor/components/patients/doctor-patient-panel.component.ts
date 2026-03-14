import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MyPatientsListComponent } from './my-patients-list/my-patients-list.component';
import { PatientHistoryComponent } from './patient-history/patient-history.component';

@Component({
    selector: 'app-doctor-patient-panel',
    standalone: true,
    imports: [CommonModule, MyPatientsListComponent, PatientHistoryComponent],
    template: `
    <app-my-patients-list 
      *ngIf="!selectedPatientId" 
      (patientSelect)="handlePatientSelect($event)"
    ></app-my-patients-list>
    
    <app-patient-history 
      *ngIf="selectedPatientId" 
      [patientId]="selectedPatientId"
      (back)="handleBackToList()"
    ></app-patient-history>
  `
})
export class DoctorPatientPanelComponent {
    selectedPatientId: number | null = null;

    handlePatientSelect(patientId: number) {
        this.selectedPatientId = patientId;
    }

    handleBackToList() {
        this.selectedPatientId = null;
    }
}
