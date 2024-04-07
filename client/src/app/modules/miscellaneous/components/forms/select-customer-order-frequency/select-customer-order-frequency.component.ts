import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { UserNoteService } from 'src/app/services/user.notes.service';
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
    private userNoteService2: UserNoteService,
    private customerOrderFrequencyService: CustomerOrderFrequencyService
  ) {
    super(formBuild, userNoteService2)
  }

  initTypes(): void {
    this.types = [];
    this.customerOrderFrequencyService.getCustomerOrderFrequencies().subscribe(response => {
      this.types = response;
      this.types = this.types.sort((a, b) => a.label.localeCompare(b.label));
    })
  }

}
