/* tslint:disable:no-unused-variable */
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { DebugElement } from '@angular/core';

import { AutocompletePrincipalAccountingAccountComponent } from './autocomplete-principal-accounting-account.component';

describe('AutocompletePrincipalAccountingAccountComponent', () => {
  let component: AutocompletePrincipalAccountingAccountComponent;
  let fixture: ComponentFixture<AutocompletePrincipalAccountingAccountComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ AutocompletePrincipalAccountingAccountComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AutocompletePrincipalAccountingAccountComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
