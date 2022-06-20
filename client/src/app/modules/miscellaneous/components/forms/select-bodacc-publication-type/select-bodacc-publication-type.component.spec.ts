/* tslint:disable:no-unused-variable */
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { DebugElement } from '@angular/core';

import { SelectBodaccPublicationTypeComponent } from './select-bodacc-publication-type.component';

describe('SelectBodaccPublicationTypeComponent', () => {
  let component: SelectBodaccPublicationTypeComponent;
  let fixture: ComponentFixture<SelectBodaccPublicationTypeComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ SelectBodaccPublicationTypeComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SelectBodaccPublicationTypeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
