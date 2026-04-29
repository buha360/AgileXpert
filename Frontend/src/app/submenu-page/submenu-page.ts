import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { forkJoin} from 'rxjs';

import { MenusService } from '../generated-api/api/menus.service';
import { RootMenuResponse } from '../generated-api/model/rootMenuResponse';
import { AddApplicationRequest } from '../generated-api/model/addApplicationRequest';

@Component({
  selector: 'app-submenu-page',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './submenu-page.html',
  styleUrl: './submenu-page.css'
})
export class SubmenuPageComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private menusService = inject(MenusService);

  userId = '';
  submenuId = '';
  groupId = '';

  submenu: RootMenuResponse | null = null;
  rootMenu: RootMenuResponse | null = null;

  loading = false;
  errorMessage = '';

  isAddAppModalOpen = false;
  draggedApp: FavoriteAppVm | null = null;
  isTrashVisible = false;
  isTrashHover = false;

  isLaunchModalOpen = false;
  launchMessage = '';

  readonly allApplications: DesktopAppOption[] = [
    { id: 'app-openmap', name: 'OpenMap', icon: '🗺️' },
    { id: 'app-paint', name: 'Paint', icon: '🎨' },
    { id: 'app-contacts', name: 'Contacts', icon: '👥' },
    { id: 'app-minesweeper', name: 'Minesweeper', icon: '💣' }
  ];

  ngOnInit(): void {
    this.userId = this.route.snapshot.paramMap.get('userId') ?? '';
    this.submenuId = this.route.snapshot.paramMap.get('submenuId') ?? '';
    this.groupId = history.state?.groupId ?? '';
    this.loadData();
  }

  loadData(): void {
    if (!this.userId || !this.submenuId) {
      this.errorMessage = 'Missing submenu data.';
      return;
    }

    this.loading = true;
    this.errorMessage = '';

    forkJoin({
      rootMenu: this.menusService.getRootMenu(this.userId),
      submenu: this.menusService.getSubMenu(this.submenuId)
    }).subscribe({
      next: ({ rootMenu, submenu }) => {
        this.rootMenu = rootMenu;
        this.submenu = submenu;
        this.loading = false;
      },
      error: (error) => {
        console.error(error);
        this.errorMessage = 'Failed to load submenu.';
        this.loading = false;
      }
    });
  }

  launchApplication(app: FavoriteAppVm): void {
    if (!this.submenuId || !app.menuItemId) {
      this.errorMessage = 'Missing application launch data.';
      return;
    }

    this.menusService.launchSubMenuApplication(this.submenuId, app.menuItemId).subscribe({
      next: (response) => {
        this.launchMessage = response?.message ?? `${app.name} has been launched.`;
        this.isLaunchModalOpen = true;
      },
      error: (error) => {
        console.error(error);
        this.errorMessage = 'Failed to launch application.';
      }
    });
  }

  closeLaunchModal(): void {
    this.isLaunchModalOpen = false;
    this.launchMessage = '';
  }

  getSubmenuApplications(): FavoriteAppVm[] {
    const items = this.submenu?.applications ?? [];

    return items.map(item => ({
      menuItemId: item.id ?? '',
      applicationId: this.getApplicationIdByName(item.name ?? ''),
      name: item.name ?? 'Unknown App',
      icon: this.getAppIcon(item.name ?? '')
    }));
  }

  getAllAssignedApplicationNames(): Set<string> {
    const assigned = new Set<string>();

    for (const app of this.rootMenu?.applications ?? []) {
      if (app.name) {
        assigned.add(app.name);
      }
    }

    const currentSubmenuApps = this.getSubmenuApplications();
    for (const app of currentSubmenuApps) {
      assigned.add(app.name);
    }

    return assigned;
  }

  getAvailableApplicationsToAdd(): DesktopAppOption[] {
    const assignedNames = this.getAllAssignedApplicationNames();
    return this.allApplications.filter(app => !assignedNames.has(app.name));
  }

  openAddAppModal(): void {
    this.isAddAppModalOpen = true;
  }

  closeAddAppModal(): void {
    this.isAddAppModalOpen = false;
  }

  addApplicationToSubmenu(app: DesktopAppOption): void {
    const request: AddApplicationRequest = {
      applicationId: app.id
    };

    this.menusService.addApplicationToSubMenu(this.submenuId, request).subscribe({
      next: () => {
        this.closeAddAppModal();
        this.loadData();
      },
      error: (error) => {
        console.error(error);
        this.errorMessage = 'Failed to add application to folder.';
      }
    });
  }

  removeApplicationFromSubmenu(app: FavoriteAppVm): void {
    this.menusService.removeApplicationFromSubMenu(this.submenuId, app.menuItemId).subscribe({
      next: () => {
        this.draggedApp = null;
        this.isTrashVisible = false;
        this.isTrashHover = false;
        this.loadData();
      },
      error: (error) => {
        console.error(error);
        this.errorMessage = 'Failed to remove application from folder.';
      }
    });
  }

  onAppDragStart(app: FavoriteAppVm): void {
    this.draggedApp = app;
    this.isTrashVisible = true;
  }

  onAppDragEnd(): void {
    this.draggedApp = null;
    this.isTrashVisible = false;
    this.isTrashHover = false;
  }

  onTrashDragOver(event: DragEvent): void {
    event.preventDefault();
    this.isTrashHover = true;
  }

  onTrashDragLeave(): void {
    this.isTrashHover = false;
  }

  onTrashDrop(event: DragEvent): void {
    event.preventDefault();
    this.isTrashHover = false;

    if (!this.draggedApp) {
      return;
    }

    this.removeApplicationFromSubmenu(this.draggedApp);
  }

  goBack(): void {
    this.router.navigate(['/users', this.userId, 'menu'], {
      state: { groupId: this.groupId }
    });
  }

  getAppIcon(name?: string | null): string {
    switch (name) {
      case 'Paint':
        return '🎨';
      case 'OpenMap':
        return '🗺️';
      case 'Contacts':
        return '👥';
      case 'Minesweeper':
        return '💣';
      default:
        return '📱';
    }
  }

  getApplicationIdByName(name?: string | null): string {
    switch (name) {
      case 'OpenMap':
        return 'app-openmap';
      case 'Paint':
        return 'app-paint';
      case 'Contacts':
        return 'app-contacts';
      case 'Minesweeper':
        return 'app-minesweeper';
      default:
        return '';
    }
  }

  trackByFavoriteApp(_: number, app: FavoriteAppVm): string {
    return app.menuItemId;
  }

  trackByAvailableApp(_: number, app: DesktopAppOption): string {
    return app.id;
  }

  getWallpaperUrl(name?: string | null): string {
    switch (name) {
      case 'Mountain':
        return 'wallpapers/mountain.jpg';
      case 'Space':
        return 'wallpapers/space.jpg';
      case 'Family':
        return 'wallpapers/family.jpg';
      case 'Minimal':
        return 'wallpapers/minimal.jpg';
      default:
        return 'wallpapers/minimal.jpg';
    }
  }

  getThemeClass(name?: string | null): string {
    switch (name) {
      case 'Light':
        return 'theme-light';
      case 'Dark':
        return 'theme-dark';
      case 'Blue':
        return 'theme-blue';
      case 'Classic':
        return 'theme-classic';
      default:
        return 'theme-dark';
    }
  }

  getDisplaySubmenuName(name?: string | null): string {
    return name?.trim() || 'Folder';
  }
}

interface DesktopAppOption {
  id: string;
  name: string;
  icon: string;
}

interface FavoriteAppVm {
  menuItemId: string;
  applicationId: string;
  name: string;
  icon: string;
}