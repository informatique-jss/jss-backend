/* tslint:disable:no-unused-variable */
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { DebugElement } from '@angular/core';

import { DomiciliationComponent } from './domiciliation.component';

describe('DomiciliationComponent', () => {
  let component: DomiciliationComponent;
  let fixture: ComponentFixture<DomiciliationComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DomiciliationComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DomiciliationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
