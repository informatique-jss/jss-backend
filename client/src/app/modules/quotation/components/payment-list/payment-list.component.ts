import { AfterContentChecked, ChangeDetectorRef, Component, EventEmitter, Input, OnInit, Output } from "@angular/core";
import { FormBuilder } from "@angular/forms";
import { MatDialog, MatDialogRef } from "@angular/material/dialog";
import { formatDateTimeForSortTable, formatEurosForSortTable, toIsoString } from "src/app/libs/FormatHelper";
import { AssociatePaymentDialogComponent } from "src/app/modules/invoicing/components/associate-payment-dialog/associate-payment-dialog.component";
import { PaymentSearch } from "src/app/modules/invoicing/model/PaymentSearch";
import { PaymentSearchResult } from "src/app/modules/invoicing/model/PaymentSearchResult";
import { PaymentSearchResultService } from "src/app/modules/invoicing/services/payment.search.result.service";
import { PaymentService } from "src/app/modules/invoicing/services/payment.service";
import { EditCommentDialogComponent } from "src/app/modules/miscellaneous/components/edit-comment-dialog.component/edit-comment-dialog-component.component";
import { UploadAttachementDialogComponent } from "src/app/modules/miscellaneous/components/upload-attachement-dialog/upload-attachement-dialog.component";
import { IAttachment } from "src/app/modules/miscellaneous/model/IAttachment";
import { SortTableAction } from "src/app/modules/miscellaneous/model/SortTableAction";
import { SortTableColumn } from "src/app/modules/miscellaneous/model/SortTableColumn";
import { ConstantService } from "src/app/modules/miscellaneous/services/constant.service";
import { OFX_ENTITY_TYPE } from "src/app/routing/search/search.component";
import { AppService } from "src/app/services/app.service";
import { HabilitationsService } from "src/app/services/habilitations.service";
import { AccountingAccount } from '../../../accounting/model/AccountingAccount';
import { PaymentDetailsDialogService } from '../../../invoicing/services/payment.details.dialog.service';
import { RefundPaymentDialogComponent } from "../refund-payment-dialog/refund-payment-dialog.component";
import { SelectAccountingAccountDialogComponent } from "../select-accounting-account-dialog/select-accounting-account-dialog.component";
import { SelectCompetentAuthorityDialogComponent } from "../select-competent-authority-dialog/select-competent-authority-dialog.component";


@Component({
  selector: 'payment-list',
  templateUrl: './payment-list.component.html',
  styleUrls: ['./payment-list.component.css']
})
export class PaymentListComponent implements OnInit, AfterContentChecked {
  @Input() paymentSearch: PaymentSearch = {} as PaymentSearch;
  @Input() isForDashboard: boolean = false;
  payments: PaymentSearchResult[] | undefined;
  availableColumns: SortTableColumn[] = [];
  displayedColumns: SortTableColumn[] = [];
  columnToDisplayOnDashboard: string[] = ["payemntDate", "payemntAmount", "label"];
  tableAction: SortTableAction[] = [];
  uploadAttachementDialogRef: MatDialogRef<UploadAttachementDialogComponent> | undefined;
  selectAccountingAccountDialogComponentRef: MatDialogRef<SelectAccountingAccountDialogComponent> | undefined;

  @Output() actionBypass: EventEmitter<PaymentSearchResult> = new EventEmitter<PaymentSearchResult>();
  @Input() overrideIconAction: string = "";
  @Input() overrideTooltipAction: string = "";
  @Input() defaultStatusFilter: string[] | undefined;

  constructor(
    private paymentSearchResultService: PaymentSearchResultService,
    private paymentService: PaymentService,
    private constantService: ConstantService,
    protected uploadAttachementDialog: MatDialog,
    protected selectAccountingAccountDialogComponent: MatDialog,
    private changeDetectorRef: ChangeDetectorRef,
    private appService: AppService,
    public associatePaymentDialog: MatDialog,
    public refundPaymentDialog: MatDialog,
    public selectCompetentAuthorityDialog: MatDialog,
    private formBuilder: FormBuilder,
    private habilitationService: HabilitationsService,
    private editCommentDialog: MatDialog,
    private paymentDetailsDialogService: PaymentDetailsDialogService,
  ) { }

  ngAfterContentChecked(): void {
    this.changeDetectorRef.detectChanges();
  }

  canImportOfxFile() {
    return this.habilitationService.canImportOfxFile();
  }

  canAddCheckPayment() {
    return this.habilitationService.canAddCheckPayment();
  }

  openRoute(event: any, link: string) {
    this.appService.openRoute(event, link, null);
  }

