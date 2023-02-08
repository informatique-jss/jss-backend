import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ProvisionBoardComponent } from './provision-board.component';

describe('ProvisionBoardComponent', () => {
  let component: ProvisionBoardComponent;
  let fixture: ComponentFixture<ProvisionBoardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ProvisionBoardComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ProvisionBoardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
