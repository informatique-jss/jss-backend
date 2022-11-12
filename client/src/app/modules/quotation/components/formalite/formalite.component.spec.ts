/* tslint:disable:no-unused-variable */
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { DebugElement } from '@angular/core';

import { FormaliteComponent } from './formalite.component';

describe('FormaliteComponent', () => {
  let component: FormaliteComponent;
  let fixture: ComponentFixture<FormaliteComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ FormaliteComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(FormaliteComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
