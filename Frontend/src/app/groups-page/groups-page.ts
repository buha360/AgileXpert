import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';

import { GroupsService } from '../generated-api/api/groups.service';
import { GroupSummaryResponse } from '../generated-api/model/groupSummaryResponse';
import { CreateGroupRequest } from '../generated-api/model/createGroupRequest';
import { ValidateGroupAccessRequest } from '../generated-api/model/validateGroupAccessRequest';

@Component({
  selector: 'app-groups-page',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './groups-page.html',
  styleUrl: './groups-page.css'
})
export class GroupsPageComponent implements OnInit {
  private groupsService = inject(GroupsService);
  private router = inject(Router);

  groups: GroupSummaryResponse[] = [];
  loading = false;
  errorMessage = '';

  createRequest: CreateGroupRequest = {
    groupName: '',
    accessCode: '',
    adminUserName: '',
    adminPassword: ''
  };

  selectedGroup: GroupSummaryResponse | null = null;
  accessCode = '';
  accessErrorMessage = '';

  ngOnInit(): void {
    this.loadGroups();
  }

  loadGroups(): void {
    this.loading = true;
    this.errorMessage = '';

    this.groupsService.getAllGroups().subscribe({
      next: (groups) => {
        this.groups = groups;
        this.loading = false;
      },
      error: (error) => {
        console.error(error);
        this.errorMessage = 'Failed to load groups.';
        this.loading = false;
      }
    });
  }

  createGroup(): void {
    this.errorMessage = '';

    if (!this.isCreateGroupFormValid()) {
      this.errorMessage = 'Please fill in all required fields.';
      return;
    }

    this.groupsService.createGroup(this.createRequest).subscribe({
      next: () => {
        this.createRequest = {
          groupName: '',
          accessCode: '',
          adminUserName: '',
          adminPassword: ''
        };
        this.loadGroups();
      },
      error: (error) => {
        console.error(error);
        this.errorMessage = 'Failed to create group.';
      }
    });
  }

  openEnterGroup(group: GroupSummaryResponse): void {
    this.selectedGroup = group;
    this.accessCode = '';
    this.accessErrorMessage = '';
  }

  continueToGroupLogin(): void {
    if (!this.selectedGroup?.id) {
      this.accessErrorMessage = 'No group selected.';
      return;
    }

    const request: ValidateGroupAccessRequest = {
      accessCode: this.accessCode
    };

    this.groupsService.validateAccessCode(this.selectedGroup.id, request).subscribe({
      next: (response) => {
        if (response.valid) {
          this.accessErrorMessage = '';
          this.router.navigate(['/groups', this.selectedGroup!.id, 'login']);
        } else {
          this.accessErrorMessage = response.message ?? 'Invalid access code.';
        }
      },
      error: (error) => {
        console.error(error);
        this.accessErrorMessage = 'Failed to validate access code.';
      }
    });
  }

  cancelEnterGroup(): void {
    this.selectedGroup = null;
    this.accessCode = '';
    this.accessErrorMessage = '';
  }

  trackById(_: number, item: { id?: string | null }): string {
    return item.id ?? Math.random().toString();
  }

  isCreateGroupFormValid(): boolean {
    return !!this.createRequest.groupName?.trim()
      && !!this.createRequest.accessCode?.trim()
      && !!this.createRequest.adminUserName?.trim()
      && !!this.createRequest.adminPassword?.trim();
  }
}