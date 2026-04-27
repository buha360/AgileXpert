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

    this.menusService.getRootMenu(this.userId).subscribe({
      next: (menu) => {
        this.menu = menu;
        this.loading = false;
      },
      error: (error) => {
        console.error(error);
        this.errorMessage = 'Failed to load menu.';
        this.loading = false;
      }
    });
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
    }
  }

  toggleThemeMenu(): void {
    this.themeMenuOpen = !this.themeMenuOpen;
    if (this.themeMenuOpen) {
      this.wallpaperMenuOpen = false;
    }
  }

  selectWallpaper(wallpaperId: string): void {
    const request: SelectWallpaperRequest = {
      wallpaperId: wallpaperId
    };

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
    const request: SelectThemeRequest = {
      themeId: themeId
    };

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
}