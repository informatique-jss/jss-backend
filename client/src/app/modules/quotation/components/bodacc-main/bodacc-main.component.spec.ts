/* tslint:disable:no-unused-variable */
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { DebugElement } from '@angular/core';

import { BodaccMainComponent } from './bodacc-main.component';

describe('BodaccMainComponent', () => {
  let component: BodaccMainComponent;
  let fixture: ComponentFixture<BodaccMainComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ BodaccMainComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(BodaccMainComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
