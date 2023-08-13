import { Component, Input, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { formatDateTimeForSortTable, formatEurosForSortTable } from 'src/app/libs/FormatHelper';
import { AssociatePaymentDialogComponent } from 'src/app/modules/invoicing/components/associate-payment-dialog/associate-payment-dialog.component';
import { AzureInvoice } from 'src/app/modules/invoicing/model/AzureInvoice';
import { Payment } from 'src/app/modules/invoicing/model/Payment';
import { PaymentService } from 'src/app/modules/invoicing/services/payment.service';
import { Attachment } from 'src/app/modules/miscellaneous/model/Attachment';
import { SortTableAction } from 'src/app/modules/miscellaneous/model/SortTableAction';
import { SortTableColumn } from 'src/app/modules/miscellaneous/model/SortTableColumn';
import { ConstantService } from 'src/app/modules/miscellaneous/services/constant.service';
import { AppService } from 'src/app/services/app.service';
import { HabilitationsService } from '../../../../services/habilitations.service';
import { CustomerOrder } from '../../model/CustomerOrder';
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

  invoiceDisplayedColumns: SortTableColumn[] = [];
  invoiceTableActions: SortTableAction[] = [];

  paymentsDisplayedColumns: SortTableColumn[] = [];
  paymentsTableActions: SortTableAction[] = [];

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
    private constantService: ConstantService,
  ) { }

  provisionPaymentForm = this.formBuilder.group({});

  ngOnInit() {
    this.invoiceDisplayedColumns = [];
    this.invoiceDisplayedColumns.push({ id: "id", fieldName: "id", label: "N°" } as SortTableColumn);
    this.invoiceDisplayedColumns.push({ id: "manualAccountingDocumentNumber", fieldName: "manualAccountingDocumentNumber", label: "N°" } as SortTableColumn);
    this.invoiceDisplayedColumns.push({ id: "invoiceDate", fieldName: "createdDate", label: "Date", valueFonction: formatDateTimeForSortTable } as SortTableColumn);
    this.invoiceDisplayedColumns.push({ id: "invoiceAmount", fieldName: "totalPrice", label: "Montant", valueFonction: formatEurosForSortTable } as SortTableColumn);
    this.invoiceDisplayedColumns.push({ id: "invoiceStatus", fieldName: "invoiceStatus.label", label: "Statut", valueFonction: formatEurosForSortTable } as SortTableColumn);
    this.invoiceDisplayedColumns.push({ id: "confrere", fieldName: "confrere.label", label: "Confrere" } as SortTableColumn);
    this.invoiceDisplayedColumns.push({ id: "competentAuthority", fieldName: "competentAuthority.label", label: "Autorité compétente" } as SortTableColumn);
    this.invoiceDisplayedColumns.push({ id: "provider", fieldName: "provider.label", label: "Fournisseur" } as SortTableColumn);

    this.invoiceTableActions.push({
      actionIcon: "point_of_sale", actionName: "Voir le détail de la facture / associer", actionLinkFunction: (action: SortTableAction, element: any) => {
        if (element)
          return ['/invoicing/view', element.invoiceId];
        return undefined;
      }, display: true,
    } as SortTableAction);

    this.paymentsDisplayedColumns = [];
    this.paymentsDisplayedColumns.push({ id: "id", fieldName: "id", label: "N°" } as SortTableColumn);
    this.paymentsDisplayedColumns.push({ id: "paymentDate", fieldName: "paymentDate", label: "Date", valueFonction: formatDateTimeForSortTable } as SortTableColumn);
    this.paymentsDisplayedColumns.push({ id: "paymentAmount", fieldName: "paymentAmount", label: "Montant", valueFonction: formatEurosForSortTable } as SortTableColumn);
    this.paymentsDisplayedColumns.push({ id: "paymentTypeLabel", fieldName: "paymentType.label", label: "Type" } as SortTableColumn);
    this.paymentsDisplayedColumns.push({ id: "label", fieldName: "label", label: "Libellé" } as SortTableColumn);
    this.paymentsDisplayedColumns.push({ id: "checkNumber", fieldName: "checkNumber", label: "Numéro de chèque" } as SortTableColumn);
    this.paymentsDisplayedColumns.push({ id: "invoice", fieldName: "invoice.manualAccountingDocumentNumber", label: "Facture associée" } as SortTableColumn);

    this.paymentsTableActions.push({
      actionIcon: "merge_type", actionName: "Associer le paiement", actionClick: (action: SortTableAction, element: any) => {
        if ((!element.invoice && !element.isCancelled))
          this.openAssociationDialog(element);
      }, display: true,
    } as SortTableAction);
  }

  openAssociationDialog(elementIn: Payment) {
    if (this.quotation)
      this.paymentService.getPaymentById(elementIn.id).subscribe(element => {
        let dialogPaymentDialogRef = this.associatePaymentDialog.open(AssociatePaymentDialogComponent, {
          width: '100%'
        });
        dialogPaymentDialogRef.componentInstance.payment = element;
        dialogPaymentDialogRef.afterClosed().subscribe(response => {
          this.appService.openRoute(null, '/order/' + this.quotation!.id, null);
        });
      })
  }

  getAvailableAzureInvoices(): Attachment[] {
    let attachments = [];
    if (this.provision && this.provision.attachments)
      for (let attachment of this.provision.attachments)
        if (attachment.azureInvoice && !attachment.invoice)
          attachments.push(attachment)
    return attachments;
  }

  createInvoiceFromAzureInvoice(azureInvoice: AzureInvoice, event: any) {
    if (this.provision)
      this.appService.openRoute(event, 'invoicing/add/azure/' + azureInvoice.id + "/" + this.provision.id, null);
  }

  canAddNewInvoice() {
    return this.habilitationsService.canAddNewInvoice();
  }

  addNewPayment() {
    if (this.newPayment && this.provisionPaymentForm.valid && this.provision && this.quotation)
      this.paymentService.addProvisionPayment(this.newPayment, this.provision).subscribe(payment => {
        this.appService.openRoute(null, '/order/' + this.quotation!.id, null);
      })
  }
}
