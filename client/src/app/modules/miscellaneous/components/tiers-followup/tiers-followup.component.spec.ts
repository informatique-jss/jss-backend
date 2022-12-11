/* tslint:disable:no-unused-variable */
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { DebugElement } from '@angular/core';

import { TiersFollowupComponent } from './tiers-followup.component';

describe('TiersFollowupComponent', () => {
  let component: TiersFollowupComponent;
  let fixture: ComponentFixture<TiersFollowupComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ TiersFollowupComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TiersFollowupComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
