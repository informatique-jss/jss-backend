import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import { InvoiceStatus } from 'src/app/modules/invoicing/model/InvoiceStatus';
import { InvoiceStatusService } from 'src/app/modules/invoicing/services/invoice.status.service';
import { AppService } from 'src/app/services/app.service';
import { GenericReferentialComponent } from '../generic-referential/generic-referential-component';

@Component({
  selector: 'referential-invoice-status',
  templateUrl: './../generic-referential/generic-referential.component.html',
  styleUrls: ['./../generic-referential/generic-referential.component.css']
})
export class ReferentialInvoiceStatusComponent extends GenericReferentialComponent<InvoiceStatus> implements OnInit {
  constructor(private invoiceStatusService: InvoiceStatusService,
    private formBuilder2: FormBuilder,
    private appService2: AppService,) {
    super(formBuilder2, appService2);
  }

  getAddOrUpdateObservable(): Observable<InvoiceStatus> {
    return this.invoiceStatusService.addOrUpdateInvoiceStatus(this.selectedEntity!);
  }
  getGetObservable(): Observable<InvoiceStatus[]> {
    return this.invoiceStatusService.getInvoiceStatus();
  }
}
