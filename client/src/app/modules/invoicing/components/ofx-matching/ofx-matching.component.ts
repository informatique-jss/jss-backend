import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { formatDateTimeForSortTable, formatEurosForSortTable, toIsoString } from 'src/app/libs/FormatHelper';
import { EditCommentDialogComponent } from 'src/app/modules/miscellaneous/components/edit-comment-dialog.component/edit-comment-dialog-component.component';
import { SortTableAction } from 'src/app/modules/miscellaneous/model/SortTableAction';
import { SortTableColumn } from 'src/app/modules/miscellaneous/model/SortTableColumn';
import { RefundPaymentDialogComponent } from 'src/app/modules/quotation/components/refund-payment-dialog/refund-payment-dialog.component';
import { SelectAccountingAccountDialogComponent } from 'src/app/modules/quotation/components/select-accounting-account-dialog/select-accounting-account-dialog.component';
import { AppService } from 'src/app/services/app.service';
import { HabilitationsService } from 'src/app/services/habilitations.service';
import { UserPreferenceService } from 'src/app/services/user.preference.service';
import { Payment } from '../../model/Payment';
import { PaymentSearch } from '../../model/PaymentSearch';
import { PaymentSearchResult } from '../../model/PaymentSearchResult';
import { PaymentDetailsDialogService } from '../../services/payment.details.dialog.service';
import { PaymentService } from '../../services/payment.service';
import { AssociatePaymentDialogComponent } from '../associate-payment-dialog/associate-payment-dialog.component';

@Component({
  selector: 'ofx-matching',
  templateUrl: './ofx-matching.component.html',
  styleUrls: ['./ofx-matching.component.css']
})
export class OfxMatchingComponent implements OnInit {

  payments: Payment[] | undefined;
  displayedColumns: SortTableColumn<Payment>[] = [];
  tableAction: SortTableAction<Payment>[] = [];
  bookmark: PaymentSearch | undefined;
  paymentSearch: PaymentSearch = {} as PaymentSearch;

  constructor(
    private paymentService: PaymentService,
    private formBuilder: FormBuilder,
    private userPreferenceService: UserPreferenceService,
    private appService: AppService,
    private associatePaymentDialog: MatDialog,
    private refundPaymentDialog: MatDialog,
    private editCommentDialog: MatDialog,
    private selectAccountingAccountDialogComponent: MatDialog,
    private habilitationService: HabilitationsService,
    private paymentDetailsDialogService: PaymentDetailsDialogService,
  ) { }

  ofxMatchForm = this.formBuilder.group({});

