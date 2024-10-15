/* tslint:disable:no-unused-variable */
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { DebugElement } from '@angular/core';

import { SingleUploadComponent } from './single-upload.component';

describe('SingleUploadComponent', () => {
  let component: SingleUploadComponent;
  let fixture: ComponentFixture<SingleUploadComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ SingleUploadComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SingleUploadComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
