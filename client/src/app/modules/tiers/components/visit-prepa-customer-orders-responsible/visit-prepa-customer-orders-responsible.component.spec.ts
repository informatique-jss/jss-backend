/* tslint:disable:no-unused-variable */
import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { VisitPrepaCustomerOrdersResponsibleComponent } from './visit-prepa-customer-orders-responsible.component';

describe('ResponsableMainComponent', () => {
  let component: VisitPrepaCustomerOrdersResponsibleComponent;
  let fixture: ComponentFixture<VisitPrepaCustomerOrdersResponsibleComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ VisitPrepaCustomerOrdersResponsibleComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(VisitPrepaCustomerOrdersResponsibleComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
