/* tslint:disable:no-unused-variable */
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { DebugElement } from '@angular/core';

import { PrintRegisteredLabelDialogComponent } from './print-registered-label-dialog.component';

describe('PrintRegisteredLabelDialogComponent', () => {
  let component: PrintRegisteredLabelDialogComponent;
  let fixture: ComponentFixture<PrintRegisteredLabelDialogComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ PrintRegisteredLabelDialogComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PrintRegisteredLabelDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
