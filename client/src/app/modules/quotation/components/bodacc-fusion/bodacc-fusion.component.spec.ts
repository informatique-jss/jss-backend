/* tslint:disable:no-unused-variable */
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { DebugElement } from '@angular/core';

import { BodaccFusionComponent } from './bodacc-fusion.component';

describe('BodaccFusionComponent', () => {
  let component: BodaccFusionComponent;
  let fixture: ComponentFixture<BodaccFusionComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ BodaccFusionComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(BodaccFusionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
