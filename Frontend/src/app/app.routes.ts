import { Routes } from '@angular/router';
import { GroupsPageComponent } from './groups-page/groups-page';
import { GroupLoginPageComponent } from './group-login-page/group-login-page';
import { UserMenuPageComponent } from './user-menu-page/user-menu-page';
import { GroupAdminPageComponent } from './group-admin-page/group-admin-page';

export const routes: Routes = [
  { path: '', redirectTo: 'groups', pathMatch: 'full' },
  { path: 'groups', component: GroupsPageComponent },
  { path: 'groups/:groupId/login', component: GroupLoginPageComponent },
  { path: 'users/:userId/menu', component: UserMenuPageComponent },
  { path: 'groups/:groupId/admin', component: GroupAdminPageComponent }
];