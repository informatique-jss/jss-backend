import { Component, EventEmitter, Input, OnInit, Output, SimpleChanges } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { Subscription } from 'rxjs';
import { CUSTOMER_ORDER_STATUS_BILLED } from 'src/app/libs/Constants';
import { instanceOfCustomerOrder, instanceOfQuotation } from 'src/app/libs/TypeHelper';
import { AssociatePaymentDialogComponent } from 'src/app/modules/invoicing/components/associate-payment-dialog/associate-payment-dialog.component';
import { getAffaireListArrayForIQuotation, getAffaireListFromIQuotation, getCustomerOrderForIQuotation, getCustomerOrderNameForIQuotation, getLetteringDate } from 'src/app/modules/invoicing/components/invoice-tools';
import { Payment } from 'src/app/modules/invoicing/model/Payment';
import { InvoiceSearchResultService } from 'src/app/modules/invoicing/services/invoice.search.result.service';
import { PaymentDetailsDialogService } from 'src/app/modules/invoicing/services/payment.details.dialog.service';
import { ConstantService } from 'src/app/modules/miscellaneous/services/constant.service';
import { AppService } from '../../../../services/app.service';
import { InvoiceSearchResult } from '../../../invoicing/model/InvoiceSearchResult';
import { CustomerOrder } from '../../model/CustomerOrder';
import { IQuotation } from '../../model/IQuotation';
import { InvoiceItem } from '../../model/InvoiceItem';
import { InvoiceLabelResult } from '../../model/InvoiceLabelResult';
import { VatBase } from '../../model/VatBase';
import { InvoiceLabelResultService } from '../../services/invoice.label.result.service';
import { QuotationComponent } from '../quotation/quotation.component';

@Component({
  selector: 'invoice-management',
  templateUrl: './invoice-management.component.html',
  styleUrls: ['./invoice-management.component.css']
})
export class InvoiceManagementComponent implements OnInit {

  @Input() quotation: IQuotation = {} as IQuotation;
  customerOrder: CustomerOrder | undefined;
  @Input() customerOrderInvoices: InvoiceSearchResult[] | undefined;
  customerOrderProviderInvoices: InvoiceSearchResult[] | undefined;
  @Input() editMode: boolean = false;
  @Input() instanceOfCustomerOrder: boolean = false;
  @Output() invoiceItemChange: EventEmitter<void> = new EventEmitter<void>();
  updateDocumentsSubscription: Subscription | undefined;

  invoiceLabelResult: InvoiceLabelResult | undefined;

  instanceOfCustomerOrderFn = instanceOfCustomerOrder;
  instanceOfQuotation = instanceOfQuotation;
  getLetteringDate = getLetteringDate;
  getAffaireListFromIQuotation = getAffaireListFromIQuotation;
  getCustomerOrderNameForIQuotation = getCustomerOrderNameForIQuotation;
  getCustomerOrderForIQuotation = getCustomerOrderForIQuotation;
  getAffaireListArrayForIQuotation = getAffaireListArrayForIQuotation;

  invoiceStatusCancelled = this.constantService.getInvoiceStatusCancelled();

  constructor(private formBuilder: FormBuilder,
    private constantService: ConstantService,
    private appService: AppService,
    private invoiceLabelResultService: InvoiceLabelResultService,
    public associatePaymentDialog: MatDialog,
    private paymentDetailsDialogService: PaymentDetailsDialogService,
    protected invoiceSearchResultService: InvoiceSearchResultService,) { }

  ngOnInit() {
    this.updateInvoiceLabelResult();
    this.invoiceManagementForm.markAllAsTouched();

    if (instanceOfCustomerOrder(this.quotation))
      this.invoiceSearchResultService.getProviderInvoiceForCustomerOrder(this.quotation).subscribe(invoices => this.customerOrderProviderInvoices = invoices);
  }

  ngOnDestroy() {
    if (this.updateDocumentsSubscription)
      this.updateDocumentsSubscription.unsubscribe();
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes.quotation != undefined) {
      this.invoiceManagementForm.markAllAsTouched();
    }
  }

  invoiceManagementForm = this.formBuilder.group({
  });

  itemChange(invoiceItem: InvoiceItem) {
    invoiceItem.isOverridePrice = true;
    this.invoiceItemChange.emit();
  }

  toggleIsGifTed(invoiceItem: InvoiceItem) {
    if (!invoiceItem.isGifted)
      invoiceItem.isGifted = true;
    else
      invoiceItem.isGifted = false;
    this.invoiceItemChange.emit();
  }

  getFormStatus(): boolean {
    this.invoiceManagementForm.markAllAsTouched();
    return this.invoiceManagementForm.valid;
  }

  getPreTaxPriceTotal(): number {
    return QuotationComponent.computePreTaxPriceTotal(this.quotation);
  }

  getDiscountTotal(): number {
    return QuotationComponent.computeDiscountTotal(this.quotation);
  }

  getVatTotal(): number {
    return QuotationComponent.computeVatTotal(this.quotation);
  }

  getApplicableVat(): VatBase[] {
    return QuotationComponent.computeApplicableVat(this.quotation, this.constantService.getVatDeductible());
  }

  getPriceTotal(): number {
    return QuotationComponent.computePriceTotal(this.quotation);
  }

  getRemainingToPay() {
    if (instanceOfCustomerOrder(this.quotation))
      if (this.quotation.customerOrderStatus.code != CUSTOMER_ORDER_STATUS_BILLED)
        return Math.round((QuotationComponent.computePriceTotal(this.quotation) - QuotationComponent.computePayed(this.quotation)) * 100) / 100;
      else if (this.getCurrentInvoiceForCustomerOrder() != undefined) {
        return Math.round((this.getCurrentInvoiceForCustomerOrder()!.remainingToPay) * 100) / 100;
      }
    return Math.round((this.getPriceTotal()) * 100) / 100;
  }

  openRoute(event: any, link: string) {
    this.appService.openRoute(event, link, null);
  }

  updateInvoiceLabelResult() {
    if (this.quotation && this.quotation.id && instanceOfCustomerOrder(this.quotation) && (this.quotation.tiers || this.quotation.confrere || this.quotation.responsable)) {
      this.invoiceLabelResultService.getInvoiceLabelComputeResult(this.quotation).subscribe(response => {
        if (response && response.billingLabel)
          this.invoiceLabelResult = response;
      });
    }
  }

  getCurrentInvoiceForCustomerOrder(): InvoiceSearchResult | undefined {
    if (instanceOfCustomerOrder(this.quotation) && this.customerOrderInvoices)
      for (let invoice of this.customerOrderInvoices)
        if (invoice.invoiceStatus && (invoice.invoiceStatusId == this.constantService.getInvoiceStatusSend().id || invoice.invoiceStatusId == this.constantService.getInvoiceStatusPayed().id))
          return invoice;
    return undefined;
  }

  movePayment(payment: Payment) {
    let dialogPaymentDialogRef = this.associatePaymentDialog.open(AssociatePaymentDialogComponent, {
      width: '100%'
    });
    dialogPaymentDialogRef.componentInstance.payment = payment;
    dialogPaymentDialogRef.componentInstance.doNotInitializeAsso = true;
    dialogPaymentDialogRef.componentInstance.customerOrder = this.customerOrder as CustomerOrder;

    dialogPaymentDialogRef.afterClosed().subscribe(response => {
      this.appService.openRoute(null, '/order/' + this.quotation.id, null);
    });
  }

  openPaymentDialog(payment: Payment) {
    this.paymentDetailsDialogService.displayPaymentDetailsDialog(payment);
  }
}
