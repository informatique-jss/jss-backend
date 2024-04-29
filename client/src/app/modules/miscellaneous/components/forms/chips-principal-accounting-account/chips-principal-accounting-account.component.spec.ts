/* tslint:disable:no-unused-variable */
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { DebugElement } from '@angular/core';

import { ChipsPrincipalAccountingAccountComponent } from './chips-principal-accounting-account.component';

describe('ChipsPrincipalAccountingAccountComponent', () => {
  let component: ChipsPrincipalAccountingAccountComponent;
  let fixture: ComponentFixture<ChipsPrincipalAccountingAccountComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ChipsPrincipalAccountingAccountComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ChipsPrincipalAccountingAccountComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
