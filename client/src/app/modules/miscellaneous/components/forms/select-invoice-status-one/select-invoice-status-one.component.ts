import { Component, Input, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { InvoiceStatus } from 'src/app/modules/invoicing/model/InvoiceStatus';
import { InvoiceStatusService } from 'src/app/modules/invoicing/services/invoice.status.service';
import { GenericSelectComponent } from '../generic-select/generic-select.component';

@Component({
  selector: 'select-invoice-status-one',
  templateUrl: './select-invoice-status-one.component.html',
  styleUrls: ['./select-invoice-status-one.component.css']
})
export class SelectInvoiceStatusOneComponent extends GenericSelectComponent<InvoiceStatus> implements OnInit {

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
        this.model = undefined;
        for (let type of this.types) {
          for (let defaultStatus of this.defaultStatusSelected) {
            if (type.id == defaultStatus.id) {
              this.model = type;
            }
          }
        }
        this.modelChange.emit(this.model);
        this.selectionChange.emit(this.model);
      }
    });
  }
}
