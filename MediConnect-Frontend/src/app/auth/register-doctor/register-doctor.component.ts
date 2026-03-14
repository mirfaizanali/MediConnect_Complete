import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { AuthService } from '../../services/auth.service';
import { Router, RouterLink } from '@angular/router';

@Component({
  selector: 'app-register-doctor',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './register-doctor.component.html',
  styleUrls: ['./register-doctor.component.css']
})
export class RegisterDoctorComponent implements OnInit {
  registerForm: FormGroup;
  termsAccepted = false;
  isSubmitting = false;
  errorMessage = '';
  successMessage = '';

  specializations = [
    'Cardiology', 'Dermatology', 'Endocrinology', 'Gastroenterology',
    'Neurology', 'Oncology', 'Orthopedics', 'Pediatrics',
    'Psychiatry', 'Radiology', 'Surgery', 'Urology'
  ];

  qualifications = [
    'MBBS', 'MD', 'MS', 'DM', 'MCh', 'DNB',
    'BDS', 'MDS', 'BAMS', 'BHMS', 'BUMS', 'BVSc'
  ];

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {
    this.registerForm = this.fb.group({
      name: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      specialization: ['', Validators.required],
      qualification: ['', Validators.required],
      exp: [0, [Validators.required, Validators.min(0)]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      confirmPassword: ['', Validators.required]
    });
  }

  ngOnInit() {
    this.registerForm.get('name')?.valueChanges.subscribe(value => {
      if (value && !value.startsWith('Dr. ') && value.length > 0) {
        this.registerForm.get('name')?.setValue('Dr. ' + value, { emitEvent: false });
      }
    });
  }

  toggleTerms(event: any) {
    this.termsAccepted = event.target.checked;
  }

  navigate(path: string) {
    this.router.navigate([path]);
  }

  onSubmit() {
    this.errorMessage = '';
    this.successMessage = '';

    if (this.registerForm.valid) {
      if (this.registerForm.value.password !== this.registerForm.value.confirmPassword) {
        this.errorMessage = "Passwords do not match!";
        return;
      }
      // if (!this.termsAccepted) {
      //   this.errorMessage = "You must accept the Terms and Conditions.";
      //   return;
      // }

      this.isSubmitting = true;

      this.authService.registerDoctor(this.registerForm.value).subscribe({
        next: (response) => {
          this.isSubmitting = false;
          if (response.status) {
            this.successMessage = response.message || "Registration successful!";
              this.router.navigate(['/dashboard/doctor']);
          
          } else {
            this.errorMessage = response.message || "Registration failed.";
          }
        },
        error: (err) => {
          this.isSubmitting = false;
          this.errorMessage = err.message || "Registration failed. Please try again.";
          console.error("Registration failed", err);
        }
      });
    } else {
      this.errorMessage = "Please fill in all required fields correctly.";
    }
  }
}
