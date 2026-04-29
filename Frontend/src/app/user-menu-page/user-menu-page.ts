import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';

import { MenusService } from '../generated-api/api/menus.service';
import { CustomizationService } from '../generated-api/api/customization.service';

import { RootMenuResponse } from '../generated-api/model/rootMenuResponse';
import { WallpaperResponse } from '../generated-api/model/wallpaperResponse';
import { ThemeResponse } from '../generated-api/model/themeResponse';
import { SelectWallpaperRequest } from '../generated-api/model/selectWallpaperRequest';
import { SelectThemeRequest } from '../generated-api/model/selectThemeRequest';
import { AddApplicationRequest } from '../generated-api/model/addApplicationRequest';
import { CreateSubMenuRequest } from '../generated-api/model/createSubMenuRequest';

import { forkJoin, of } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';
import { SubMenuResponse } from '../generated-api/model/subMenuResponse';

@Component({
  selector: 'app-user-menu-page',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './user-menu-page.html',
  styleUrl: './user-menu-page.css'
})
export class UserMenuPageComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private menusService = inject(MenusService);
  private customizationService = inject(CustomizationService);

  userId = '';
  groupId = '';
  menu: RootMenuResponse | null = null;
  loading = false;
  errorMessage = '';

  wallpapers: WallpaperResponse[] = [];
  themes: ThemeResponse[] = [];

  wallpaperMenuOpen = false;
  themeMenuOpen = false;

  isAddAppModalOpen = false;
  draggedApp: FavoriteAppVm | null = null;
  isTrashVisible = false;
  isTrashHover = false;

  isCreateFolderModalOpen = false;
  newFolderName = '';
  allSubmenus: RootMenuResponse[] = [];
  draggedFolder: FolderVm | null = null;
  isFolderDeleteConfirmOpen = false;
  folderPendingDeletion: FolderVm | null = null;

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
    this.groupId = history.state?.groupId ?? '';
    this.loadMenu();
    this.loadWallpapers();
    this.loadThemes();
  }

