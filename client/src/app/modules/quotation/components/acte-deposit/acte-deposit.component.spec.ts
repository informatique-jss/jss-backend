/* tslint:disable:no-unused-variable */
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { DebugElement } from '@angular/core';

import { ActeDepositComponent } from './acte-deposit.component';

describe('ActeDepositComponent', () => {
  let component: ActeDepositComponent;
  let fixture: ComponentFixture<ActeDepositComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ActeDepositComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ActeDepositComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
