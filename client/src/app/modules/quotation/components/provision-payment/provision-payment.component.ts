import { Component, Input, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { formatDateTimeForSortTable, formatEurosForSortTable } from 'src/app/libs/FormatHelper';
import { AssociatePaymentDialogComponent } from 'src/app/modules/invoicing/components/associate-payment-dialog/associate-payment-dialog.component';
import { AzureInvoice } from 'src/app/modules/invoicing/model/AzureInvoice';
import { Payment } from 'src/app/modules/invoicing/model/Payment';
import { PaymentDetailsDialogService } from 'src/app/modules/invoicing/services/payment.details.dialog.service';
import { PaymentService } from 'src/app/modules/invoicing/services/payment.service';
import { Attachment } from 'src/app/modules/miscellaneous/model/Attachment';
import { SortTableAction } from 'src/app/modules/miscellaneous/model/SortTableAction';
import { SortTableColumn } from 'src/app/modules/miscellaneous/model/SortTableColumn';
import { ConstantService } from 'src/app/modules/miscellaneous/services/constant.service';
import { AppService } from 'src/app/services/app.service';
import { HabilitationsService } from '../../../../services/habilitations.service';
import { CustomerOrder } from '../../model/CustomerOrder';
import { Invoice } from '../../model/Invoice';
import { Provision } from '../../model/Provision';

@Component({
  selector: 'provision-payment',
  templateUrl: './provision-payment.component.html',
  styleUrls: ['./provision-payment.component.css']
})
export class ProvisionPaymentComponent implements OnInit {

  @Input() provision: Provision | undefined;
  @Input() editMode: boolean = false;
  @Input() quotation: CustomerOrder | undefined;

  invoiceDisplayedColumns: SortTableColumn<Invoice>[] = [];
  invoiceTableActions: SortTableAction<Invoice>[] = [];

  paymentsDisplayedColumns: SortTableColumn<Payment>[] = [];
  paymentsTableActions: SortTableAction<Payment>[] = [];

  azureInvoiceSelected: AzureInvoice | undefined;

  newPayment: Payment = {} as Payment;

  paymentTypeCheck = this.constantService.getPaymentTypeCheques();
  paymentTypeCash = this.constantService.getPaymentTypeEspeces();

  constructor(
    private formBuilder: FormBuilder,
    private appService: AppService,
    private habilitationsService: HabilitationsService,
    public associatePaymentDialog: MatDialog,
    private paymentService: PaymentService,
    private paymentDetailsDialogService: PaymentDetailsDialogService,
    private constantService: ConstantService,
  ) { }

  provisionPaymentForm = this.formBuilder.group({});

  ngOnInit() {
    this.invoiceDisplayedColumns = [];
    this.invoiceDisplayedColumns.push({ id: "id", fieldName: "id", label: "N°" } as SortTableColumn<Invoice>);
    this.invoiceDisplayedColumns.push({ id: "manualAccountingDocumentNumber", fieldName: "manualAccountingDocumentNumber", label: "N°" } as SortTableColumn<Invoice>);
    this.invoiceDisplayedColumns.push({ id: "invoiceDate", fieldName: "createdDate", label: "Date", valueFonction: formatDateTimeForSortTable } as SortTableColumn<Invoice>);
    this.invoiceDisplayedColumns.push({ id: "invoiceAmount", fieldName: "totalPrice", label: "Montant", valueFonction: formatEurosForSortTable } as SortTableColumn<Invoice>);
    this.invoiceDisplayedColumns.push({ id: "invoiceStatus", fieldName: "invoiceStatus.label", label: "Statut", statusFonction: (element: Invoice) => { return element.invoiceStatus.code }, displayAsStatus: true } as SortTableColumn<Invoice>);
    this.invoiceDisplayedColumns.push({ id: "confrere", fieldName: "confrere.label", label: "Confrere" } as SortTableColumn<Invoice>);
    this.invoiceDisplayedColumns.push({ id: "competentAuthority", fieldName: "competentAuthority.label", label: "Autorité compétente" } as SortTableColumn<Invoice>);
    this.invoiceDisplayedColumns.push({ id: "provider", fieldName: "provider.label", label: "Fournisseur" } as SortTableColumn<Invoice>);

    this.invoiceTableActions.push({
      actionIcon: "point_of_sale", actionName: "Voir le détail de la facture / associer", actionLinkFunction: (column: SortTableAction<Invoice>, element: Invoice) => {
        if (element)
          return ['/invoicing/view', element.id];
        return undefined;
      }, display: true,
    } as SortTableAction<Invoice>);

    this.paymentsDisplayedColumns = [];
    this.paymentsDisplayedColumns.push({ id: "id", fieldName: "id", label: "N°" } as SortTableColumn<Payment>);
    this.paymentsDisplayedColumns.push({ id: "paymentDate", fieldName: "paymentDate", label: "Date", valueFonction: formatDateTimeForSortTable } as SortTableColumn<Payment>);
    this.paymentsDisplayedColumns.push({ id: "paymentAmount", fieldName: "paymentAmount", label: "Montant", valueFonction: formatEurosForSortTable } as SortTableColumn<Payment>);
    this.paymentsDisplayedColumns.push({ id: "paymentTypeLabel", fieldName: "paymentType.label", label: "Type" } as SortTableColumn<Payment>);
    this.paymentsDisplayedColumns.push({ id: "label", fieldName: "label", label: "Libellé" } as SortTableColumn<Payment>);
    this.paymentsDisplayedColumns.push({ id: "checkNumber", fieldName: "checkNumber", label: "Numéro de chèque" } as SortTableColumn<Payment>);
    if (this.habilitationsService.isAdministrator())
      this.paymentsDisplayedColumns.push({ id: "isCancelled", fieldName: "isCancelled", label: "Est annulé ?" } as SortTableColumn<Payment>);
    this.paymentsDisplayedColumns.push({ id: "invoice", fieldName: "invoice.manualAccountingDocumentNumber", label: "Facture associée" } as SortTableColumn<Payment>);
    this.paymentsTableActions.push({
      actionIcon: "merge_type", actionName: "Associer le paiement", actionClick: (column: SortTableAction<Payment>, element: Payment, event: any) => {
        if ((!element.invoice && !element.isCancelled))
          this.openAssociationDialog(element);
      }, display: true,
    } as SortTableAction<Payment>);
    this.paymentsTableActions.push({
      actionIcon: "visibility", actionName: "Voir le détail du paiement", actionClick: (column: SortTableAction<Payment>, element: Payment, event: any) => {
        this.paymentDetailsDialogService.displayPaymentDetailsDialog(element)
      }, display: true,
    } as SortTableAction<Payment>);
  }

  openAssociationDialog(elementIn: Payment) {
    if (this.quotation)
      this.paymentService.getPaymentById(elementIn.id).subscribe(element => {
        let dialogPaymentDialogRef = this.associatePaymentDialog.open(AssociatePaymentDialogComponent, {
          width: '100%'
        });
        dialogPaymentDialogRef.componentInstance.payment = element;

        // Try to match with current invoices
        let nbr = 0;
        let invoiceFound = undefined;
        if (this.provision && this.provision.providerInvoices)
          for (let invoice of this.provision.providerInvoices)
            if (invoice.invoiceStatus.id == this.constantService.getInvoiceStatusReceived().id)
              if (Math.round(invoice.totalPrice) == Math.round(Math.abs(elementIn.paymentAmount))) {
                nbr++;
                invoiceFound = invoice;
              }

        if (nbr == 1) {
          dialogPaymentDialogRef.componentInstance.invoice = invoiceFound;
        } else {
          dialogPaymentDialogRef.componentInstance.doNotInitializeAsso = true;
        }

        dialogPaymentDialogRef.afterClosed().subscribe(response => {
          this.appService.openRoute(null, '/order/' + this.quotation!.id, null);
        });
      })
  }

  getAvailableAzureInvoices(): Attachment[] {
    let attachments = [];
    if (this.provision && this.provision.attachments)
      for (let attachment of this.provision.attachments)
        if (attachment.azureInvoice && (!attachment.azureInvoice.invoices || attachment.azureInvoice.invoices.length == 0))
          attachments.push(attachment)
    return attachments;
  }

  createInvoiceFromAzureInvoice(azureInvoice: AzureInvoice, event: any) {
    if (this.provision)
      this.appService.openRoute(event, 'invoicing/azure/add/' + azureInvoice.id + "/" + this.provision.id, null);
  }

  canAddNewInvoice() {
    return this.habilitationsService.canAddNewInvoice();
  }

  canAddNewAzureInvoice() {
    return this.habilitationsService.canAddNewAzureInvoice();
  }

  addNewPayment() {
    if (this.newPayment && this.provisionPaymentForm.valid && this.provision && this.quotation) {
      this.newPayment.paymentDate = new Date(this.newPayment.paymentDate.setHours(12));
      this.paymentService.addProvisionPayment(this.newPayment, this.provision).subscribe(payment => {
        this.appService.openRoute(null, '/order/' + this.quotation!.id, null);
      })
    }
  }

  createNewInvoice(event: any) {
    if (this.provision)
      this.appService.openRoute(event, '/invoicing/add/null/' + this.provision.id + '/' + this.quotation!.id, null);
  }
}
