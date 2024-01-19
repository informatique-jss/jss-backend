/* tslint:disable:no-unused-variable */
import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { PrepaVisiteCustomerOrdersResponsableComponent } from './prepa-visite-customer-orders-responsable.component';

describe('ResponsableMainComponent', () => {
  let component: PrepaVisiteCustomerOrdersResponsableComponent;
  let fixture: ComponentFixture<PrepaVisiteCustomerOrdersResponsableComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ PrepaVisiteCustomerOrdersResponsableComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PrepaVisiteCustomerOrdersResponsableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
