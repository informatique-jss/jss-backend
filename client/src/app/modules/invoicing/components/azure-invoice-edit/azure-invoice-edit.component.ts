import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { ConstantService } from 'src/app/modules/miscellaneous/services/constant.service';
import { AZURE_INVOICE_ENTITY_TYPE } from 'src/app/routing/search/search.component';
import { AppService } from 'src/app/services/app.service';
import { AZURE_CONFIDENT_THRESHOLD } from '../../../../libs/Constants';
import { AzureInvoice } from '../../model/AzureInvoice';
import { AzureInvoiceService } from '../../services/azure.invoice.service';

@Component({
  selector: 'app-azure-invoice-edit',
  templateUrl: './azure-invoice-edit.component.html',
  styleUrls: ['./azure-invoice-edit.component.css']
})
export class AzureInvoiceEditComponent implements OnInit {

  invoice: AzureInvoice | undefined;
  idAzureInvoice: number | undefined;

  constructor(
    private appService: AppService,
    private formBuilder: FormBuilder,
    private activatedRoute: ActivatedRoute,
    private azureInvoiceService: AzureInvoiceService,
    private constantService: ConstantService,
  ) { }

  invoiceForm = this.formBuilder.group({});
  payer: string = "";
  checkNumber: string = "";
  hideConfidentFields: boolean = true;
  confidentThreshold: number = AZURE_CONFIDENT_THRESHOLD;
  AZURE_INVOICE_ENTITY_TYPE = AZURE_INVOICE_ENTITY_TYPE;
  attachmentTypeProviderInvoice = this.constantService.getAttachmentTypeProviderInvoice();

  ngOnInit() {
    this.idAzureInvoice = this.activatedRoute.snapshot.params.idAzureInvoice;
    this.appService.changeHeaderTitle("Modifier une facture automatique")

    if (this.idAzureInvoice)
      this.azureInvoiceService.getAzureInvoice(this.idAzureInvoice).subscribe(response => this.invoice = response);
  }

  editInvoice() {
    if (this.invoice && this.invoiceForm.valid) {
      // Remove UTC delay
      if (this.invoice.invoiceDate)
        this.invoice.invoiceDate = new Date(this.invoice.invoiceDate.setHours(12));

      this.azureInvoiceService.updateAzureInvoice(this.invoice).subscribe(response => {
        this.appService.openRoute(null, "/invoicing", undefined);
      })
    }
  }

  disableInvoice() {
    if (this.invoice) {
      this.invoice.isDisabled = true;
      this.azureInvoiceService.updateAzureInvoice(this.invoice).subscribe(response => {
        this.appService.openRoute(null, "/invoicing", undefined);
      })
    }
  }
}
