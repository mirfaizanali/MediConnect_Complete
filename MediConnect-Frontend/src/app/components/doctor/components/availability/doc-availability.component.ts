import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { DoctorService } from '../../../../services/doctor.service';
import { AuthService } from '../../../../services/auth.service';
import { DoctorAvailability } from '../../../../models/types';

@Component({
    selector: 'app-doc-availability',
    standalone: true,
    imports: [CommonModule, FormsModule],
    templateUrl: './doc-availability.component.html',
    styleUrls: ['./doc-availability.component.css']
})
export class DocAvailabilityComponent implements OnInit {
    selectedDate: string = new Date().toISOString().split('T')[0];
    availability: DoctorAvailability[] = [];
    allAvailability: DoctorAvailability[] = [];

    startTime = "09:00";
    endTime = "12:00";
    breakStartTime = "13:00";
    breakEndTime = "14:00";

    loading = false;
    viewMode: 'date' | 'all' = 'date';
    slotViewMode: 'grid' | 'compact' = 'compact';
    expandedDates: Set<string> = new Set();

    groupedAvailability: Record<string, DoctorAvailability[]> = {};
    sortedDates: string[] = [];
    dailyPeriods: Record<string, DoctorAvailability[]> = { morning: [], afternoon: [], evening: [] };

    minDate = new Date().toISOString().split('T')[0];

    constructor(
        private doctorService: DoctorService,
        private authService: AuthService
    ) { }

    ngOnInit() {
        this.fetchAvailability(this.selectedDate);
    }

    objectKeys(obj: any): string[] {
        return Object.keys(obj);
    }

    setViewMode(mode: 'date' | 'all') {
        this.viewMode = mode;
        if (mode === 'date') {
            this.fetchAvailability(this.selectedDate);
        } else {
            this.fetchAllAvailability();
        }
    }

    setSlotViewMode(mode: 'grid' | 'compact') {
        this.slotViewMode = mode;
    }

    onDateChange(date: string) {
        this.selectedDate = date;
        this.fetchAvailability(date);
    }

    fetchAvailability(date: string) {
        const user = this.authService.getCurrentUser();
        if (!user) return;

        this.loading = true;
        this.doctorService.getDoctorAvailabilityForDate(date).subscribe({
            next: (data) => {
                this.availability = data;
                this.updateDailyPeriods();
                this.loading = false;
            },
            error: (err) => {
                console.error("Failed to fetch availability", err);
                this.availability = [];
                this.loading = false;
            }
        });
    }

    fetchAllAvailability() {
        const user = this.authService.getCurrentUser();
        if (!user) return;

        this.loading = true;
        this.doctorService.getAllDoctorAvailability().subscribe({
            next: (data) => {
                this.allAvailability = data;
                this.groupAvailability();
                this.loading = false;
            },
            error: (err) => {
                console.error("Failed to fetch all availability", err);
                this.allAvailability = [];
                this.loading = false;
            }
        });
    }

    groupAvailability() {
        this.groupedAvailability = this.allAvailability.reduce((acc, slot) => {
            const date = slot.date;
            if (!acc[date]) {
                acc[date] = [];
            }
            acc[date].push(slot);
            return acc;
        }, {} as Record<string, DoctorAvailability[]>);

        this.sortedDates = Object.keys(this.groupedAvailability).sort((a, b) =>
            new Date(b).getTime() - new Date(a).getTime()
        );
    }

    handleGenerateSchedule() {
        const payload = {
            date: this.selectedDate,
            startTime: this.startTime,
            endTime: this.endTime
        };

        this.doctorService.generateDoctorSchedule(payload).subscribe({
            next: () => {
                this.fetchAvailability(this.selectedDate);
            },
            error: (err) => console.error(err)
        });
    }

    handleMarkBreak() {
        const payload = {
            date: this.selectedDate,
            startTime: this.breakStartTime,
            endTime: this.breakEndTime
        };

        this.doctorService.markDoctorBreak(payload).subscribe({
            next: () => this.fetchAvailability(this.selectedDate),
            error: (err) => console.error(err)
        });
    }

  handleToggleSlotStatus(slot: DoctorAvailability) {
  const newStatus = slot.status === 'AVAILABLE' ? 'UNAVAILABLE' : 'AVAILABLE';

  this.doctorService.updateSlotStatus(
    slot.availabilityId,
  ).subscribe({
    next: () => {

      // ✅ Re-fetch fresh data after update
      if (this.viewMode === 'date') {
        this.fetchAvailability(slot.date);   // reload only that date
      } else {
        this.fetchAllAvailability();         // reload all slots
      }
    },
    error: (err) => console.error("Error updating slot", err)
  });
}


    handleClearDaySchedule() {
        if (confirm(`Are you sure you want to clear all slots on ${this.selectedDate}?`)) {
            this.doctorService.clearDoctorAvailabilityForDate(this.selectedDate).subscribe({
                next: () => {
                    this.availability = [];
                    this.updateDailyPeriods();
                },
                error: (err) => console.error(err)
            });
        }
    }

    updateDailyPeriods() {
        this.dailyPeriods = {
            morning: this.availability.filter(slot => {
                const hour = parseInt(slot.timeSlot.split(':')[0]);
                return hour >= 6 && hour < 12;
            }),
            afternoon: this.availability.filter(slot => {
                const hour = parseInt(slot.timeSlot.split(':')[0]);
                return hour >= 12 && hour < 17;
            }),
            evening: this.availability.filter(slot => {
                const hour = parseInt(slot.timeSlot.split(':')[0]);
                return hour >= 17 && hour < 22;
            })
        };
    }

    getStatusColor(status: string) {
        switch (status) {
            case 'AVAILABLE': return 'var(--hc-green)';
            case 'BOOKED': return 'var(--hc-orange)';
            case 'UNAVAILABLE': return 'var(--hc-gray-text)';
            default: return 'var(--hc-gray-text)';
        }
    }

    toggleDateExpansion(date: string) {
        if (this.expandedDates.has(date)) {
            this.expandedDates.delete(date);
        } else {
            this.expandedDates.add(date);
        }
    }

    canToggleSlot(slot: DoctorAvailability) {
        return slot.status === 'AVAILABLE' || slot.status === 'UNAVAILABLE';
    }
}
