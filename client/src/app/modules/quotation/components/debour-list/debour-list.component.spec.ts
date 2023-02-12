/* tslint:disable:no-unused-variable */
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { DebugElement } from '@angular/core';

import { DebourListComponent } from './debour-list.component';

describe('DebourListComponent', () => {
  let component: DebourListComponent;
  let fixture: ComponentFixture<DebourListComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DebourListComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DebourListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
