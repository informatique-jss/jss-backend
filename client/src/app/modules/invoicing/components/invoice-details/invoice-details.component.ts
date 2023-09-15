import { Component, Input, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { ConstantService } from 'src/app/modules/miscellaneous/services/constant.service';
import { QuotationComponent } from 'src/app/modules/quotation/components/quotation/quotation.component';
import { Affaire } from 'src/app/modules/quotation/model/Affaire';
import { IQuotation } from 'src/app/modules/quotation/model/IQuotation';
import { Invoice } from 'src/app/modules/quotation/model/Invoice';
import { VatBase } from 'src/app/modules/quotation/model/VatBase';
import { QuotationService } from 'src/app/modules/quotation/services/quotation.service';
import { CUSTOMER_ORDER_ENTITY_TYPE, INVOICE_ENTITY_TYPE } from 'src/app/routing/search/search.component';
import { AppService } from 'src/app/services/app.service';
import { HabilitationsService } from 'src/app/services/habilitations.service';
import { instanceOfConfrere, instanceOfResponsable, instanceOfTiers } from '../../../../libs/TypeHelper';
import { ITiers } from '../../../tiers/model/ITiers';
import { InvoiceService } from '../../services/invoice.service';
import { getAffaireList, getAffaireListArray, getCustomerOrderForInvoice, getCustomerOrderNameForInvoice, getLetteringDate, getProviderLabelForInvoice, getRemainingToPay, getResponsableName } from '../invoice-tools';

@Component({
  selector: 'app-invoice-details',
  templateUrl: './invoice-details.component.html',
  styleUrls: ['./invoice-details.component.css']
})
export class InvoiceDetailsComponent implements OnInit {

  @Input() invoice: Invoice | undefined;
  getAmountRemaining = getRemainingToPay;
  getAffaireList = getAffaireList;
  getAffaireListArray = getAffaireListArray;
  INVOICE_ENTITY_TYPE = INVOICE_ENTITY_TYPE;

  constructor(private formBuilder: FormBuilder,
    private invoiceService: InvoiceService,
    private appService: AppService,
    private activatedRoute: ActivatedRoute,
    private constantService: ConstantService,
    private habilitationService: HabilitationsService,
    private quotationService: QuotationService,
  ) { }

  invoiceStatusSend = this.constantService.getInvoiceStatusSend();
  invoiceStatusPayed = this.constantService.getInvoiceStatusPayed();
  invoiceStatusReceived = this.constantService.getInvoiceStatusReceived();
  invoiceStatusCreditNoteReceived = this.constantService.getInvoiceStatusCreditNoteReceived();
  attachmentTypeInvoice = this.constantService.getAttachmentTypeInvoice();
  attachmentTypeCreditNote = this.constantService.getAttachmentTypeCreditNote();

  CUSTOMER_ORDER_ENTITY_TYPE = CUSTOMER_ORDER_ENTITY_TYPE;

  ngOnInit() {
    this.refreshData();
  }

  onStateChange() {
    this.refreshData();
  }

  refreshData() {
    let idInvoice: number = this.activatedRoute.snapshot.params.id;

    if (idInvoice) {
      this.appService.changeHeaderTitle("Facture/avoir n°" + idInvoice);
      this.invoiceService.getInvoiceById(idInvoice).subscribe(response => {
        this.invoice = response;
        this.appService.changeHeaderTitle((this.invoice.isCreditNote || this.invoice.isProviderCreditNote ? "Avoir" : "Facture") + " n°" + idInvoice + " - " + this.invoice.invoiceStatus.label);
      })
    }
  }

  beforeToday(date: Date): boolean {
    return (new Date(date)).getTime() < (new Date()).getTime();
  }

  invoiceDetailForm = this.formBuilder.group({
  });

  getCustomerOrderName = getCustomerOrderNameForInvoice;
  getProviderName = getProviderLabelForInvoice;

  getCustomerOrder(invoice: Invoice): any {
    if (invoice) {
      if (invoice.provider)
        return invoice.provider
      return getCustomerOrderForInvoice(invoice);
    }
    return {} as ITiers;
  }

  openCustomerOrderLink(event: any) {
    if (this.invoice) {
      let link: string[] = [];
      let tiers = this.getCustomerOrder(this.invoice);
      if (this.invoice.provider)
        link = ['/provider', this.invoice.provider.id + ""];
      else if (tiers) {
        if (instanceOfTiers(tiers))
          link = ['/tiers', tiers.id + ""];
        if (instanceOfResponsable(tiers))
          link = ['/tiers/responsable', tiers.id + ""];
        if (instanceOfConfrere(tiers))
          link = ['/confrere', tiers.id + ""];
      }

      this.appService.openRoute(event, link.join("/"), null);
    }
  }

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
        discountAmount += parseFloat((invoiceItem.discountAmount ? invoiceItem.discountAmount : 0) + "");
      }
    }
    return discountAmount;
  }

  getVatTotal(): number {
    let vat = 0;
    if (this.invoice && this.invoice.invoiceItems) {
      for (let invoiceItem of this.invoice.invoiceItems) {
        vat += parseFloat((invoiceItem.vatPrice ? invoiceItem.vatPrice : 0) + "");
      }
    }
    return vat;
  }

  getApplicableVat(): VatBase[] {
    let vatBases: VatBase[] = [];
    if (this.invoice && this.invoice.invoiceItems) {
      for (let invoiceItem of this.invoice.invoiceItems) {
        if (invoiceItem.vat && invoiceItem.vatPrice && invoiceItem.vatPrice > 0) {
          let vatFound = false;
          for (let vatBase of vatBases) {
            if (vatBase.label == invoiceItem.vat.label) {
              vatFound = true;
              vatBase.base += invoiceItem.preTaxPrice - (invoiceItem.discountAmount ? invoiceItem.discountAmount : 0);
              vatBase.total += invoiceItem.vatPrice;
            }
          }
          if (!vatFound) {
            vatBases.push({ label: invoiceItem.vat.label, base: (invoiceItem.preTaxPrice - (invoiceItem.discountAmount ? invoiceItem.discountAmount : 0)), total: invoiceItem.vatPrice });
          }
        }
      }
    }
    return vatBases;
  }

  getPriceTotal(): number {
    if (!this.invoice || this.invoice.invoiceItems && this.invoice.invoiceItems.length > 0)
      return this.getPreTaxPriceTotal() - this.getDiscountTotal() + this.getVatTotal();
    else
      return this.invoice.totalPrice;
  }


  openRoute(event: any, link: string) {
    this.appService.openRoute(event, link, null);
  }

  editInvoice(event: any) {
    if (this.invoice)
      this.appService.openRoute(event, "/invoicing/add/" + this.invoice.id, undefined);
  }

  cancelInvoice(event: any) {
    if (this.invoice) {
      this.invoiceService.cancelInvoice(this.invoice).subscribe(response => this.appService.openRoute(event, "/invoicing/view/" + response.id, undefined));
    }
  }

  addCreditNote(event: any) {
    if (this.invoice) {
      this.appService.openRoute(null, 'invoicing/credit-note/' + this.invoice.id, undefined);
    }
  }

  canCancelInvoice() {
    return this.habilitationService.canCancelInvoice();
  }

  canAddCreditNote() {
    if (this.invoice && !this.invoice.isCreditNote) {
      return this.habilitationService.canAddNewInvoice();
    }
    return false;
  }

  sendMailReminder() {
    if (this.invoice && this.invoice.customerOrder)
      this.quotationService.sendCustomerOrderFinalisationToCustomer(this.invoice.customerOrder).subscribe();
  }

}
