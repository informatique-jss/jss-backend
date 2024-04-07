import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import { CustomerOrderFrequency } from 'src/app/modules/miscellaneous/model/CustomerOrderFrequency';
import { CustomerOrderFrequencyService } from 'src/app/modules/miscellaneous/services/customer.order.frequency.service';
import { AppService } from 'src/app/services/app.service';
import { GenericReferentialComponent } from '../generic-referential/generic-referential-component';

@Component({
  selector: 'referential-customer-order-frequency',
  templateUrl: './referential-customer-order-frequency.component.html',
  styleUrls: ['./../generic-referential/generic-referential.component.css']
})
export class ReferentialCustomerOrderFrequencyComponent extends GenericReferentialComponent<CustomerOrderFrequency> implements OnInit {
  constructor(private customerOrderFrequencyService: CustomerOrderFrequencyService,
    private formBuilder2: FormBuilder,
    private appService2: AppService,) {
    super(formBuilder2, appService2);
  }

  getAddOrUpdateObservable(): Observable<CustomerOrderFrequency> {
    return this.customerOrderFrequencyService.addOrUpdateCustomerOrderFrequency(this.selectedEntity!);
  }
  getGetObservable(): Observable<CustomerOrderFrequency[]> {
    return this.customerOrderFrequencyService.getCustomerOrderFrequencies();
  }
}
