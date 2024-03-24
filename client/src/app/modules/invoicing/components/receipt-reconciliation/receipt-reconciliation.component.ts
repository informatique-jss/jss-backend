import { Component, Input, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { formatDateTimeForSortTable } from 'src/app/libs/FormatHelper';
import { Attachment } from 'src/app/modules/miscellaneous/model/Attachment';
import { IAttachment } from 'src/app/modules/miscellaneous/model/IAttachment';
import { SortTableAction } from 'src/app/modules/miscellaneous/model/SortTableAction';
import { SortTableColumn } from 'src/app/modules/miscellaneous/model/SortTableColumn';
import { ConstantService } from 'src/app/modules/miscellaneous/services/constant.service';
import { UploadAttachmentService } from 'src/app/modules/miscellaneous/services/upload.attachment.service';
import { Affaire } from 'src/app/modules/quotation/model/Affaire';
import { BankTransfert } from 'src/app/modules/quotation/model/BankTransfert';
import { CustomerOrder } from 'src/app/modules/quotation/model/CustomerOrder';
import { Invoice } from 'src/app/modules/quotation/model/Invoice';
import { Provision } from 'src/app/modules/quotation/model/Provision';
import { AppService } from 'src/app/services/app.service';
import { BankTransfertService } from '../../../quotation/services/bank.transfert.service';
import { AzureReceipt } from '../../model/AzureReceipt';
import { AzureReceiptInvoice } from '../../model/AzureReceiptInvoice';
import { AzureReceiptInvoiceService } from '../../services/azure.receipt.invoice.service';
import { AzureReceiptInvoiceStatusService } from '../../services/azure.receipt.invoice.status.service';
import { AzureReceiptService } from '../../services/azure.receipt.service';
import { getAffaireListForProviderInvoice } from '../invoice-tools';
import { ReceiptReconciliationEditDialogComponent } from '../receipt-reconciliation-edit-dialog/receipt-reconciliation-edit-dialog.component';

@Component({
  selector: 'receipt-reconciliation',
  templateUrl: './receipt-reconciliation.component.html',
  styleUrls: ['./receipt-reconciliation.component.css']
})
export class ReceiptReconciliationComponent implements OnInit {

  displayedColumns: SortTableColumn<Attachment>[] = [];
  selectedAttachmentId: number | undefined;
  selectedAttachment: Attachment | undefined;
  selectedAzureReceipt: AzureReceipt | undefined;
  attachments: Attachment[] | undefined;
  tableActions: SortTableAction<Attachment>[] = [] as Array<SortTableAction<Attachment>>;
  @Input() provider: IAttachment | undefined;

  getAffaireList = getAffaireListForProviderInvoice;
  paymentTypeVirement = this.constantService.getPaymentTypeVirement();
  paymentTypeEspece = this.constantService.getPaymentTypeEspeces();
  paymentTypeAccount = this.constantService.getPaymentTypeAccount();

  constructor(
    private constantService: ConstantService,
    private azureReceiptInvoiceService: AzureReceiptInvoiceService,
    private uploadAttachmentService: UploadAttachmentService,
    private azureReceiptInvoiceStatusService: AzureReceiptInvoiceStatusService,
    private appService: AppService,
    private bankTransfertService: BankTransfertService,
    private azureReceiptService: AzureReceiptService,
    public receiptReconciliationEditDialogComponent: MatDialog,
  ) { }

  ngOnInit() {
    this.displayedColumns = [];
    this.displayedColumns.push({ id: "name", fieldName: "uploadedFile.filename", label: "Nom", isShrinkColumn: true } as SortTableColumn<Attachment>);
    this.displayedColumns.push({ id: "createdBy", fieldName: "uploadedFile.createdBy", label: "Ajouté par" } as SortTableColumn<Attachment>);
    this.displayedColumns.push({ id: "creationDate", fieldName: "uploadedFile.creationDate", label: "Ajouté le", valueFonction: formatDateTimeForSortTable } as SortTableColumn<Attachment>);
    this.displayedColumns.push({ id: "globalDocumentConfidence", fieldName: "globalDocumentConfidence", label: "Confiance de la reconnaissance ?", valueFonction: (element: Attachment, column: SortTableColumn<Attachment>) => { return element.azureReceipt.globalDocumentConfidence * 100 + " %" } } as SortTableColumn<Attachment>);
    this.displayedColumns.push({ id: "isReconciliated", fieldName: "isReconciliated", label: "Pointé en totalité ?", valueFonction: (element: Attachment, column: SortTableColumn<Attachment>) => { return element.azureReceipt.isReconciliated ? "✔️" : "❌" } } as SortTableColumn<Attachment>);

    this.tableActions.push({
      actionIcon: "visibility", actionName: "Prévisualiser le relevé", actionClick: (column: SortTableAction<Attachment>, element: Attachment, event: any): void => {
        this.uploadAttachmentService.previewAttachment(element);
      }, display: true
    } as SortTableAction<Attachment>);
    this.tableActions.push({
      actionIcon: "download", actionName: "Télécharger le relevé", actionClick: (column: SortTableAction<Attachment>, element: Attachment, event: any): void => {
        this.uploadAttachmentService.downloadAttachment(element);
      }, display: true
    } as SortTableAction<Attachment>);

    if (this.provider && this.provider.attachments)
      this.attachments = this.provider.attachments.filter(attachment => attachment.azureReceipt && attachment.attachmentType.id == this.constantService.getAttachmentTypeBillingClosure().id).sort(function (a: Attachment, b: Attachment) {
        return new Date(b.uploadedFile.creationDate).getTime() - new Date(a.uploadedFile.creationDate).getTime();
      });
  }

  selectReceipt(attachement: Attachment) {
    this.selectedAttachmentId = attachement.id;
    this.selectedAttachment = attachement;
    this.azureReceiptService.getAzureReceipt(attachement.azureReceipt.id).subscribe(response => {
      this.selectedAzureReceipt = response;
      this.sortReceipt();
      if (this.selectedAzureReceipt.azureReceiptInvoices)
        this.selectedAzureReceipt.azureReceiptInvoices.forEach(invoice => {
          this.azureReceiptInvoiceStatusService.getInvoiceStatus(invoice).subscribe(response => {
            invoice.azureReceiptInvoiceStatus = response;
          })
        });
    })
  }

  sortReceipt() {
    if (this.selectedAzureReceipt && this.selectedAzureReceipt.azureReceiptInvoices)
      this.selectedAzureReceipt.azureReceiptInvoices.sort((a, b) => {
        if (b.isReconciliated && !a.isReconciliated)
          return -1;
        if (!b.isReconciliated && a.isReconciliated)
          return 1;
        return 0;
      });
  }

  markAsNotReconciliated(azureReceiptInvoice: AzureReceiptInvoice) {
    this.azureReceiptInvoiceService.markAsReconciliated(azureReceiptInvoice, false).subscribe(response => {
      for (let invoice of this.selectedAzureReceipt!.azureReceiptInvoices)
        if (invoice.id == response.id)
          invoice.isReconciliated = false;
      this.checkAllInvocesReconciliated();
    });
  }

  markAsReconciliated(azureReceiptInvoice: AzureReceiptInvoice) {
    this.azureReceiptInvoiceService.markAsReconciliated(azureReceiptInvoice, true).subscribe(response => {
      for (let invoice of this.selectedAzureReceipt!.azureReceiptInvoices)
        if (invoice.id == response.id)
          invoice.isReconciliated = true;
      this.checkAllInvocesReconciliated();
    });
  }

  checkAllInvocesReconciliated() {
    if (this.selectedAzureReceipt) {
      this.sortReceipt();
      this.selectedAzureReceipt.isReconciliated = false;
      for (let invoice of this.selectedAzureReceipt.azureReceiptInvoices)
        if (!invoice.isReconciliated)
          return;
      this.selectedAzureReceipt.isReconciliated = true;
    }
  }

  openInvoice(event: any, invoice: Invoice) {
    this.appService.openRoute(event, "/invoicing/view/" + invoice.id, undefined);
  }

  openCustomerOrder(event: any, customerOrder: CustomerOrder) {
    this.appService.openRoute(event, "/order/" + customerOrder.id, undefined);
  }

  openProvision(event: any, provision: Provision) {
    this.appService.openRoute(event, "/provision/" + provision.service.assoAffaireOrder.id + "/" + provision.id, undefined);
  }

  getAffaireLabel(affaire: Affaire): string {
    return (affaire.denomination ? affaire.denomination : (affaire.firstname + ' ' + affaire.lastname)) +
      (affaire.city ? " (" + affaire.city.label + ", " + affaire.siret + ")" : "");
  }

  markBankTransfertForExport(bankTransfert: BankTransfert, azureReceiptInvoice: AzureReceiptInvoice) {
    this.bankTransfertService.selectBankTransfertForExport(bankTransfert).subscribe(response => {
      this.azureReceiptInvoiceStatusService.getInvoiceStatus(azureReceiptInvoice).subscribe(response => {
        for (let invoice of this.selectedAzureReceipt!.azureReceiptInvoices)
          if (invoice.id == azureReceiptInvoice.id)
            invoice.azureReceiptInvoiceStatus = response;
      })
    });
  }

  unmarkBankTransfertForExport(bankTransfert: BankTransfert, azureReceiptInvoice: AzureReceiptInvoice) {
    this.bankTransfertService.unselectBankTransfertForExport(bankTransfert).subscribe(response => {
      this.azureReceiptInvoiceStatusService.getInvoiceStatus(azureReceiptInvoice).subscribe(response => {
        for (let invoice of this.selectedAzureReceipt!.azureReceiptInvoices)
          if (invoice.id == azureReceiptInvoice.id)
            invoice.azureReceiptInvoiceStatus = response;
      })
    });
  }

  modifyAzureReceiptInvoice(azureReceiptInvoice: AzureReceiptInvoice) {
    let dialogRef = this.receiptReconciliationEditDialogComponent.open(ReceiptReconciliationEditDialogComponent, {
      width: '40%',
    });
    dialogRef.componentInstance.azureReceiptInvoice = azureReceiptInvoice;
    dialogRef.afterClosed().subscribe(response => {
      if (response && this.selectedAttachment)
        this.selectReceipt(this.selectedAttachment);
    })
  }
}
