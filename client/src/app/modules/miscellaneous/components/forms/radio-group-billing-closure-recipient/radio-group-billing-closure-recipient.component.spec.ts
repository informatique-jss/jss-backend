/* tslint:disable:no-unused-variable */
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { DebugElement } from '@angular/core';

import { RadioGroupBillingClosureRecipientComponent } from './radio-group-billing-closure-recipient.component';

describe('RadioGroupBillingClosureRecipientComponent', () => {
  let component: RadioGroupBillingClosureRecipientComponent;
  let fixture: ComponentFixture<RadioGroupBillingClosureRecipientComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ RadioGroupBillingClosureRecipientComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RadioGroupBillingClosureRecipientComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
