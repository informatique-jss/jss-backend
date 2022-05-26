/* tslint:disable:no-unused-variable */
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { DebugElement } from '@angular/core';

import { UploadTiersAttachementDialogComponent } from './upload-tiers-attachement-dialog.component';

describe('UploadTiersAttachementDialogComponent', () => {
  let component: UploadTiersAttachementDialogComponent;
  let fixture: ComponentFixture<UploadTiersAttachementDialogComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ UploadTiersAttachementDialogComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(UploadTiersAttachementDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
