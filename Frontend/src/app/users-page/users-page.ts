import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { UsersService } from '../generated-api/api/users.service';
import { UserSummaryResponse } from '../generated-api/model/userSummaryResponse';
import { CreateUserRequest } from '../generated-api/model/createUserRequest';

@Component({
  selector: 'app-users-page',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './users-page.html',
  styleUrl: './users-page.css'
})
export class UsersPageComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private usersService = inject(UsersService);

  groupId = '';
  users: UserSummaryResponse[] = [];
  loading = false;
  errorMessage = '';

  createRequest: CreateUserRequest = {
    name: '',
    password: ''
  };

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

  createUser(): void {
    if (!this.groupId) {
      this.errorMessage = 'Missing group id.';
      return;
    }

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

  goBack(): void {
    this.router.navigate(['/groups']);
  }
}