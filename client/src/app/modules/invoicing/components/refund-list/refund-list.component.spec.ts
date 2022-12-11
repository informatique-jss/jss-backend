/* tslint:disable:no-unused-variable */
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { DebugElement } from '@angular/core';

import { RefundListComponent } from './refund-list.component';

describe('RefundListComponent', () => {
  let component: RefundListComponent;
  let fixture: ComponentFixture<RefundListComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ RefundListComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RefundListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
