/* tslint:disable:no-unused-variable */
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { DebugElement } from '@angular/core';

import { EditAmountInvoiceItemDialogComponent } from './edit-amount-invoice-item-dialog.component';

describe('EditAmountInvoiceItemDialogComponent', () => {
  let component: EditAmountInvoiceItemDialogComponent;
  let fixture: ComponentFixture<EditAmountInvoiceItemDialogComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ EditAmountInvoiceItemDialogComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(EditAmountInvoiceItemDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
