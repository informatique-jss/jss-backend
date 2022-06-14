/* tslint:disable:no-unused-variable */
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { DebugElement } from '@angular/core';

import { GenericInputComponent } from './generic-input.component';

describe('GenericInputComponent', () => {
  let component: GenericInputComponent;
  let fixture: ComponentFixture<GenericInputComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ GenericInputComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(GenericInputComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
