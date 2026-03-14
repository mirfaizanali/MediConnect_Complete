import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { AuthService } from '../../services/auth.service';
import { Router, RouterLink } from '@angular/router';

@Component({
  selector: 'app-register-patient',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './register-patient.component.html',
  styleUrls: ['./register-patient.component.css']
})
export class RegisterPatientComponent {
  registerForm: FormGroup;
  showPassword = false;
  showConfirmPassword = false;
  isSubmitting = false;
  errorMessage = '';
  successMessage = '';
  bloodGroups = ['A+', 'A-', 'B+', 'B-', 'AB+', 'AB-', 'O+', 'O-'];

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {
    this.registerForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      confirmPassword: ['', Validators.required],
      name: ['', Validators.required],
      age: ['', [Validators.required, Validators.min(1), Validators.max(120)]],
      bloodGroup: ['', Validators.required],
      phoneNumber: ['', [Validators.required, Validators.pattern('^[0-9]{10}$')]],
      address: ['', Validators.required],
      gender: ['Male', Validators.required]
    });
  }

  togglePasswordVisibility() {
    this.showPassword = !this.showPassword;
  }

  toggleConfirmPasswordVisibility() {
    this.showConfirmPassword = !this.showConfirmPassword;
  }

  navigate(path: string) {
    this.router.navigate([path]);
  }

  onSubmit() {
    if (this.registerForm.valid) {
      if (this.registerForm.value.password !== this.registerForm.value.confirmPassword) {
        this.errorMessage = "Passwords do not match!";
        return;
      }

      this.isSubmitting = true;
      this.errorMessage = '';

      this.authService.registerPatient(this.registerForm.value).subscribe({
        next: (response) => {
          this.isSubmitting = false;
          if (response.status) {
            this.successMessage = response.message || "Registration successful!";
            setTimeout(() => {
              this.router.navigate(['/dashboard/patient']);
            }, 1500);
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
