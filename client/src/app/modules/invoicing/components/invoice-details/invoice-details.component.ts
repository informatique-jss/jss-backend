import { Component, Input, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { MatTabChangeEvent } from '@angular/material/tabs';
import { ActivatedRoute } from '@angular/router';
import { ConstantService } from 'src/app/modules/miscellaneous/services/constant.service';
import { QuotationComponent } from 'src/app/modules/quotation/components/quotation/quotation.component';
import { Affaire } from 'src/app/modules/quotation/model/Affaire';
import { IQuotation } from 'src/app/modules/quotation/model/IQuotation';
import { Invoice } from 'src/app/modules/quotation/model/Invoice';
import { InvoiceItem } from 'src/app/modules/quotation/model/InvoiceItem';
import { Service } from 'src/app/modules/quotation/model/Service';
import { VatBase } from 'src/app/modules/quotation/model/VatBase';
import { CustomerOrderService } from 'src/app/modules/quotation/services/customer.order.service';
import { QuotationService } from 'src/app/modules/quotation/services/quotation.service';
import { ServiceService } from 'src/app/modules/quotation/services/service.service';
import { Tiers } from 'src/app/modules/tiers/model/Tiers';
import { CUSTOMER_ORDER_ENTITY_TYPE, INVOICE_ENTITY_TYPE } from 'src/app/routing/search/search.component';
import { AppService } from 'src/app/services/app.service';
import { HabilitationsService } from 'src/app/services/habilitations.service';
import { instanceOfResponsable, instanceOfTiers } from '../../../../libs/TypeHelper';
import { UserPreferenceService } from '../../../../services/user.preference.service';
import { InvoiceItemService } from '../../services/invoice.item.service';
import { InvoiceService } from '../../services/invoice.service';
import { EditAmountInvoiceItemDialogComponent } from '../edit-amount-invoice-item-dialog/edit-amount-invoice-item-dialog.component';
import { getAffaireList, getAffaireListArray, getCustomerOrderNameForInvoice, getLetteringDate, getRemainingToPay, getResponsableName } from '../invoice-tools';

@Component({
  selector: 'invoice-details',
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
    private customerOrderService: CustomerOrderService,
    private userPreferenceService: UserPreferenceService,
    private serviceService: ServiceService,
    private invoiceItemService: InvoiceItemService,
    public editAmountInvoiceItemDialogComponent: MatDialog,
  ) { }

  invoiceStatusSend = this.constantService.getInvoiceStatusSend();
  invoiceStatusPayed = this.constantService.getInvoiceStatusPayed();
  invoiceStatusReceived = this.constantService.getInvoiceStatusReceived();
  invoiceStatusCancelled = this.constantService.getInvoiceStatusCancelled();
  invoiceStatusCreditNoteReceived = this.constantService.getInvoiceStatusCreditNoteReceived();
  attachmentTypeInvoice = this.constantService.getAttachmentTypeInvoice();
  attachmentTypeCreditNote = this.constantService.getAttachmentTypeCreditNote();

  CUSTOMER_ORDER_ENTITY_TYPE = CUSTOMER_ORDER_ENTITY_TYPE;

  @Input() idInvoice: number | undefined;
  @Input() isForIntegration: boolean = false;

  ngOnInit() {
    this.refreshData();
  }

  onStateChange() {
    this.refreshData();
  }

  refreshData() {
    let idInvoice: number = this.activatedRoute.snapshot.params.id;
    if (this.idInvoice)
      idInvoice = this.idInvoice;

    if (idInvoice) {
      if (!this.isForIntegration)
        this.appService.changeHeaderTitle("Facture/avoir n°" + idInvoice);
      this.invoiceService.getInvoiceById(idInvoice).subscribe(response => {
        this.invoice = response;
        this.restoreTab();
        if (!this.isForIntegration)
          this.appService.changeHeaderTitle((this.invoice.isCreditNote ? "Avoir" : "Facture") + " n°" + idInvoice + " - " + this.invoice.invoiceStatus.label);
      })
    }
  }

  beforeToday(date: Date): boolean {
    return (new Date(date)).getTime() < (new Date()).getTime();
  }

  invoiceDetailForm = this.formBuilder.group({
  });

  getCustomerOrderName = getCustomerOrderNameForInvoice;

  getCustomerOrder(invoice: Invoice): any {
    if (invoice) {
      if (invoice.provider)
        return invoice.provider
      return invoice.responsable;
    }
    return {} as Tiers;
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
      }

      this.appService.openRoute(event, link.join("/"), null);
    }
  }

  openEditAmountDialog(invoiceItem: InvoiceItem) {
    if (invoiceItem) {
      const dialogRef = this.editAmountInvoiceItemDialogComponent.open(EditAmountInvoiceItemDialogComponent, {
        maxWidth: "400px",
      });
      dialogRef.componentInstance.amount = invoiceItem.preTaxPrice;
      dialogRef.afterClosed().subscribe(dialogResult => {
        if (dialogResult != null && dialogResult != undefined) {
          if (dialogResult < 0)
            this.appService.displaySnackBar("Le montant à refacturer doit être positif", true, 10);
          else
            this.invoiceItemService.updateInvoiceItemFromInvoice(invoiceItem.id, dialogResult).subscribe(respnse => {
              this.refreshData();
              this.appService.openRoute(null, 'invoicing/view/' + this.invoice!.id, undefined);
            });
        }
      });
    }
  }

  getResponsableName = getResponsableName;
  getLetteringDate = getLetteringDate;

  getServiceLabel(service: Service) {
    return this.serviceService.getServiceLabel(service, false, this.constantService.getServiceTypeOther());
  }

  computePreTaxPriceTotal(quotation: IQuotation): number {
    return QuotationComponent.computePreTaxPriceTotal(quotation);
  }
  getFirstItemIndex(invoice: Invoice, affaire: Affaire) {
    if (invoice && affaire) {
      for (let i = 0; i < invoice.invoiceItems.length; i++) {
        const invoiceItem = invoice.invoiceItems[i];
        if (invoiceItem.provision.service.assoAffaireOrder.affaire.id == affaire.id)
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
        if (invoiceItem.vat && invoiceItem.vatPrice) {
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

  generateCreditNoteForCustomerOrderInvoice(event: any) {
    if (this.invoice && this.invoice.customerOrder) {
      this.customerOrderService.generateCreditNoteForCustomerOrderInvoice(this.invoice, this.invoice.customerOrder).subscribe(response => {
        this.appService.openRoute(null, 'invoicing/view/' + this.invoice!.id, undefined);
      })
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

  canEditAmountInvoice() {
    return this.habilitationService.canEditPreTaxPriceReinvoiced();
  }

  canAddCreditNoteForCustomerOrderInvoice() {
    return this.habilitationService.canAddCreditNoteForCustomerOrderInvoice();
  }

  sendMailReminder() {
    if (this.invoice && this.invoice.customerOrder)
      this.quotationService.sendCustomerOrderFinalisationToCustomer(this.invoice.customerOrder).subscribe();
  }

  //Tabs management
  index: number = 0;
  onTabChange(event: MatTabChangeEvent) {
    this.userPreferenceService.setUserTabsSelectionIndex('invoice-details', event.index);
  }

  restoreTab() {
    this.index = this.userPreferenceService.getUserTabsSelectionIndex('invoice-details');
  }
}
