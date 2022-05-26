/* tslint:disable:no-unused-variable */
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { DebugElement } from '@angular/core';

import { TiersAttachmentsComponent } from './tiers-attachments.component';

describe('TiersAttachmentsComponent', () => {
  let component: TiersAttachmentsComponent;
  let fixture: ComponentFixture<TiersAttachmentsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ TiersAttachmentsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TiersAttachmentsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
