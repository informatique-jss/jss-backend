import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { CustomerOrderOrigin } from '../../../model/CustomerOrderOrigin';
import { CustomerOrderOriginService } from '../../../services/customer.order.origin.service';
import { GenericSelectComponent } from '../generic-select/generic-select.component';
import { AppService } from 'src/app/services/app.service';

@Component({
  selector: 'select-customer-order-origin',
  templateUrl: './../generic-select/generic-select.component.html',
  styleUrls: ['./../generic-select/generic-select.component.css']
})
export class SelectCustomerOrderOriginComponent extends GenericSelectComponent<CustomerOrderOrigin> implements OnInit {

  types: CustomerOrderOrigin[] = [] as Array<CustomerOrderOrigin>;

  constructor(private formBuild: UntypedFormBuilder,
    private customerOrderOriginService: CustomerOrderOriginService, private appService3: AppService) {
    super(formBuild, appService3)
  }

  initTypes(): void {
    this.customerOrderOriginService.getCustomerOrderOrigins().subscribe(response => {
      this.types = response;
    })
  }
}