  ngOnInit() {
    this.availableColumns = [];
    this.availableColumns.push({ id: "id", fieldName: "id", label: "N° du paiement" } as SortTableColumn);
    this.availableColumns.push({ id: "originPaymentId", fieldName: "originPaymentId", label: "Paiement d'origine" } as SortTableColumn);
    this.availableColumns.push({ id: "payemntDate", fieldName: "paymentDate", label: "Date", valueFonction: formatDateTimeForSortTable } as SortTableColumn);
    this.availableColumns.push({ id: "payemntAmount", fieldName: "paymentAmount", label: "Montant", valueFonction: formatEurosForSortTable } as SortTableColumn);
    this.availableColumns.push({ id: "paymentTypeLabel", fieldName: "paymentTypeLabel", label: "Type" } as SortTableColumn);
    this.availableColumns.push({ id: "label", fieldName: "paymentLabel", label: "Libellé" } as SortTableColumn);
    this.availableColumns.push({ id: "isInternallyAssociated", fieldName: "isAssociated", label: "Associé dans Osiris", valueFonction: (element: any) => { return (element.isAssociated) ? "Oui" : "Non" } } as SortTableColumn);
    this.availableColumns.push({ id: "isExternallyAssociated", fieldName: "isExternallyAssociated", label: "Associé hors Osiris", valueFonction: (element: any) => { return element.isExternallyAssociated ? "Oui" : "Non" } } as SortTableColumn);
    this.availableColumns.push({ id: "isCancelled", fieldName: "isCancelled", label: "Annulé", valueFonction: (element: any) => { return element.isCancelled ? "Oui" : "Non" } } as SortTableColumn);
    this.availableColumns.push({ id: "isAppoint", fieldName: "isAppoint", label: "Appoint", valueFonction: (element: any) => { return element.isCancelled ? "Oui" : "Non" } } as SortTableColumn);
    this.availableColumns.push({ id: "invoice", fieldName: "invoiceId", label: "Facture associée", actionLinkFunction: this.getActionLink, actionIcon: "visibility", actionTooltip: "Voir la facture associée" } as SortTableColumn);
    this.availableColumns.push({ id: "comment", fieldName: "comment", label: "Commentaire" } as SortTableColumn);

    if (this.overrideIconAction == "") {
      if (this.habilitationService.canModifyPaymentAssociation()) {
        this.tableAction.push({
          actionIcon: "merge_type", actionName: "Associer le paiement", actionClick: (action: SortTableAction, element: any) => {
            if ((!element.invoice && !element.customerOrder && !element.isExternallyAssociated && !element.isCancelled && !element.isAssociated))
              this.openAssociationDialog(element);
          }, display: true,
        } as SortTableAction);
        this.tableAction.push({
          actionIcon: "savings", actionName: "Rembourser le paiement", actionClick: (action: SortTableAction, element: PaymentSearchResult) => {
            if ((!element.invoiceId && !element.isExternallyAssociated && !element.isCancelled && !element.isAssociated && element.paymentAmount > 0))
              this.openRefundPaymentDialog(element);
          }, display: true,
        } as SortTableAction);
        this.tableAction.push({
          actionIcon: "visibility", actionName: "Voir le détail du paiement", actionClick: (action: SortTableAction, element: PaymentSearchResult) => {
            this.paymentDetailsDialogService.displayPaymentDetailsDialog(element as any);
          }, display: true,
        } as SortTableAction);
        this.tableAction.push({
          actionIcon: "account_balance", actionName: "Mettre en compte", actionClick: (action: SortTableAction, element: PaymentSearchResult) => {
            if (!element.isAssociated && !element.isCancelled)
              this.displayAccountingPaymentDetailsDialog(element as any);
          }, display: true,
        } as SortTableAction);
        this.tableAction.push({
          actionIcon: 'mode_comment', actionName: 'Modifier le commentaire', actionClick: (action: SortTableAction, element: any) => {
            let dialogRef = this.editCommentDialog.open(EditCommentDialogComponent, {
              width: '40%'
            });
            dialogRef.componentInstance.comment = element.comment;

            dialogRef.afterClosed().subscribe(newComment => {
              if (newComment) {
                this.paymentService.addOrUpdatePaymentComment(element.id, newComment).subscribe(response => { this.searchPayments() });
              }
            });
          }, display: true,
        } as SortTableAction);
      }
    } else {
      this.tableAction.push({
        actionIcon: this.overrideIconAction, actionName: this.overrideTooltipAction, actionClick: (action: SortTableAction, element: any) => {
          this.actionBypass.emit(element);
        }, display: true,
      } as SortTableAction);
    };

    this.setColumns();

    this.paymentSearch.isHideAssociatedPayments = true;
    this.paymentSearch.isHideAppoint = true;
    this.paymentSearch.isHideCancelledPayments = true;

    if (this.isForDashboard && !this.payments && this.paymentSearch) {
      this.searchPayments();
    }
  }

  paymentForm = this.formBuilder.group({
  });

