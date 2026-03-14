import { inject } from '@angular/core';
import {  Router, CanActivateFn } from '@angular/router';
import { AuthService } from '../services/auth.service';

export const authGuard: CanActivateFn = (route, state) => {
    const authService = inject(AuthService);
    const router = inject(Router);

    if (authService.isAuthenticated()) {
        const userRole = authService.getUserRole();
        const expectedRole = route.data['role'];

        if (expectedRole && userRole !== expectedRole) {
            if (userRole === 'doctor') return router.createUrlTree(['/dashboard/doctor']);
            if (userRole === 'patient') return router.createUrlTree(['/dashboard/patient']);
            return router.createUrlTree(['/']);
        }
        return true;
    }

    return router.createUrlTree(['/login']);
};
