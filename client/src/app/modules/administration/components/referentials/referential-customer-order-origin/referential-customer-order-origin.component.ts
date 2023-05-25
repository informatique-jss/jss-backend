import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import { CustomerOrderOrigin } from 'src/app/modules/miscellaneous/model/CustomerOrderOrigin';
import { CustomerOrderOriginService } from 'src/app/modules/miscellaneous/services/customer.order.origin.service';
import { AppService } from 'src/app/services/app.service';
import { GenericReferentialComponent } from '../generic-referential/generic-referential-component';

@Component({
  selector: 'referential-customer-order-origin',
  templateUrl: './referential-customer-order-origin.component.html',
  styleUrls: ['./../generic-referential/generic-referential.component.css']
})
export class ReferentialCustomerOrderOriginComponent extends GenericReferentialComponent<CustomerOrderOrigin> implements OnInit {
  constructor(private customerOrderOriginService: CustomerOrderOriginService,
    private formBuilder2: FormBuilder,
    private appService2: AppService,) {
    super(formBuilder2, appService2);
  }

  getAddOrUpdateObservable(): Observable<CustomerOrderOrigin> {
    return this.customerOrderOriginService.addOrUpdateCustomerOrderOrigin(this.selectedEntity!);
  }
  getGetObservable(): Observable<CustomerOrderOrigin[]> {
    return this.customerOrderOriginService.getCustomerOrderOrigins();
  }
}
