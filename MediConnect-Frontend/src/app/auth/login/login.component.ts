import { Component, HostListener } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { AuthService } from '../../services/auth.service';
import { Router, RouterLink } from '@angular/router';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  loginForm: FormGroup;
  error = '';
  successMessage = '';
  isLoading = false;
  showPassword = false;
  isScroll = false;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', Validators.required]
    });
  }

  @HostListener('window:scroll', [])
  onWindowScroll() {
    this.isScroll = window.scrollY > 0;
  }

  togglePasswordVisibility() {
    this.showPassword = !this.showPassword;
  }

  navigate(path: string) {
    this.router.navigate([path]);
  }

  onSubmit() {
    if (this.loginForm.valid) {
      this.isLoading = true;
      this.error = '';
      this.successMessage = '';
      const { email, password } = this.loginForm.value;

      this.authService.login(email, password).subscribe({
        next: (user) => {
          this.isLoading = false;
          let roleMsg = user.role === 'doctor' ? 'Welcome Doctor' : 'Welcome back';
          this.successMessage = `Login successful! ${roleMsg}`;
        },
        error: (err) => {
          this.isLoading = false;
          this.error = 'Invalid credentials. Please try again.';
          console.error('Login error', err);
        }
      });
    } else {
      this.error = "Please enter both email and password.";
    }
  }
}
