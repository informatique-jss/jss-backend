/* tslint:disable:no-unused-variable */
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { DebugElement } from '@angular/core';

import { BodaccSplitComponent } from './bodacc-split.component';

describe('BodaccSplitComponent', () => {
  let component: BodaccSplitComponent;
  let fixture: ComponentFixture<BodaccSplitComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ BodaccSplitComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(BodaccSplitComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
