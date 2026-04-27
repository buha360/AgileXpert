import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UserMenuPage } from './user-menu-page';

describe('UserMenuPage', () => {
  let component: UserMenuPage;
  let fixture: ComponentFixture<UserMenuPage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UserMenuPage],
    }).compileComponents();

    fixture = TestBed.createComponent(UserMenuPage);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
