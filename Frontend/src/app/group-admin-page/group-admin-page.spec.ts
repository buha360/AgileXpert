import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GroupAdminPage } from './group-admin-page';

describe('GroupAdminPage', () => {
  let component: GroupAdminPage;
  let fixture: ComponentFixture<GroupAdminPage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [GroupAdminPage],
    }).compileComponents();

    fixture = TestBed.createComponent(GroupAdminPage);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
