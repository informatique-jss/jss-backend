import { Component, Input, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { InvoicingBlockage } from 'src/app/modules/invoicing/model/InvoicingBlockage';
import { InvoicingBlockageService } from 'src/app/modules/invoicing/services/invoicing.blockage.service';
import { AppService } from 'src/app/services/app.service';
import { GenericSelectComponent } from '../generic-select/generic-select.component';

@Component({
  selector: 'select-invoicing-blockage',
  templateUrl: './../generic-select/generic-select.component.html',
  styleUrls: ['./../generic-select/generic-select.component.css'],
  standalone: false
})
export class SelectInvoicingBlockageComponent extends GenericSelectComponent<InvoicingBlockage> implements OnInit {

  @Input() types: InvoicingBlockage[] = [] as Array<InvoicingBlockage>;

  constructor(private formBuild: UntypedFormBuilder,
    private invoicingBlockageService: InvoicingBlockageService, private appService3: AppService) {
    super(formBuild, appService3)
  }

  initTypes(): void {
    this.invoicingBlockageService.getInvoicingBlockages().subscribe(response => {
      this.types = response;
    })
  }
}
