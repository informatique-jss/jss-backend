import { Component, EventEmitter, Input, OnInit, Output, SimpleChanges } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { Subscription } from 'rxjs';
import { CUSTOMER_ORDER_STATUS_BILLED } from 'src/app/libs/Constants';
import { formatDate, formatDateForSortTable, formatDateTimeForSortTable, formatEurosForSortTable } from 'src/app/libs/FormatHelper';
import { instanceOfCustomerOrder, instanceOfQuotation } from 'src/app/libs/TypeHelper';
import { AssociatePaymentDialogComponent } from 'src/app/modules/invoicing/components/associate-payment-dialog/associate-payment-dialog.component';
import { getAffaireListArrayForIQuotation, getAffaireListFromIQuotation, getCustomerOrderNameForIQuotation, getLetteringDate } from 'src/app/modules/invoicing/components/invoice-tools';
import { InvoicingBlockage } from 'src/app/modules/invoicing/model/InvoicingBlockage';
import { Payment } from 'src/app/modules/invoicing/model/Payment';
import { InvoiceSearchResultService } from 'src/app/modules/invoicing/services/invoice.search.result.service';
import { PaymentDetailsDialogService } from 'src/app/modules/invoicing/services/payment.details.dialog.service';
import { PaymentService } from 'src/app/modules/invoicing/services/payment.service';
import { SortTableAction } from 'src/app/modules/miscellaneous/model/SortTableAction';
import { SortTableColumn } from 'src/app/modules/miscellaneous/model/SortTableColumn';
import { ConstantService } from 'src/app/modules/miscellaneous/services/constant.service';
import { Employee } from 'src/app/modules/profile/model/Employee';
import { CUSTOMER_ORDER_STATUS_TO_BILLED } from '../../../../libs/Constants';
import { AppService } from '../../../../services/app.service';
import { HabilitationsService } from '../../../../services/habilitations.service';
import { InvoiceSearchResult } from '../../../invoicing/model/InvoiceSearchResult';
import { CustomerOrder } from '../../model/CustomerOrder';
import { IQuotation } from '../../model/IQuotation';
import { InvoiceItem } from '../../model/InvoiceItem';
import { InvoiceLabelResult } from '../../model/InvoiceLabelResult';
import { VatBase } from '../../model/VatBase';
import { CustomerOrderService } from '../../services/customer.order.service';
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
  customerInvoicesColumns: SortTableColumn<InvoiceSearchResult>[] = [];
  providerInvoicesColumns: SortTableColumn<InvoiceSearchResult>[] = [];
  invoicesActions: SortTableAction<InvoiceSearchResult>[] = [];
  invoiceLabelResult: InvoiceLabelResult | undefined;

  instanceOfCustomerOrderFn = instanceOfCustomerOrder;
  instanceOfQuotation = instanceOfQuotation;
  getLetteringDate = getLetteringDate;
  getAffaireListFromIQuotation = getAffaireListFromIQuotation;
  getCustomerOrderNameForIQuotation = getCustomerOrderNameForIQuotation;
  getAffaireListArrayForIQuotation = getAffaireListArrayForIQuotation;
  formatDate = formatDate;

  CUSTOMER_ORDER_STATUS_TO_BILLED = CUSTOMER_ORDER_STATUS_TO_BILLED;
  CUSTOMER_ORDER_STATUS_BILLED = CUSTOMER_ORDER_STATUS_BILLED;

  invoiceStatusCancelled = this.constantService.getInvoiceStatusCancelled();

  constructor(private formBuilder: FormBuilder,
    private constantService: ConstantService,
    private appService: AppService,
    private invoiceLabelResultService: InvoiceLabelResultService,
    public associatePaymentDialog: MatDialog,
    private paymentDetailsDialogService: PaymentDetailsDialogService,
    protected invoiceSearchResultService: InvoiceSearchResultService,
    private habilitationsService: HabilitationsService,
    private paymentService: PaymentService,
    private customerOrderService: CustomerOrderService
  ) { }

  ngOnInit() {
    this.updateInvoiceLabelResult();
    this.invoiceManagementForm.markAllAsTouched();

    if (instanceOfCustomerOrder(this.quotation))
      this.invoiceSearchResultService.getProviderInvoiceForCustomerOrder(this.quotation).subscribe(invoices => this.customerOrderProviderInvoices = invoices);

    this.customerInvoicesColumns = [];
    this.customerInvoicesColumns.push({ id: "id", fieldName: "invoiceId", label: "N° de facture" } as SortTableColumn<InvoiceSearchResult>);
    this.customerInvoicesColumns.push({ id: "status", fieldName: "invoiceStatus", label: "Status", statusFonction: (element: InvoiceSearchResult) => { return element.invoiceStatusCode }, displayAsStatus: true } as SortTableColumn<InvoiceSearchResult>);
    this.customerInvoicesColumns.push({ id: "createdDate", fieldName: "createdDate", label: "Date d'émission", valueFonction: formatDateTimeForSortTable } as SortTableColumn<InvoiceSearchResult>);
    this.customerInvoicesColumns.push({ id: "totalPrice", fieldName: "totalPrice", label: "Montant TTC", valueFonction: formatEurosForSortTable } as SortTableColumn<InvoiceSearchResult>);
    this.customerInvoicesColumns.push({ id: "payments", fieldName: "paymentId", label: "Paiement(s) associé(s)" } as SortTableColumn<InvoiceSearchResult>);
    this.customerInvoicesColumns.push({ id: "dueDate", fieldName: "dueDate", label: "Date d'échéance", valueFonction: formatDateForSortTable } as SortTableColumn<InvoiceSearchResult>);
    this.customerInvoicesColumns.push({ id: "firstReminderDateTime", fieldName: "firstReminderDateTime", label: "Date de première relance", valueFonction: formatDateForSortTable } as SortTableColumn<InvoiceSearchResult>);
    this.customerInvoicesColumns.push({ id: "secondReminderDateTime", fieldName: "secondReminderDateTime", label: "Date de seconde relance", valueFonction: formatDateForSortTable } as SortTableColumn<InvoiceSearchResult>);
    this.customerInvoicesColumns.push({ id: "thirdReminderDateTime", fieldName: "thirdReminderDateTime", label: "Date de troisième relance", valueFonction: formatDateForSortTable } as SortTableColumn<InvoiceSearchResult>);
    this.customerInvoicesColumns.push({ id: "lastFollowupDate", fieldName: "lastFollowupDate", label: "Dernier suivi", valueFonction: formatDateForSortTable } as SortTableColumn<InvoiceSearchResult>);


    this.providerInvoicesColumns = [];
    this.providerInvoicesColumns.push({ id: "id", fieldName: "invoiceId", label: "N° de facture" } as SortTableColumn<InvoiceSearchResult>);
    this.providerInvoicesColumns.push({ id: "status", fieldName: "invoiceStatus", label: "Status", statusFonction: (element: InvoiceSearchResult) => { return element.invoiceStatusCode }, displayAsStatus: true } as SortTableColumn<InvoiceSearchResult>);
    this.providerInvoicesColumns.push({ id: "providerLabel", fieldName: "providerLabel", label: "Fournisseur" } as SortTableColumn<InvoiceSearchResult>);
    this.providerInvoicesColumns.push({ id: "createdDate", fieldName: "createdDate", label: "Date d'émission", valueFonction: formatDateTimeForSortTable } as SortTableColumn<InvoiceSearchResult>);
    this.providerInvoicesColumns.push({ id: "totalPrice", fieldName: "totalPrice", label: "Montant TTC", valueFonction: formatEurosForSortTable } as SortTableColumn<InvoiceSearchResult>);
    this.providerInvoicesColumns.push({ id: "manualAccountingDocumentNumber", fieldName: "manualAccountingDocumentNumber", label: "N° pièce" } as SortTableColumn<InvoiceSearchResult>);
    this.providerInvoicesColumns.push({ id: "payments", fieldName: "paymentId", label: "Paiement(s) associé(s)" } as SortTableColumn<InvoiceSearchResult>);
    this.providerInvoicesColumns.push({ id: "dueDate", fieldName: "dueDate", label: "Date d'échéance", valueFonction: formatDateForSortTable } as SortTableColumn<InvoiceSearchResult>);


    this.invoicesActions.push({
      actionIcon: "point_of_sale", actionName: "Voir le détail de la facture / associer", actionLinkFunction: (action: SortTableAction<InvoiceSearchResult>, element: InvoiceSearchResult) => {
        if (element)
          return ['/invoicing/view', element.invoiceId];
        return undefined;
      }, display: true,
    } as SortTableAction<InvoiceSearchResult>);
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

  canMovePaymentToWaitingAccount() {
    return this.habilitationsService.canMovePaymentToWaitingAccount();
  }

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
    return QuotationComponent.computeApplicableVat(this.quotation);
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
    if (this.quotation && this.quotation.id && instanceOfCustomerOrder(this.quotation) && (this.quotation.responsable)) {
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

    dialogPaymentDialogRef.afterClosed().subscribe(response => {
      this.appService.openRoute(null, '/order/' + this.quotation.id, null);
    });
  }

  openPaymentDialog(payment: Payment) {
    this.paymentDetailsDialogService.displayPaymentDetailsDialog(payment);
  }

  movePaymentToWaitingAccount(payment: Payment) {
    this.paymentService.movePaymentToWaitingAccount(payment).subscribe((res) => {
      this.appService.openRoute(null, '/order/' + this.quotation.id, null);
    });
  }

  assignInvoicingEmployee(employee: Employee) {
    this.customerOrderService.assignInvoicingEmployee(this.quotation.id, employee).subscribe();
  }

  modifyInvoicingBlockage(invoicingBlockage: InvoicingBlockage) {
    this.customerOrderService.modifyInvoicingBlockage(this.quotation.id, invoicingBlockage).subscribe();
  }
}
