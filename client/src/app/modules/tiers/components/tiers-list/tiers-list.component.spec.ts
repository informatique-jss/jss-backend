/* tslint:disable:no-unused-variable */
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { DebugElement } from '@angular/core';

import { TiersListComponent } from './tiers-list.component';

describe('TiersListComponent', () => {
  let component: TiersListComponent;
  let fixture: ComponentFixture<TiersListComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ TiersListComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TiersListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
