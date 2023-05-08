/* tslint:disable:no-unused-variable */
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { DebugElement } from '@angular/core';

import { ReceiptReconciliationEditDialogComponent } from './receipt-reconciliation-edit-dialog.component';

describe('ReceiptReconciliationEditDialogComponent', () => {
  let component: ReceiptReconciliationEditDialogComponent;
  let fixture: ComponentFixture<ReceiptReconciliationEditDialogComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ReceiptReconciliationEditDialogComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ReceiptReconciliationEditDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
