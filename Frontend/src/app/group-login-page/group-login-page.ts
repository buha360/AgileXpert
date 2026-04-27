import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { UsersService } from '../generated-api/api/users.service';
import { UserSummaryResponse } from '../generated-api/model/userSummaryResponse';
import { UserLoginRequest } from '../generated-api/model/userLoginRequest';

@Component({
  selector: 'app-group-login-page',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './group-login-page.html',
  styleUrl: './group-login-page.css'
})
export class GroupLoginPageComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private usersService = inject(UsersService);

  groupId = '';
  users: UserSummaryResponse[] = [];
  loading = false;
  errorMessage = '';

  selectedUserId = '';
  password = '';
  loginErrorMessage = '';

  ngOnInit(): void {
    this.groupId = this.route.snapshot.paramMap.get('groupId') ?? '';
    this.loadUsers();
  }

  loadUsers(): void {
    if (!this.groupId) {
      this.errorMessage = 'Missing group id.';
      return;
    }

    this.loading = true;
    this.errorMessage = '';

    this.usersService.getUsersByGroup(this.groupId).subscribe({
      next: (users) => {
        this.users = users;
        this.loading = false;
      },
      error: (error) => {
        console.error(error);
        this.errorMessage = 'Failed to load users.';
        this.loading = false;
      }
    });
  }

  selectUser(userId: string): void {
    this.selectedUserId = userId;
    this.password = '';
    this.loginErrorMessage = '';
  }

  login(): void {
    if (!this.groupId || !this.selectedUserId) {
      this.loginErrorMessage = 'Please select a user.';
      return;
    }

    const request: UserLoginRequest = {
      password: this.password
    };

    this.usersService.loginUser(this.groupId, this.selectedUserId, request).subscribe({
      next: (authenticatedUser) => {
        this.loginErrorMessage = '';

        if (authenticatedUser.role === 'ADMIN') {
          this.router.navigate(['/groups', this.groupId, 'admin'], {
            state: { userId: authenticatedUser.id }
          });
        } else {
          this.router.navigate(['/users', authenticatedUser.id, 'menu'], {
            state: { groupId: this.groupId }
          });
        }
      },
      error: (error) => {
        console.error(error);
        this.loginErrorMessage = 'Invalid password or login failed.';
      }
    });
  }

  goBack(): void {
    this.router.navigate(['/groups']);
  }
}