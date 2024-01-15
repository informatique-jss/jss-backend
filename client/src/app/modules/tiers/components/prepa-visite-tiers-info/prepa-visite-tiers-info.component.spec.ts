/* tslint:disable:no-unused-variable */
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { DebugElement } from '@angular/core';

import { PrepaVisiteTiersComponent } from './prepa-visite-tiers-info.component';

describe('ResponsableMainComponent', () => {
  let component: PrepaVisiteTiersComponent;
  let fixture: ComponentFixture<PrepaVisiteTiersComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ PrepaVisiteTiersComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PrepaVisiteTiersComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
