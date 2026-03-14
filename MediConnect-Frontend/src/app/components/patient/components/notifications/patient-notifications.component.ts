import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NotificationService } from '../../../../services/notification.service';
import { AuthService } from '../../../../services/auth.service';

@Component({
    selector: 'app-patient-notifications',
    standalone: true,
    imports: [CommonModule],
    templateUrl: './patient-notifications.component.html',
    styleUrls: ['./patient-notifications.component.css']
})
export class PatientNotificationsComponent implements OnInit, OnDestroy {
    notifications: any[] = [];
    loading = true;
    private intervalId: any;

    constructor(
        private notificationService: NotificationService,
        private authService: AuthService
    ) { }

    ngOnInit() {
        this.fetchNotifications();
        // Poll every 20 seconds for new unread notifications
        this.intervalId = setInterval(() => this.fetchNotifications(), 20000);
    }

    ngOnDestroy() {
        if (this.intervalId) {
            clearInterval(this.intervalId);
        }
    }

    fetchNotifications() {
   

        this.loading = this.notifications.length === 0; 
        
        this.notificationService.getUnreadNotifications().subscribe({
            next: (data) => {
                // The backend already filters by the authenticated user principal
                this.notifications = data.data;
                console.log('Fetched notifications:', data);
                this.loading = false;
            },
            error: (err) => {
                console.error('Failed to fetch notifications:', err);
                this.loading = false;
            }
        });
    }

    handleMarkAsRead(id: number) {
        this.notificationService.markNotificationAsRead(id).subscribe({
            next: () => {
                this.notifications = this.notifications.filter(n => n.id !== id);
            },
            error: (err) => console.error('Error marking as read:', err)
        });
    }

    handleMarkAllAsRead() {
        if (this.notifications.length === 0) return;

        this.notificationService.markAllNotificationsAsRead().subscribe({
            next: () => {
                this.notifications = [];
            },
            error: (err) => console.error('Error marking all as read:', err)
        });
    }
}