/* tslint:disable:no-unused-variable */
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { DebugElement } from '@angular/core';

import { SelectCompetentAuthorityTypeComponent } from './select-competent-authority-type.component';

describe('SelectCompetentAuthorityTypeComponent', () => {
  let component: SelectCompetentAuthorityTypeComponent;
  let fixture: ComponentFixture<SelectCompetentAuthorityTypeComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ SelectCompetentAuthorityTypeComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SelectCompetentAuthorityTypeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
