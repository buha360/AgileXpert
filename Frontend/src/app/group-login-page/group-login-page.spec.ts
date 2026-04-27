import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GroupLoginPage } from './group-login-page';

describe('GroupLoginPage', () => {
  let component: GroupLoginPage;
  let fixture: ComponentFixture<GroupLoginPage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [GroupLoginPage],
    }).compileComponents();

    fixture = TestBed.createComponent(GroupLoginPage);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
