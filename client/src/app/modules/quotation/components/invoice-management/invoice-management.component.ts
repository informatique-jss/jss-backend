import { Component, EventEmitter, Input, OnInit, Output, SimpleChanges } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Subscription } from 'rxjs';
import { CUSTOMER_ORDER_STATUS_BILLED } from 'src/app/libs/Constants';
import { instanceOfCustomerOrder, instanceOfQuotation } from 'src/app/libs/TypeHelper';
import { getAffaireListArrayForIQuotation, getAffaireListFromIQuotation, getCustomerOrderForIQuotation, getCustomerOrderNameForIQuotation, getLetteringDate, getRemainingToPay } from 'src/app/modules/invoicing/components/invoice-tools';
import { InvoiceService } from 'src/app/modules/invoicing/services/invoice.service';
import { ConstantService } from 'src/app/modules/miscellaneous/services/constant.service';
import { AppService } from '../../../../services/app.service';
import { CustomerOrder } from '../../model/CustomerOrder';
import { Invoice } from '../../model/Invoice';
import { InvoiceItem } from '../../model/InvoiceItem';
import { InvoiceLabelResult } from '../../model/InvoiceLabelResult';
import { IQuotation } from '../../model/IQuotation';
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
    protected invoiceService: InvoiceService,) { }

  ngOnInit() {
    this.updateInvoiceLabelResult();
    this.invoiceManagementForm.markAllAsTouched();
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
        return getRemainingToPay(this.getCurrentInvoiceForCustomerOrder()!);
      }
    return this.getPriceTotal();
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

  getCurrentInvoiceForCustomerOrder(): Invoice | undefined {
    if (instanceOfCustomerOrder(this.quotation) && this.quotation.invoices)
      for (let invoice of this.quotation.invoices)
        if (invoice.invoiceStatus && (invoice.invoiceStatus.id == this.constantService.getInvoiceStatusSend().id || invoice.invoiceStatus.id == this.constantService.getInvoiceStatusPayed().id))
          return invoice;
    return undefined;
  }
}
