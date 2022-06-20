/* tslint:disable:no-unused-variable */
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { DebugElement } from '@angular/core';

import { RadioGroupTransfertFundsComponent } from './radio-group-transfert-funds.component';

describe('RadioGroupTransfertFundsComponent', () => {
  let component: RadioGroupTransfertFundsComponent;
  let fixture: ComponentFixture<RadioGroupTransfertFundsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ RadioGroupTransfertFundsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RadioGroupTransfertFundsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
