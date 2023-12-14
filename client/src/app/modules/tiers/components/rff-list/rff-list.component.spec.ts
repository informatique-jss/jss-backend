/* tslint:disable:no-unused-variable */
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { DebugElement } from '@angular/core';

import { RffListComponent } from './rff-list.component';

describe('RffListComponent', () => {
  let component: RffListComponent;
  let fixture: ComponentFixture<RffListComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ RffListComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RffListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
