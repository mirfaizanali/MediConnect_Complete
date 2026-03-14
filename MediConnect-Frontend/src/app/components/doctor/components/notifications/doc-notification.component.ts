import { Component, OnInit, OnDestroy, HostListener, ElementRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NotificationService } from '../../../../services/notification.service';
import { AuthService } from '../../../../services/auth.service';

@Component({
    selector: 'app-doc-notification',
    standalone: true,
    imports: [CommonModule],
    templateUrl: './doc-notification.component.html',
    styleUrls: ['./doc-notification.component.css']
})
export class DocNotificationComponent implements OnInit, OnDestroy {
    notifications: any[] = [];
    isLoading = true;
    isOpen = false;
    private intervalId: any;

    constructor(
        private notificationService: NotificationService,
        private authService: AuthService,
        private elementRef: ElementRef
    ) { }

    ngOnInit() {
        this.fetchNotifications();
        // Poll every 20 seconds
        this.intervalId = setInterval(() => this.fetchNotifications(), 20000);
    }

    ngOnDestroy() {
        if (this.intervalId) {
            clearInterval(this.intervalId);
        }
    }

    // Handles clicking outside the notification dropdown to close it
    @HostListener('document:click', ['$event'])
    clickOutside(event: MouseEvent) {
        if (!this.elementRef.nativeElement.contains(event.target)) {
            this.isOpen = false;
        }
    }

    toggleDropdown(event: Event) {
        event.stopPropagation();
        this.isOpen = !this.isOpen;
    }

    fetchNotifications() {


        if (this.notifications.length === 0) this.isLoading = true;

        this.notificationService.getUnreadNotifications().subscribe({
            next: (data) => {

                const items = (data && (data.data ?? data)) || [];
                this.notifications = Array.isArray(items) ? items : [];
                console.log('Fetched doctor notifications:', data);
                this.isLoading = false;
            },
            error: (err) => {
                console.error("Error fetching notifications", err);
                this.isLoading = false;
            }
        });
    }

    handleMarkAsRead(id: number) {
        this.notificationService.markNotificationAsRead(id).subscribe({
            next: () => {
                this.notifications = this.notifications.filter(n => n.id !== id);
            },
            error: (err) => console.error("Failed to mark as read", err)
        });
    }

    handleMarkAllAsRead() {
        if (this.notifications.length === 0) return;

        this.notificationService.markAllNotificationsAsRead().subscribe({
            next: () => {
                this.notifications = [];
                this.isOpen = false;
            },
            error: (err) => console.error("Failed to mark all as read", err)
        });
    }

    closeDropdown() {
    this.isOpen = false;
}
}