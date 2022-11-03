import { Component, Input, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Vat } from 'src/app/modules/miscellaneous/model/Vat';
import { ConstantService } from 'src/app/modules/miscellaneous/services/constant.service';
import { QuotationComponent } from 'src/app/modules/quotation/components/quotation/quotation.component';
import { Affaire } from 'src/app/modules/quotation/model/Affaire';
import { Invoice } from 'src/app/modules/quotation/model/Invoice';
import { IQuotation } from 'src/app/modules/quotation/model/IQuotation';
import { INVOICE_ENTITY_TYPE } from 'src/app/routing/search/search.component';
import { AppService } from 'src/app/services/app.service';
import { InvoiceService } from '../../services/invoice.service';
import { getAffaireList, getAffaireListArray, getAmountRemaining, getCustomerOrderForInvoice, getCustomerOrderNameForInvoice, getLetteringDate, getResponsableName } from '../invoice-tools';

@Component({
  selector: 'app-invoice-details',
  templateUrl: './invoice-details.component.html',
  styleUrls: ['./invoice-details.component.css']
})
export class InvoiceDetailsComponent implements OnInit {

  @Input() invoice: Invoice | undefined;
  getAmountRemaining = getAmountRemaining;
  getAffaireList = getAffaireList;
  getAffaireListArray = getAffaireListArray;
  INVOICE_ENTITY_TYPE = INVOICE_ENTITY_TYPE;

  constructor(private formBuilder: FormBuilder,
    private invoiceService: InvoiceService,
    private appService: AppService,
    private activatedRoute: ActivatedRoute,
    private constantService: ConstantService,
  ) { }

  invoiceStatusSend = this.constantService.getInvoiceStatusSend();

  ngOnInit() {
    this.refreshData();
  }

  onStateChange() {
    this.refreshData();
  }

  refreshData() {
    let idInvoice: number = this.activatedRoute.snapshot.params.id;

    if (idInvoice) {
      this.appService.changeHeaderTitle("Facture n°" + idInvoice);
      this.invoiceService.getInvoiceById(idInvoice).subscribe(response => {
        this.invoice = response;
        this.appService.changeHeaderTitle("Facture n°" + idInvoice + " - " + this.invoice.billingLabel + " - " + this.invoice.invoiceStatus.label);
      })
    }
  }

  invoiceDetailForm = this.formBuilder.group({
  });

  getCustomerOrderName = getCustomerOrderNameForInvoice;
  getCustomerOrder = getCustomerOrderForInvoice;
  getResponsableName = getResponsableName;
  getLetteringDate = getLetteringDate;

  computePreTaxPriceTotal(quotation: IQuotation): number {
    return QuotationComponent.computePreTaxPriceTotal(quotation);
  }
  getFirstItemIndex(invoice: Invoice, affaire: Affaire) {
    if (invoice && affaire) {
      for (let i = 0; i < invoice.invoiceItems.length; i++) {
        const invoiceItem = invoice.invoiceItems[i];
        if (invoiceItem.provision.assoAffaireOrder.affaire.id == affaire.id)
          return i;
      }
    }
    return 0;
  }
  getPreTaxPriceTotal(): number {
    let preTaxPrice = 0;
    if (this.invoice && this.invoice.invoiceItems) {
      for (let invoiceItem of this.invoice.invoiceItems) {
        preTaxPrice += invoiceItem.preTaxPrice;
      }
    }
    return preTaxPrice;
  }

  getDiscountTotal(): number {
    let discountAmount = 0;
    if (this.invoice && this.invoice.invoiceItems) {
      for (let invoiceItem of this.invoice.invoiceItems) {
        discountAmount += parseFloat(invoiceItem.discountAmount + "");
      }
    }
    return discountAmount;
  }

  getVatTotal(): number {
    let vat = 0;
    if (this.invoice && this.invoice.invoiceItems) {
      for (let invoiceItem of this.invoice.invoiceItems) {
        vat += invoiceItem.vatPrice;
      }
    }
    return vat;
  }

  getApplicableVat(): Vat | undefined {
    if (this.invoice && this.invoice.invoiceItems) {
      for (let invoiceItem of this.invoice.invoiceItems) {
        if (invoiceItem.vat)
          return invoiceItem.vat;
      }
    }
    return undefined;
  }

  getPriceTotal(): number {
    return this.getPreTaxPriceTotal() - this.getDiscountTotal() + this.getVatTotal();
  }

}
