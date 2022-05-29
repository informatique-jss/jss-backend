/* tslint:disable:no-unused-variable */
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { DebugElement } from '@angular/core';

import { AddressingComponent } from './addressing.component';

describe('AddressingComponent', () => {
  let component: AddressingComponent;
  let fixture: ComponentFixture<AddressingComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ AddressingComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AddressingComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
