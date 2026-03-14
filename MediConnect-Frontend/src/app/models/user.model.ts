export interface User {
    id: number;
    email: string;
    name: string;
    role: 'doctor' | 'patient' | 'admin';
    token?: string;
    specialization?: string; 
    profileImage?: string;
}

export interface LoginResponse {
    user: User;
    accessToken: string;
}
