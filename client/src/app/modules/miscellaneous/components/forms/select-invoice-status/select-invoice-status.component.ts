import { Component, Input, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { InvoiceStatus } from 'src/app/modules/invoicing/model/InvoiceStatus';
import { InvoiceStatusService } from 'src/app/modules/invoicing/services/invoice.status.service';
import { GenericMultipleSelectComponent } from '../generic-select/generic-multiple-select.component';

@Component({
  selector: 'select-invoice-status',
  templateUrl: './select-invoice-status.component.html',
  styleUrls: ['./select-invoice-status.component.css']
})
export class SelectInvoiceStatusComponent extends GenericMultipleSelectComponent<InvoiceStatus> implements OnInit {

  types: InvoiceStatus[] = [] as Array<InvoiceStatus>;

  /**
 * List of code to autoselect at loading
 */
  @Input() defaultStatusSelected: InvoiceStatus[] | undefined;

  constructor(private formBuild: UntypedFormBuilder, private invoiceStatusService: InvoiceStatusService,) {
    super(formBuild)
  }

  initTypes(): void {
    this.invoiceStatusService.getInvoiceStatus().subscribe(response => {
      this.types = response;
      if (this.defaultStatusSelected) {
        this.model = [];
        for (let type of this.types) {
          for (let defaultStatus of this.defaultStatusSelected) {
            if (type.id == defaultStatus.id) {
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
