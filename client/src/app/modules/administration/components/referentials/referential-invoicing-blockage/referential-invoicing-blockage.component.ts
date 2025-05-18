import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import { InvoicingBlockage } from 'src/app/modules/invoicing/model/InvoicingBlockage';
import { InvoicingBlockageService } from 'src/app/modules/invoicing/services/invoicing.blockage.service';
import { AppService } from 'src/app/services/app.service';
import { GenericReferentialComponent } from '../generic-referential/generic-referential-component';

@Component({
  selector: 'referential-invoicing-blockage',
  templateUrl: './../generic-referential/generic-referential.component.html',
  styleUrls: ['./../generic-referential/generic-referential.component.css']
})
export class ReferentialInvoicingBlockageComponent extends GenericReferentialComponent<InvoicingBlockage> implements OnInit {
  constructor(private invoicingBlockageService: InvoicingBlockageService,
    private formBuilder2: FormBuilder,
    private appService2: AppService,) {
    super(formBuilder2, appService2);
  }

  getAddOrUpdateObservable(): Observable<InvoicingBlockage> {
    return this.invoicingBlockageService.addOrUpdateInvoicingBlockage(this.selectedEntity!);
  }
  getGetObservable(): Observable<InvoicingBlockage[]> {
    return this.invoicingBlockageService.getInvoicingBlockages();
  }
}
