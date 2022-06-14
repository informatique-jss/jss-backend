/* tslint:disable:no-unused-variable */
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { DebugElement } from '@angular/core';

import { RadioGroupCivilityComponent } from './radio-group-civility.component';

describe('RadioGroupCivilityComponent', () => {
  let component: RadioGroupCivilityComponent;
  let fixture: ComponentFixture<RadioGroupCivilityComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ RadioGroupCivilityComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RadioGroupCivilityComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
