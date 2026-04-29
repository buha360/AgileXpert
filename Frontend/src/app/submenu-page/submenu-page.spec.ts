import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SubmenuPageComponent } from './submenu-page';

describe('SubmenuPageComponent', () => {
  let component: SubmenuPageComponent;
  let fixture: ComponentFixture<SubmenuPageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SubmenuPageComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(SubmenuPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
