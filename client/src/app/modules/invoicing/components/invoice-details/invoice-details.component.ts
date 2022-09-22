import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Vat } from 'src/app/modules/miscellaneous/model/Vat';
import { Invoice } from 'src/app/modules/quotation/model/Invoice';
import { IQuotation } from 'src/app/modules/quotation/model/IQuotation';
import { AppService } from 'src/app/services/app.service';
import { InvoiceService } from '../../services/invoice.service';
import { InvoiceListComponent } from '../invoice-list/invoice-list.component';

@Component({
  selector: 'app-invoice-details',
  templateUrl: './invoice-details.component.html',
  styleUrls: ['./invoice-details.component.css']
})
export class InvoiceDetailsComponent implements OnInit {

  invoice: Invoice | undefined;

  constructor(private formBuilder: FormBuilder,
    private invoiceService: InvoiceService,
    private appService: AppService,
    private activatedRoute: ActivatedRoute,
  ) { }

  ngOnInit() {
    let idInvoice: number = this.activatedRoute.snapshot.params.id;

    if (idInvoice) {
      this.appService.changeHeaderTitle("Facture n°" + idInvoice);
      this.invoiceService.getInvoiceById(idInvoice).subscribe(response => {
        this.invoice = response;
        this.appService.changeHeaderTitle("Facture n°" + idInvoice + " - " + this.invoice.billingLabel);
      })
    }
  }

  invoiceDetailForm = this.formBuilder.group({
  });

  getCustomerOrderName = InvoiceListComponent.getCustomerOrderName;
  getResponsableName = InvoiceListComponent.getResponsableName;
  getAffaireList = InvoiceListComponent.getAffaireList;

  computePreTaxPriceTotal(quotation: IQuotation): number {
    let preTaxPrice = 0;
    if (quotation && quotation.provisions) {
      for (let provision of quotation.provisions) {
        if (provision.invoiceItems) {
          for (let invoiceItem of provision.invoiceItems) {
            preTaxPrice += parseFloat(invoiceItem.preTaxPrice + "");
          }
        }
      }
    }
    return preTaxPrice;
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
