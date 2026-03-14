export interface Appointment {
[x: string]: any;
    id?: string | number; 
    appointmentId?: number;
    patientId: number;
    patientName: string;
    doctorId: number;
    doctorName: string;
    date: string;
    timeSlot: string;
    status: 'Waiting' | 'Booked' | 'Completed' | 'Cancelled';
    reason: string;
    consultationMode : 'virtual' | 'in-person';
}

export interface DoctorAvailability {
    availabilityId: number;
    doctorId: number;
    date: string;
    timeSlot: string;
    status: 'AVAILABLE' | 'BOOKED' | 'UNAVAILABLE';
}

export interface GenerateSchedulePayload {
    date: string;
    startTime: string;
    endTime: string;  
}

export interface MarkBreakPayload {
    date: string;
    startTime: string;
    endTime: string;
}

export interface UpdateSlotStatusPayload {
    status: 'AVAILABLE' | 'UNAVAILABLE';
}

export interface PatientForDoctor {
id: number;
    patientId: number;
    name: string;
    age: number;
    gender: string;
    email?: string;
    phoneNumber?: string;
}

export interface PatientProfile {
    id?: number;
    patientId?: number;
    userId?: number;
    name: string;
    age: number;
    gender: string;
    bloodGroup: string;
    phoneNumber: string;
    email: string;
    address: string;
}

export interface UpdatePatientProfilePayload {
    name: string;
    age: number;
    gender: string;
    bloodGroup: string;
    phoneNumber: string;
    address: string;
}

export interface Consultation {
    consultationId: number;
    doctorId: number;
    doctorName?: string;
    patientId: number;
    date: string;
    bloodPressure: string;
    height: number; 
    weight: number; 
    symptoms: string;
    description: string;
    notes: string;
    status: string; 
}

export interface Prescription {
    prescriptionId?: string | number;
    appointmentId: number;
    patientId: number;
    medicines: string;
    notes: string;
    dosage: string;
    frequency: string;
    date?: string;
    expanded?: boolean;
}


export interface PatientHistory {
    patientProfile: PatientProfile;
    appointments: Appointment[];
    consultations: Consultation[];
    prescriptions: Prescription[];
}

export interface Notification {
    id: number;
    userId: number;
    message: string;
    read: boolean;
    createdAt: string;
    type?: 'general' | 'video_call';
    roomName?: string;
}

export interface DoctorProfile {
    id: number;
    name: string;
    email: string;
    specialization: string;
    qualification: string;
    exp: number;
    rating?: number;
    availability?: DoctorAvailability[];
}

export interface BookAppointmentPayload {
    doctorId: number;
    date: string;
    timeSlot: string;
    reason: string;
    specialty?: string;
    availabilityId?: number;
    consultationMode?: 'virtual' | 'in-person';
}
