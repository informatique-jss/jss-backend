import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { UserNoteService } from 'src/app/services/user.notes.service';
import { CustomerOrderOrigin } from '../../../model/CustomerOrderOrigin';
import { CustomerOrderOriginService } from '../../../services/customer.order.origin.service';
import { GenericSelectComponent } from '../generic-select/generic-select.component';

@Component({
  selector: 'select-customer-order-origin',
  templateUrl: './../generic-select/generic-select.component.html',
  styleUrls: ['./../generic-select/generic-select.component.css']
})
export class SelectCustomerOrderOriginComponent extends GenericSelectComponent<CustomerOrderOrigin> implements OnInit {

  types: CustomerOrderOrigin[] = [] as Array<CustomerOrderOrigin>;

  constructor(private formBuild: UntypedFormBuilder,
    private customerOrderOriginService: CustomerOrderOriginService
    , private userNoteService2: UserNoteService,) {
    super(formBuild, userNoteService2)
  }

  initTypes(): void {
    this.customerOrderOriginService.getCustomerOrderOrigins().subscribe(response => {
      this.types = response;
    })
  }
}