  ngOnInit() {
    this.displayedColumns = [];
    this.displayedColumns.push({ id: "id", fieldName: "id", label: "N° du paiement" } as SortTableColumn<Payment>);
    this.displayedColumns.push({ id: "payemntDate", fieldName: "paymentDate", label: "Date", valueFonction: formatDateTimeForSortTable } as SortTableColumn<Payment>);
    this.displayedColumns.push({ id: "payemntAmount", fieldName: "paymentAmount", label: "Montant", valueFonction: formatEurosForSortTable } as SortTableColumn<Payment>);
    this.displayedColumns.push({ id: "paymentTypeLabel", fieldName: "paymentType.label", label: "Type" } as SortTableColumn<Payment>);
    this.displayedColumns.push({ id: "label", fieldName: "label", label: "Libellé" } as SortTableColumn<Payment>);
    this.displayedColumns.push({ id: "isInternallyAssociated", fieldName: "isAssociated", label: "Associé dans Osiris", valueFonction: (element: Payment, column: SortTableColumn<Payment>) => { return (this.isPaymentNotAssociated(element)) ? "Non" : "Oui" } } as SortTableColumn<Payment>);
    this.displayedColumns.push({ id: "checkDepositNumber", fieldName: "checkDepositNumber", label: "Remise de chèque" } as SortTableColumn<Payment>);
    this.displayedColumns.push({ id: "checkNumber", fieldName: "checkNumber", label: "N° chèque" } as SortTableColumn<Payment>);
    this.displayedColumns.push({ id: "invoice", fieldName: "invoice.id", label: "Facture", actionLinkFunction: this.getActionLink, actionIcon: "visibility", actionTooltip: "Voir la facture associée" } as SortTableColumn<Payment>);
    this.displayedColumns.push({ id: "customerOrder", fieldName: "customerOrder.id", label: "Commande", actionLinkFunction: this.getActionLink, actionIcon: "visibility", actionTooltip: "Voir la facture associée" } as SortTableColumn<Payment>);
    this.displayedColumns.push({ id: "refund", fieldName: "refund.id", label: "Remboursement", actionLinkFunction: this.getActionLink, actionIcon: "visibility", actionTooltip: "Voir la facture associée" } as SortTableColumn<Payment>);
    this.displayedColumns.push({ id: "bankTransfert", fieldName: "bankTransfert.id", label: "Virement", actionLinkFunction: this.getActionLink, actionIcon: "visibility", actionTooltip: "Voir la facture associée" } as SortTableColumn<Payment>);
    this.displayedColumns.push({ id: "directDebitTransfert", fieldName: "directDebitTransfert.id", label: "Prélèvement", actionLinkFunction: this.getActionLink, actionIcon: "visibility", actionTooltip: "Voir la facture associée" } as SortTableColumn<Payment>);
    this.displayedColumns.push({ id: "accountingAccount", fieldName: "accountingAccount", valueFonction: (element: Payment, column: SortTableColumn<Payment>) => { if (element && element.accountingAccount && column) return element.accountingAccount.principalAccountingAccount.code + element.accountingAccount.accountingAccountSubNumber; return "" } } as SortTableColumn<Payment>);
    this.displayedColumns.push({ id: "matchAutomation", fieldName: "matchAutomation", label: "Type" } as SortTableColumn<Payment>);
    this.displayedColumns.push({ id: "matchType", fieldName: "matchType", label: "Rapprochement" } as SortTableColumn<Payment>);
    this.displayedColumns.push({ id: "comment", fieldName: "comment", label: "Commentaire" } as SortTableColumn<Payment>);

    this.tableAction.push({
      actionIcon: "visibility", actionName: "Voir le détail du paiement", actionClick: (column: SortTableAction<Payment>, element: Payment, event: any) => {
        this.paymentDetailsDialogService.displayPaymentDetailsDialog(element);
      }, display: true,
    } as SortTableAction<Payment>);

    if (this.habilitationService.canModifyPaymentAssociation()) {
      this.tableAction.push({
        actionIcon: "merge_type", actionName: "Associer le paiement", actionClick: (column: SortTableAction<Payment>, element: Payment, event: any) => {
          if (this.isPaymentNotAssociated(element))
            this.openAssociationDialog(element);
        }, display: true,
      } as SortTableAction<Payment>);

      this.tableAction.push({
        actionIcon: "savings", actionName: "Rembourser le paiement", actionClick: (column: SortTableAction<Payment>, element: Payment, event: any) => {
          if (this.isPaymentNotAssociated(element))
            this.openRefundPaymentDialog(element);
        }, display: true,
      } as SortTableAction<Payment>);

      this.tableAction.push({
        actionIcon: 'mode_comment', actionName: 'Modifier le commentaire', actionClick: (column: SortTableAction<Payment>, element: Payment, event: any) => {
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
      } as SortTableAction<Payment>);
    }

    if (this.habilitationService.canPutInAnyAccountingAccount())
      this.tableAction.push({
        actionIcon: "account_balance", actionName: "Mettre en compte comptable", actionClick: (column: SortTableAction<Payment>, element: Payment, event: any) => {
          if (this.isPaymentNotAssociated(element))
            this.displayPutInAccountingAccountDialog(element as any);
        }, display: true,
      } as SortTableAction<Payment>);

    this.bookmark = this.userPreferenceService.getUserSearchBookmark("ofx-match") as PaymentSearch;
    if (this.bookmark) {
      this.paymentSearch = this.bookmark;
      if (this.paymentSearch.startDate)
        this.paymentSearch.startDate = new Date(this.paymentSearch.startDate);
      if (this.paymentSearch.endDate)
        this.paymentSearch.endDate = new Date(this.paymentSearch.endDate);
      this.searchPayments();
    }
  }

  getActionLink(action: SortTableColumn<Payment>, element: Payment) {
    if (element && action.id == "invoice" && element.invoice && element.invoice.id)
      return ['/invoicing/view', element.invoice.id];
    if (element && action.id == "customerOrder" && element.customerOrder && element.customerOrder.id)
      return ['/order', element.customerOrder.id];
    if (element && action.id == "refund" && element.refund && element.refund.id)
      return ['/invoicing/refund', element.refund.id];
    if (element && action.id == "bankTransfert" && element.bankTransfert && element.bankTransfert.id)
      return ['/invoicing/bankTransfert', element.bankTransfert.id];
    if (element && action.id == "directDebitTransfert" && element.directDebitTransfert && element.directDebitTransfert.id)
      return ['/invoicing/directDebit', element.directDebitTransfert.id];
    return undefined;
  }


  searchPayments() {
    if (this.ofxMatchForm.valid) {
      if (this.paymentSearch.startDate)
        this.paymentSearch.startDate = new Date(toIsoString(this.paymentSearch.startDate));
      if (this.paymentSearch.endDate)
        this.paymentSearch.endDate = new Date(toIsoString(this.paymentSearch.endDate));
      this.userPreferenceService.setUserSearchBookmark(this.paymentSearch, "ofx-match");
      this.paymentService.getMatchingOfxPayments(this.paymentSearch).subscribe(response => {
        this.payments = response;
      })
    }
  }

  isPaymentNotAssociated(payment: Payment) {
    return payment.invoice == null && payment.customerOrder == null && payment.directDebitTransfert == null && payment.refund == null && payment.bankTransfert == null && payment.isExternallyAssociated == false
      && payment.isCancelled == false && payment.competentAuthority == null && payment.provider == null && payment.accountingAccount == null;
  }

  openAssociationDialog(element: Payment) {
    let dialogPaymentDialogRef = this.associatePaymentDialog.open(AssociatePaymentDialogComponent, {
      width: '100%'
    });
    dialogPaymentDialogRef.componentInstance.payment = element;
    dialogPaymentDialogRef.afterClosed().subscribe(response => {
      this.searchPayments();
    });
  }

  openRefundPaymentDialog(payment: Payment) {
    if (!this.habilitationService.canRefundPayment())
      this.appService.displaySnackBar("Non autorisé", true, 10);
    if (payment && payment.paymentAmount > 0) {
      let dialogPaymentDialogRef = this.refundPaymentDialog.open(RefundPaymentDialogComponent, {
        width: '100%'
      });
      dialogPaymentDialogRef.afterClosed().subscribe(response => {
        this.paymentService.refundPayment(payment, response.tiers, response.affaire).subscribe(response => this.searchPayments());
      });
    } else {
      this.appService.displaySnackBar("Impossible de rembourser un paiement sortant", true, 10);
    }
  }

  displayPutInAccountingAccountDialog(payment: Payment) {
    let selectAccountingAccountDialogComponentRef = this.selectAccountingAccountDialogComponent.open(SelectAccountingAccountDialogComponent, {
    });

    selectAccountingAccountDialogComponentRef.componentInstance.filteredAccountPrincipal = undefined;
    selectAccountingAccountDialogComponentRef.afterClosed().subscribe(response => {
      if (response) {
        this.paymentService.putInAccount({ id: payment.id } as PaymentSearchResult, response).subscribe(response => this.searchPayments());
      }
    });
  }
}
