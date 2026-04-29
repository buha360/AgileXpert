import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { UsersService } from '../generated-api/api/users.service';
import { UserSummaryResponse } from '../generated-api/model/userSummaryResponse';
import { CreateUserRequest } from '../generated-api/model/createUserRequest';

@Component({
  selector: 'app-group-admin-page',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './group-admin-page.html',
  styleUrl: './group-admin-page.css'
})
export class GroupAdminPageComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private usersService = inject(UsersService);

  groupId = '';
  currentAdminUserId = '';

  users: UserSummaryResponse[] = [];
  loading = false;
  errorMessage = '';

  createRequest: CreateUserRequest = {
    name: '',
    password: ''
  };

  isDeleteUserModalOpen = false;
  userPendingDeletion: UserSummaryResponse | null = null;

  ngOnInit(): void {
    this.groupId = this.route.snapshot.paramMap.get('groupId') ?? '';
    this.currentAdminUserId = history.state?.userId ?? '';
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

  isCreateUserFormValid(): boolean {
    return !!this.createRequest.name?.trim()
      && !!this.createRequest.password?.trim();
  }

  createUser(): void {
    if (!this.groupId) {
      this.errorMessage = 'Missing group id.';
      return;
    }

    if (!this.isCreateUserFormValid()) {
      this.errorMessage = 'Please fill in all required fields.';
      return;
    }

    this.errorMessage = '';

    this.usersService.createUser(this.groupId, this.createRequest).subscribe({
      next: () => {
        this.createRequest = {
          name: '',
          password: ''
        };
        this.loadUsers();
      },
      error: (error) => {
        console.error(error);
        this.errorMessage = 'Failed to create user.';
      }
    });
  }

  requestDeleteUser(user: UserSummaryResponse): void {
    if (user.role === 'ADMIN') {
      this.errorMessage = 'Admin user cannot be deleted.';
      return;
    }

    this.userPendingDeletion = user;
    this.isDeleteUserModalOpen = true;
  }

  confirmDeleteUser(): void {
    if (!this.groupId || !this.userPendingDeletion?.id) {
      this.errorMessage = 'Missing data for deletion.';
      return;
    }

    this.usersService.deleteUser(this.groupId, this.userPendingDeletion.id).subscribe({
      next: () => {
        this.closeDeleteUserModal();
        this.loadUsers();
      },
      error: (error) => {
        console.error(error);
        this.errorMessage = 'Failed to delete user.';
      }
    });
  }

  closeDeleteUserModal(): void {
    this.isDeleteUserModalOpen = false;
    this.userPendingDeletion = null;
  }

  openMyMenu(): void {
    if (!this.currentAdminUserId) {
      this.errorMessage = 'Missing admin user id.';
      return;
    }

    this.router.navigate(['/users', this.currentAdminUserId, 'menu'], {
      state: { groupId: this.groupId }
    });
  }

  goBack(): void {
    this.router.navigate(['/groups', this.groupId, 'login']);
  }

  trackById(_: number, item: { id?: string | null }): string {
    return item.id ?? Math.random().toString();
  }
}