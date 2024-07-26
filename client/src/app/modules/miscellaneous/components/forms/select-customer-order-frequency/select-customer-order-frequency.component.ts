import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { CustomerOrderFrequency } from '../../../model/CustomerOrderFrequency';
import { CustomerOrderFrequencyService } from '../../../services/customer.order.frequency.service';
import { GenericSelectComponent } from '../generic-select/generic-select.component';

@Component({
  selector: 'select-customer-order-frequency',
  templateUrl: '../generic-select/generic-select.component.html',
  styleUrls: ['../generic-select/generic-select.component.css']
})
export class SelectCustomerOrderFrequencyComponent extends GenericSelectComponent<CustomerOrderFrequency> implements OnInit {

  types: CustomerOrderFrequency[] = [] as Array<CustomerOrderFrequency>;

  constructor(private formBuild: UntypedFormBuilder,
    private customerOrderFrequencyService: CustomerOrderFrequencyService
  ) {
    super(formBuild,)
  }

  initTypes(): void {
    this.types = [];
    this.customerOrderFrequencyService.getCustomerOrderFrequencies().subscribe(response => {
      this.types = response;
      this.types = this.types.sort((a, b) => a.label.localeCompare(b.label));
    })
  }

}
