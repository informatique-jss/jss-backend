/* tslint:disable:no-unused-variable */
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { DebugElement } from '@angular/core';

import { SelectInvoiceStatusOneComponent } from './select-invoice-status-one.component';

describe('SelectInvoiceStatusOneComponent', () => {
  let component: SelectInvoiceStatusOneComponent;
  let fixture: ComponentFixture<SelectInvoiceStatusOneComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ SelectInvoiceStatusOneComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SelectInvoiceStatusOneComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
