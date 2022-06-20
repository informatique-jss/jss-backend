/* tslint:disable:no-unused-variable */
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { DebugElement } from '@angular/core';

import { RadioGroupQuotationLabelTypeComponent } from './radio-group-quotation-label-type.component';

describe('RadioGroupQuotationLabelTypeComponent', () => {
  let component: RadioGroupQuotationLabelTypeComponent;
  let fixture: ComponentFixture<RadioGroupQuotationLabelTypeComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ RadioGroupQuotationLabelTypeComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RadioGroupQuotationLabelTypeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
