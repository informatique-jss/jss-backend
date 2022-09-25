/* tslint:disable:no-unused-variable */
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { DebugElement } from '@angular/core';

import { OrderingListComponent } from './ordering-list.component';

describe('OrderingListComponent', () => {
  let component: OrderingListComponent;
  let fixture: ComponentFixture<OrderingListComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ OrderingListComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(OrderingListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
