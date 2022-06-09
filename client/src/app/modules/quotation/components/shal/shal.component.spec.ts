/* tslint:disable:no-unused-variable */
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { DebugElement } from '@angular/core';

import { ShalComponent } from './shal.component';

describe('ShalComponent', () => {
  let component: ShalComponent;
  let fixture: ComponentFixture<ShalComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ShalComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ShalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