  setColumns() {
    this.displayedColumns = [];
    if (this.availableColumns && this.columnToDisplayOnDashboard && this.isForDashboard) {
      for (let availableColumn of this.availableColumns)
        for (let columnToDisplay of this.columnToDisplayOnDashboard)
          if (availableColumn.id == columnToDisplay)
            this.displayedColumns.push(availableColumn);
    }
    else
      this.displayedColumns.push(...this.availableColumns);
  }

  getActionLink(action: SortTableColumn, element: any) {
    if (element && action.id == "invoice" && element.invoices && element.invoices[0] && element.invoices.length == 1)
      return ['/invoicing/view', element.invoices[0].id];
    return undefined;
  }

  searchPayments() {
    if (this.paymentForm.valid) {
      if (this.paymentSearch.startDate)
        this.paymentSearch.startDate = new Date(toIsoString(this.paymentSearch.startDate));
      if (this.paymentSearch.endDate)
        this.paymentSearch.endDate = new Date(toIsoString(this.paymentSearch.endDate));
      this.paymentSearchResultService.getPayments(this.paymentSearch).subscribe(response => {
        this.payments = response;
      })
    }
  }

  importOfxFile() {
    this.selectAccountingAccountDialogComponentRef = this.selectAccountingAccountDialogComponent.open(SelectAccountingAccountDialogComponent, {
    });

    let accountingAccountSelected: AccountingAccount | undefined;
    this.selectAccountingAccountDialogComponentRef.afterClosed().subscribe(response => {
      accountingAccountSelected = response;

      if (accountingAccountSelected) {
        this.uploadAttachementDialogRef = this.uploadAttachementDialog.open(UploadAttachementDialogComponent, {
        });

        // Disgusting ... Id here represent target accounting account for this import...
        this.uploadAttachementDialogRef.componentInstance.entity = { id: accountingAccountSelected.id } as IAttachment;
        this.uploadAttachementDialogRef.componentInstance.entityType = OFX_ENTITY_TYPE.entityType;
        this.uploadAttachementDialogRef.componentInstance.forcedAttachmentType = this.constantService.getAttachmentTypeBillingClosure();
        this.uploadAttachementDialogRef.componentInstance.replaceExistingAttachementType = true;
        this.uploadAttachementDialogRef.afterClosed().subscribe(response => {
          this.paymentSearch = {} as PaymentSearch;
          this.paymentSearch.isHideCancelledPayments = true;
          this.paymentSearch.startDate = new Date();
          this.paymentSearch.startDate.setHours(0);
          this.paymentSearch.startDate.setMinutes(0);
          this.paymentSearch.startDate.setSeconds(0);
          this.paymentSearch.endDate = new Date();
          this.paymentSearch.endDate.setHours(23);
          this.paymentSearch.endDate.setMinutes(59);
          this.paymentSearch.endDate.setSeconds(59);
          this.searchPayments();
        });
      }
    })
  }

  openAssociationDialog(elementIn: PaymentSearchResult) {
    this.paymentService.getPaymentById(elementIn.id).subscribe(element => {
      let dialogPaymentDialogRef = this.associatePaymentDialog.open(AssociatePaymentDialogComponent, {
        width: '100%'
      });
      dialogPaymentDialogRef.componentInstance.payment = element;
      dialogPaymentDialogRef.afterClosed().subscribe(response => {
        this.searchPayments();
      });
    })
  }

  openRefundPaymentDialog(payment: PaymentSearchResult) {
    if (!this.habilitationService.canRefundPayment())
      this.appService.displaySnackBar("Non autorisé", true, 10);
    if (payment && payment.paymentAmount > 0) {
      this.paymentService.getPaymentById(payment.id).subscribe(element => {
        let dialogPaymentDialogRef = this.refundPaymentDialog.open(RefundPaymentDialogComponent, {
          width: '100%'
        });
        dialogPaymentDialogRef.afterClosed().subscribe(response => {
          this.paymentService.refundPayment(element, response.tiers, response.affaire).subscribe(response => this.searchPayments());
        });
      })
    } else {
      this.appService.displaySnackBar("Impossible de rembourser un paiement sortant", true, 10);
    }
  }

  displayAccountingPaymentDetailsDialog(payment: PaymentSearchResult) {
    if (!this.habilitationService.canRefundPayment())
      this.appService.displaySnackBar("Non autorisé", true, 10);
    if (payment && payment.paymentAmount < 0) {
      this.paymentService.getPaymentById(payment.id).subscribe(element => {
        let dialogCompetentAuthorityDialogRef = this.selectCompetentAuthorityDialog.open(SelectCompetentAuthorityDialogComponent, {
          width: '100%'
        });
        dialogCompetentAuthorityDialogRef.afterClosed().subscribe(response => {
          if (response)
            this.paymentService.putInAccount(payment, response).subscribe(response => this.searchPayments());
        });
      })
    } else {
      this.appService.displaySnackBar("Impossible de mettre en compte un paiement entrant", true, 10);
    }
  }
}
