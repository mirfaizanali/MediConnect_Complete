import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({
    providedIn: 'root'
})
export class NotificationService {
    private apiUrl = `${environment.apiUrl}/notifications`;

    constructor(private http: HttpClient) { }

    /**
     * Gets unread notifications for the authenticated user.
     * Maps to GET /api/notifications
     */
    getUnreadNotifications(): Observable<any> {
        return this.http.get<any>(this.apiUrl);
    }

    /**
     * Marks a specific notification as read.
     * Maps to PATCH /api/notifications/{id}/read
     */
    markNotificationAsRead(id: number): Observable<any> {
        // Sending an empty body as the backend expects @PathVariable but no @RequestBody
        return this.http.patch<any>(`${this.apiUrl}/${id}/read`, {});
    }

    /**
     * Marks all notifications for the current user as read.
     * Maps to PATCH /api/notifications/read-all
     */
    markAllNotificationsAsRead(): Observable<any> {
        return this.http.patch<any>(`${this.apiUrl}/read-all`, {});
    }

    /**
     * Gets the count of unread notifications.
     * Maps to GET /api/notifications/unread-count
     */
    getUnreadCount(): Observable<any> {
        return this.http.get<any>(`${this.apiUrl}/unread-count`);
    }
}