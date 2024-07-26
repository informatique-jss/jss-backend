import { Component, Input, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { CustomerOrderStatus } from 'src/app/modules/quotation/model/CustomerOrderStatus';
import { CustomerOrderStatusService } from 'src/app/modules/quotation/services/customer.order.status.service';
import { GenericMultipleSelectComponent } from '../generic-select/generic-multiple-select.component';

@Component({
  selector: 'select-customer-order-status',
  templateUrl: './../generic-select/generic-multiple-select.component.html',
  styleUrls: ['./../generic-select/generic-select.component.html']
})

export class SelectCustomerOrderStatusComponent extends GenericMultipleSelectComponent<CustomerOrderStatus> implements OnInit {

  types: CustomerOrderStatus[] = [] as Array<CustomerOrderStatus>;

  /**
 * List of code to autoselect at loading
 */
  @Input() defaultCodesSelected: string[] | undefined;

  constructor(private formBuild: UntypedFormBuilder, private customerOrderStatusService: CustomerOrderStatusService,) {
    super(formBuild)
  }

  initTypes(): void {
    this.customerOrderStatusService.getCustomerOrderStatus().subscribe(response => {
      this.types = response;
      if (this.defaultCodesSelected) {
        this.model = [];
        for (let type of this.types) {
          for (let defaultCode of this.defaultCodesSelected) {
            if (type.code == defaultCode) {
              this.model.push(type);
            }
          }
        }
        this.modelChange.emit(this.model);
        this.selectionChange.emit(this.model);
      }
    });
  }
}