loadMenu(): void {
  if (!this.userId) {
    this.errorMessage = 'Missing user id.';
    return;
  }

  this.loading = true;
  this.errorMessage = '';

  forkJoin({
    rootMenu: this.menusService.getRootMenu(this.userId),
    submenuList: this.menusService.getSubMenus(this.userId)
  }).pipe(
    switchMap(({ rootMenu, submenuList }) => {
      const submenuIds = (submenuList ?? [])
        .map((item: SubMenuResponse) => item.id)
        .filter((id): id is string => !!id);

      if (submenuIds.length === 0) {
        return of({
          rootMenu,
          allSubmenus: [] as RootMenuResponse[]
        });
      }

      return forkJoin(
        submenuIds.map(id => this.menusService.getSubMenu(id))
      ).pipe(
        map((allSubmenus) => ({
          rootMenu,
          allSubmenus
        }))
      );
    })
  ).subscribe({
    next: ({ rootMenu, allSubmenus }) => {
      this.menu = rootMenu;
      this.allSubmenus = allSubmenus;
      this.loading = false;
    },
    error: (error) => {
      console.error(error);
      this.errorMessage = 'Failed to load menu.';
      this.loading = false;
    }
  });
}

  launchApplication(app: FavoriteAppVm): void {
    if (!this.userId || !app.menuItemId) {
      this.errorMessage = 'Missing application launch data.';
      return;
    }

    this.menusService.launchApplicationFromRootMenu(this.userId, app.menuItemId).subscribe({
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

  loadWallpapers(): void {
    this.customizationService.getAllWallpapers().subscribe({
      next: (wallpapers) => {
        this.wallpapers = wallpapers;
      },
      error: (error) => {
        console.error('Failed to load wallpapers', error);
      }
    });
  }

  loadThemes(): void {
    this.customizationService.getAllThemes().subscribe({
      next: (themes) => {
        this.themes = themes;
      },
      error: (error) => {
        console.error('Failed to load themes', error);
      }
    });
  }

  toggleWallpaperMenu(): void {
    this.wallpaperMenuOpen = !this.wallpaperMenuOpen;
    if (this.wallpaperMenuOpen) {
      this.themeMenuOpen = false;
      this.isAddAppModalOpen = false;
      this.isCreateFolderModalOpen = false;
    }
  }

  toggleThemeMenu(): void {
    this.themeMenuOpen = !this.themeMenuOpen;
    if (this.themeMenuOpen) {
      this.wallpaperMenuOpen = false;
      this.isAddAppModalOpen = false;
      this.isCreateFolderModalOpen = false;
    }
  }

  selectWallpaper(wallpaperId: string): void {
    const request: SelectWallpaperRequest = { wallpaperId };

    this.customizationService.selectWallpaper(this.userId, request).subscribe({
      next: () => {
        this.wallpaperMenuOpen = false;
        this.loadMenu();
      },
      error: (error) => {
        console.error(error);
        this.errorMessage = 'Failed to change wallpaper.';
      }
    });
  }

  selectTheme(themeId: string): void {
    const request: SelectThemeRequest = { themeId };

    this.customizationService.selectTheme(this.userId, request).subscribe({
      next: () => {
        this.themeMenuOpen = false;
        this.loadMenu();
      },
      error: (error) => {
        console.error(error);
        this.errorMessage = 'Failed to change theme.';
      }
    });
  }

  openAddAppModal(): void {
    this.isAddAppModalOpen = true;
    this.wallpaperMenuOpen = false;
    this.themeMenuOpen = false;
    this.isCreateFolderModalOpen = false;
  }

  closeAddAppModal(): void {
    this.isAddAppModalOpen = false;
  }

  getFavoriteApplications(): FavoriteAppVm[] {
    const items = this.menu?.applications ?? [];

    return items.map(item => ({
      menuItemId: item.id ?? '',
      name: item.name ?? 'Unknown App',
      icon: this.getAppIcon(item.name ?? ''),
      applicationId: this.getApplicationIdByName(item.name ?? '')
    }));
  }

  getFolders(): FolderVm[] {
    const submenus = this.menu?.subMenus ?? [];

    return submenus.map(submenu => {
      const details = this.allSubmenus.find(
        item => item.menuName === submenu.name
      );

      return {
        id: submenu.id ?? '',
        name: submenu.name ?? 'Folder',
        applicationCount: details?.applications?.length ?? 0
      };
    });
  }

  onFolderDragStart(folder: FolderVm): void {
  this.draggedFolder = folder;
  this.draggedApp = null;
  this.isTrashVisible = true;
  }

  onFolderDragEnd(): void {
    this.draggedFolder = null;
    this.isTrashVisible = false;
    this.isTrashHover = false;
  }

  removeFolder(folder: FolderVm): void {
    if (!this.userId || !folder.id) {
      this.errorMessage = 'Missing data for folder deletion.';
      return;
    }

    const submenuDetails = this.allSubmenus.find(
      item => item.menuName === folder.name
    );

    const applicationCount = submenuDetails?.applications?.length ?? 0;

    if (applicationCount > 0) {
      this.folderPendingDeletion = folder;
      this.isFolderDeleteConfirmOpen = true;
      return;
    }

    this.deleteFolderImmediately(folder);
  }

    confirmDeleteNonEmptyFolder(): void {
    if (!this.folderPendingDeletion) {
      return;
    }

    this.deleteFolderImmediately(this.folderPendingDeletion);
    this.closeFolderDeleteConfirm();
  }

  closeFolderDeleteConfirm(): void {
    this.isFolderDeleteConfirmOpen = false;
    this.folderPendingDeletion = null;
    this.draggedFolder = null;
    this.isTrashVisible = false;
    this.isTrashHover = false;
  }

  private deleteFolderImmediately(folder: FolderVm): void {
    this.menusService.deleteSubMenu(this.userId, folder.id).subscribe({
      next: () => {
        this.draggedFolder = null;
        this.isTrashVisible = false;
        this.isTrashHover = false;
        this.folderPendingDeletion = null;
        this.isFolderDeleteConfirmOpen = false;
        this.loadMenu();
      },
      error: (error) => {
        console.error(error);
        this.errorMessage = 'Failed to delete folder.';
      }
    });
  }

  getAvailableApplicationsToAdd(): DesktopAppOption[] {
    const assignedNames = this.getAssignedApplicationNamesAcrossSystem();
    return this.allApplications.filter(app => !assignedNames.has(app.name));
  }

  getAssignedApplicationNamesAcrossSystem(): Set<string> {
    const assigned = new Set<string>();

    for (const app of this.menu?.applications ?? []) {
      if (app.name) {
        assigned.add(app.name);
      }
    }

    for (const submenu of this.allSubmenus ?? []) {
      for (const app of submenu.applications ?? []) {
        if (app.name) {
          assigned.add(app.name);
        }
      }
    }

    return assigned;
  }

  addApplicationToFavorites(app: DesktopAppOption): void {
    if (!this.userId) {
      this.errorMessage = 'Missing user id.';
      return;
    }

    const request: AddApplicationRequest = {
      applicationId: app.id
    };

    this.menusService.addApplicationToRootMenu(this.userId, request).subscribe({
      next: () => {
        this.closeAddAppModal();
        this.loadMenu();
      },
      error: (error) => {
        console.error(error);
        this.errorMessage = 'Failed to add application.';
      }
    });
  }

  removeApplicationFromFavorites(app: FavoriteAppVm): void {
    if (!this.userId || !app.menuItemId) {
      this.errorMessage = 'Missing data for deletion.';
      return;
    }

    this.menusService.removeApplicationFromRootMenu(this.userId, app.menuItemId).subscribe({
      next: () => {
        this.draggedApp = null;
        this.isTrashVisible = false;
        this.isTrashHover = false;
        this.loadMenu();
      },
      error: (error) => {
        console.error(error);
        this.errorMessage = 'Failed to remove application.';
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

    if (this.draggedApp) {
      this.removeApplicationFromFavorites(this.draggedApp);
      return;
    }

    if (this.draggedFolder) {
      this.removeFolder(this.draggedFolder);
    }
  }

  openCreateFolderModal(): void {
    this.isCreateFolderModalOpen = true;
    this.newFolderName = '';
    this.wallpaperMenuOpen = false;
    this.themeMenuOpen = false;
    this.isAddAppModalOpen = false;
  }

  closeCreateFolderModal(): void {
    this.isCreateFolderModalOpen = false;
    this.newFolderName = '';
  }

  createFolder(): void {
    const trimmedName = this.newFolderName.trim();

    if (!trimmedName) {
      this.errorMessage = 'Folder name is required.';
      return;
    }

    const request: CreateSubMenuRequest = {
      name: trimmedName
    };

    this.menusService.createSubMenu(this.userId, request).subscribe({
      next: () => {
        this.closeCreateFolderModal();
        this.loadMenu();
      },
      error: (error) => {
        console.error(error);
        this.errorMessage = 'Failed to create folder.';
      }
    });
  }

  openFolder(submenuId: string): void {
    this.router.navigate(['/users', this.userId, 'submenus', submenuId], {
      state: {
        groupId: this.groupId
      }
    });
  }

  goBack(): void {
    if (this.groupId) {
      this.router.navigate(['/groups', this.groupId, 'login']);
    } else {
      this.router.navigate(['/groups']);
    }
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

  getDisplayMenuName(name?: string | null): string {
    if (!name) {
      return 'Main Menu';
    }

    return name
      .replace(' fömenüje', ' Main Menu')
      .replace(' főmenüje', ' Main Menu');
  }

  trackById(_: number, item: { id?: string | null }): string {
    return item.id ?? Math.random().toString();
  }

  trackByFavoriteApp(_: number, app: FavoriteAppVm): string {
    return app.menuItemId;
  }

  trackByAvailableApp(_: number, app: DesktopAppOption): string {
    return app.id;
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

interface FolderVm {
  id: string;
  name: string;
  applicationCount: number;
}