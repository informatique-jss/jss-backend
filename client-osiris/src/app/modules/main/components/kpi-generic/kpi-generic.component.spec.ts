/* tslint:disable:no-unused-variable */
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { DebugElement } from '@angular/core';

import { KpiGenericComponent } from './kpi-generic.component';

describe('KpiGenericComponent', () => {
  let component: KpiGenericComponent;
  let fixture: ComponentFixture<KpiGenericComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ KpiGenericComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(KpiGenericComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
