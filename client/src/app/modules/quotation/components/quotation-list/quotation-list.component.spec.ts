/* tslint:disable:no-unused-variable */
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { DebugElement } from '@angular/core';

import { QuotationListComponent } from './quotation-list.component';

describe('QuotationListComponent', () => {
  let component: QuotationListComponent;
  let fixture: ComponentFixture<QuotationListComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ QuotationListComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(QuotationListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
