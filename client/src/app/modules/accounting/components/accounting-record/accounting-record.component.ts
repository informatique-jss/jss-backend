import { CdkDragEnd, Point } from '@angular/cdk/drag-drop';
import { Component, Input, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { MatTableDataSource } from '@angular/material/table';
import { ActivatedRoute, UrlSegment } from '@angular/router';
import { Subject } from 'rxjs';
import { formatDateForSortTable, formatDateTimeForSortTable, formatEurosForSortTable } from 'src/app/libs/FormatHelper';
import { Payment } from 'src/app/modules/invoicing/model/Payment';
import { PaymentDetailsDialogService } from 'src/app/modules/invoicing/services/payment.details.dialog.service';
import { ConfirmDialogComponent } from 'src/app/modules/miscellaneous/components/confirm-dialog/confirm-dialog.component';
import { UploadAttachementDialogComponent } from 'src/app/modules/miscellaneous/components/upload-attachement-dialog/upload-attachement-dialog.component';
import { IAttachment } from 'src/app/modules/miscellaneous/model/IAttachment';
import { SortTableAction } from 'src/app/modules/miscellaneous/model/SortTableAction';
import { SortTableColumn } from 'src/app/modules/miscellaneous/model/SortTableColumn';
import { ConstantService } from 'src/app/modules/miscellaneous/services/constant.service';
import { Responsable } from 'src/app/modules/tiers/model/Responsable';
import { Tiers } from 'src/app/modules/tiers/model/Tiers';
import { SAGE_ENTITY_TYPE } from 'src/app/routing/search/search.component';
import { AppService } from 'src/app/services/app.service';
import { UserPreferenceService } from 'src/app/services/user.preference.service';
import { HabilitationsService } from '../../../../services/habilitations.service';
import { AccountingRecord } from '../../model/AccountingRecord';
import { AccountingRecordSearch } from '../../model/AccountingRecordSearch';
import { AccountingRecordSearchResult } from '../../model/AccountingRecordSearchResult';
import { AccountingAccountService } from '../../services/accounting.account.service';
import { AccountingRecordSearchResultService } from '../../services/accounting.record.search.result.service';
import { AccountingRecordService } from '../../services/accounting.record.service';

@Component({
  selector: 'accounting-record',
  templateUrl: './accounting-record.component.html',
  styleUrls: ['./accounting-record.component.css']
})
export class AccountingRecordComponent implements OnInit {

  // Used for integration in tiers and responsable component
  @Input() tiersToDisplay: Tiers | undefined;
  @Input() responsableToDisplay: Responsable | undefined;
  @Input() accountingRecordSearch: AccountingRecordSearch = {} as AccountingRecordSearch;

  constructor(
    private formBuilder: FormBuilder,
    private accountingRecordService: AccountingRecordService,
    private userPreferenceService: UserPreferenceService,
    private appService: AppService,
    private accountingRecordSearchService: AccountingRecordSearchResultService,
    public deleteAccountingRecordDialog: MatDialog,
    private habilitationService: HabilitationsService,
    private paymentDetailsDialogService: PaymentDetailsDialogService,
    public confirmationDialog: MatDialog,
    private constantService: ConstantService,
    private activatedRoute: ActivatedRoute,
    private accountingAccountService: AccountingAccountService,
    public uploadAttachementDialog: MatDialog,
  ) { }

  accountingRecords: AccountingRecordSearchResult[] | undefined;
  toLetteredValues: AccountingRecordSearchResult[] | undefined;
  displayedColumnsTotal: string[] = ['label', 'debit', 'credit'];
  accumulatedDataSource = new MatTableDataSource<AccountingRecord>();
  currentUserPosition: Point = { x: 0, y: 0 };
  displayedColumns: SortTableColumn<AccountingRecordSearchResult>[] = [] as Array<SortTableColumn<AccountingRecordSearchResult>>;
  tableAction: SortTableAction<AccountingRecordSearchResult>[] = [] as Array<SortTableAction<AccountingRecordSearchResult>>;
  tableActionToLetterValues: SortTableAction<AccountingRecordSearchResult>[] = [] as Array<SortTableAction<AccountingRecordSearchResult>>;
  bookmark: AccountingRecordSearch | undefined;
  refreshLetteringTable: Subject<void> = new Subject<void>();
  accountingAccountId: string | undefined;
  uploadAttachementDialogRef: MatDialogRef<UploadAttachementDialogComponent> | undefined;

  canAddNewAccountingRecord() {
    return this.habilitationService.canAddNewAccountingRecord();
  }

  ngOnInit() {
    // Column init
    this.displayedColumns = [];
    this.displayedColumns.push({ id: "invoice", fieldName: "invoiceId", label: "Facture", actionLinkFunction: this.getColumnLink, actionIcon: "visibility", actionTooltip: "Voir la facture associée" } as SortTableColumn<AccountingRecordSearchResult>);
    this.displayedColumns.push({ id: "customerOrder", fieldName: "customerId", label: "Commande", actionLinkFunction: this.getColumnLink, actionIcon: "visibility", actionTooltip: "Voir la commande associée" } as SortTableColumn<AccountingRecordSearchResult>);
    this.displayedColumns.push({ id: "affaireLabel", fieldName: "affaireLabel", label: "Affaire(s)" } as SortTableColumn<AccountingRecordSearchResult>);
    this.displayedColumns.push({ id: "accountingDateTime", fieldName: "accountingDateTime", label: "Date d'écriture", valueFonction: this.formatDateForSortTable } as SortTableColumn<AccountingRecordSearchResult>);
    this.displayedColumns.push({ id: "operationDateTime", fieldName: "operationDateTime", label: "Date d'opération", valueFonction: this.formatDateTimeForSortTable } as SortTableColumn<AccountingRecordSearchResult>);
    this.displayedColumns.push({ id: "operationId", fieldName: "operationId", label: "N° d'écriture" } as SortTableColumn<AccountingRecordSearchResult>);
    this.displayedColumns.push({ id: "accountingJournal", fieldName: "accountingJournalLabel", label: "Journal" } as SortTableColumn<AccountingRecordSearchResult>);
    this.displayedColumns.push({ id: "accountingAccountNumber", fieldName: "accountingAccountNumber", label: "N° de compte", valueFonction: (element: AccountingRecordSearchResult, column: SortTableColumn<AccountingRecordSearchResult>) => { if (element && column) return element.principalAccountingAccountCode + element.accountingAccountSubNumber; return "" } } as SortTableColumn<AccountingRecordSearchResult>);
    this.displayedColumns.push({ id: "accountingAccountLabel", fieldName: "accountingAccountLabel", label: "Libellé du compte", isShrinkColumn: true } as SortTableColumn<AccountingRecordSearchResult>);
    this.displayedColumns.push({ id: "accountingDocumentNumber", fieldName: "manualAccountingDocumentNumber", label: "N° de pièce justificative" } as SortTableColumn<AccountingRecordSearchResult>);
    this.displayedColumns.push({ id: "accountingDocumentDate", fieldName: "manualAccountingDocumentDate", label: "Date pièce justificative", valueFonction: this.formatDateForSortTable } as SortTableColumn<AccountingRecordSearchResult>);
    this.displayedColumns.push({ id: "debitAmount", fieldName: "debitAmount", label: "Débit", valueFonction: this.formatEurosForSortTable } as SortTableColumn<AccountingRecordSearchResult>);
    this.displayedColumns.push({ id: "creditAmount", fieldName: "creditAmount", label: "Crédit", valueFonction: this.formatEurosForSortTable } as SortTableColumn<AccountingRecordSearchResult>);
    this.displayedColumns.push({ id: "label", fieldName: "label", label: "Libellé", isShrinkColumn: true } as SortTableColumn<AccountingRecordSearchResult>);
    this.displayedColumns.push({ id: "letteringNumber", fieldName: "letteringNumber", label: "Lettrage" } as SortTableColumn<AccountingRecordSearchResult>);
    this.displayedColumns.push({ id: "letteringDate", fieldName: "letteringDate", label: "Date de lettrage", valueFonction: this.formatDateForSortTable } as SortTableColumn<AccountingRecordSearchResult>);
    this.displayedColumns.push({ id: "debitAccumulation", fieldName: "debitAccumulation", label: "Cumul débit", valueFonction: this.formatEurosForSortTable } as SortTableColumn<AccountingRecordSearchResult>);
    this.displayedColumns.push({ id: "creditAccumulation", fieldName: "creditAccumulation", label: "Cumul crédit", valueFonction: this.formatEurosForSortTable } as SortTableColumn<AccountingRecordSearchResult>);
    this.displayedColumns.push({ id: "balance", fieldName: "balance", label: "Solde", valueFonction: this.formatEurosForSortTable } as SortTableColumn<AccountingRecordSearchResult>);
    this.displayedColumns.push({ id: "payment", fieldName: "paymentId", label: "Paiement", actionFunction: (element: AccountingRecordSearchResult) => this.paymentDetailsDialogService.displayPaymentDetailsDialog({ id: element.paymentId } as Payment), actionIcon: "visibility", actionTooltip: "Voir le détail du paiement" } as SortTableColumn<AccountingRecordSearchResult>);

    if (this.tiersToDisplay && this.tiersToDisplay.id) {
      this.accountingRecordSearch.tiersId = this.tiersToDisplay.id;
      this.accountingRecordSearch.hideLettered = true;
      this.searchRecords();
    } else if (this.accountingRecordSearch && this.accountingRecordSearch.idPayment)
      this.searchRecords();
    else {
      this.bookmark = this.userPreferenceService.getUserSearchBookmark("accounting-record") as AccountingRecordSearch;
      if (this.bookmark) {
        this.accountingRecordSearch = this.bookmark;
        if (this.accountingRecordSearch.startDate)
          this.accountingRecordSearch.startDate = new Date(this.accountingRecordSearch.startDate);
        if (this.accountingRecordSearch.endDate)
          this.accountingRecordSearch.endDate = new Date(this.accountingRecordSearch.endDate);
        this.searchRecords();
      }
    }

    if (this.habilitationService.canManuallyLetterAs400AccountingRecords())
      this.tableAction.push({
        actionIcon: 'add', actionName: "Ajouter au lettrage en cours", actionClick: (column: SortTableAction<AccountingRecordSearchResult>, element: AccountingRecordSearchResult, event: any) => {
          if (element) {
            if (!this.toLetteredValues)
              this.toLetteredValues = [];
            if (this.toLetteredValues.indexOf(element) < 0 && element.letteringNumber == null)
              this.toLetteredValues.push(element);
            this.refreshLetteringTable.next();
          }
        }, display: true,
      } as SortTableAction<AccountingRecordSearchResult>);

    if (this.habilitationService.canUpdateAccountingRecordsOnBilanJournal())
      this.tableAction.push({
        actionIcon: 'edit', actionName: "Modifier l'écriture", actionClick: (column: SortTableAction<AccountingRecordSearchResult>, element: AccountingRecordSearchResult, event: any) => {
          if (element.isManual) {
            this.appService.openRoute(event, '/accounting/edit/' + element.temporaryOperationId, undefined);
          } else {
            this.appService.displaySnackBar("La modification n'est possible sur ces lignes", true, 10);
          }
        }, display: true,
      } as SortTableAction<AccountingRecordSearchResult>);

    if (this.habilitationService.canDeleteAccountingRecordsOnBilanJournal())
      this.tableAction.push({
        actionIcon: 'delete', actionName: "Supprimer l'écriture", actionClick: (column: SortTableAction<AccountingRecordSearchResult>, element: AccountingRecordSearchResult, event: any) => {
          if (element && (element.accountingJournalCode == this.constantService.getAccountingJournalBilan().code || element.accountingJournalCode == this.constantService.getAccountingJournalSalary().code)) {
            const dialogRef = this.confirmationDialog.open(ConfirmDialogComponent, {
              maxWidth: "400px",
              data: {
                title: "Supprimer les lignes de comptabilité ?",
                content: "Êtes-vous sûr de vouloir supprimer la ligne sélectionnée et les lignes associées ?",
                closeActionText: "Annuler",
                validationActionText: "Supprimer"
              }
            });

            dialogRef.afterClosed().subscribe(dialogResult => {
              if (dialogResult)
                this.accountingRecordService.deleteRecords(element).subscribe(res => { this.searchRecords() });
            });
          } else {
            this.appService.displaySnackBar("La suppression n'est possible que sur des lignes du journal Bilan et Salaire", true, 10);
          }
        }, display: true,
      } as SortTableAction<AccountingRecordSearchResult>);

    this.tableActionToLetterValues.push({
      actionIcon: 'delete', actionName: "Supprimer l'écriture", actionClick: (column: SortTableAction<AccountingRecordSearchResult>, element: AccountingRecordSearchResult, event: any) => {
        if (element && this.toLetteredValues) {
          this.toLetteredValues.splice(this.toLetteredValues.indexOf(element), 1);
        }
      }, display: true,
    } as SortTableAction<AccountingRecordSearchResult>);

    let url: UrlSegment[] = this.activatedRoute.snapshot.url;
    this.accountingAccountId = this.activatedRoute.snapshot.params.accountingAccountId;
    if (url[1].path == "view" && this.accountingAccountId) {
      this.accountingAccountService.getAccountingAccountById(parseInt(this.accountingAccountId)).subscribe(response => {
        this.accountingRecordSearch.accountingAccount = response;
        this.accountingRecordSearch.hideLettered = true;
        this.setCurentFiscalYear();
        this.searchRecords();
      });
    }
  }

  letterSelectedRecords() {
    if (this.toLetteredValues) {
      let as400Found = false;
      for (let record of this.toLetteredValues) {
        if (record.isFromAs400 && record.isFromAs400 == true) {
          as400Found = true;
          break;
        }
      }
      if (!as400Found && !this.habilitationService.canManuallyLetterAccountingRecords()) {
        this.appService.displaySnackBar("Les lignes sélectionnées doivent contenir au moins une ligne de l'AS400", true, 10);
        return;
      }
      this.accountingRecordService.letterRecordsForAs400(this.toLetteredValues).subscribe(res => {
        this.toLetteredValues = [];
        this.searchRecords();
      })
    }
  }


  formatEurosForSortTable = formatEurosForSortTable;
  formatDateForSortTable = formatDateForSortTable;
  formatDateTimeForSortTable = formatDateTimeForSortTable;

  accountingRecordForm = this.formBuilder.group({
  });

  getColumnLink(column: SortTableColumn<AccountingRecordSearchResult>, element: AccountingRecordSearchResult) {
    if (element && column.id == "invoice" && element.invoiceId) {
      return ['/invoicing/view', element.invoiceId];
    }
    if (element && column.id == "customerOrder" && element.customerId) {
      return ['/order', element.customerId];
    }
    return null;
  }

  putEndDateSameYear() {
    if (this.accountingRecordSearch.startDate && this.accountingRecordSearch.endDate
      && this.accountingRecordSearch.startDate.getFullYear() != this.accountingRecordSearch.endDate.getFullYear()) {
      this.accountingRecordSearch.endDate = new Date(this.accountingRecordSearch.startDate.getFullYear(), 11, 31);
    }
  }

  exportGrandLivre() {
    if (this.accountingRecordSearch.startDate && this.accountingRecordSearch.endDate)
      if (this.accountingRecordSearch.startDate.getFullYear() != this.accountingRecordSearch.endDate.getFullYear()) {
        this.appService.displaySnackBar("Merci de saisir une plage de recherche sur un seul exercice fiscal", false, 10);
        return;
      }
    if (this.accountingRecordSearch.startDate) {
      this.accountingRecordSearch.startDate = new Date(this.accountingRecordSearch.startDate.setHours(12));
      if (this.accountingRecordSearch.endDate)
        this.accountingRecordSearch.endDate = new Date(this.accountingRecordSearch.endDate.setHours(12));
      this.accountingRecordService.exportGrandLivre(this.accountingRecordSearch);
    }
  }

  exportAllGrandLivre() {
    if (this.accountingRecordSearch.startDate && this.accountingRecordSearch.endDate)
      if (this.accountingRecordSearch.startDate.getFullYear() != this.accountingRecordSearch.endDate.getFullYear()) {
        this.appService.displaySnackBar("Merci de saisir une plage de recherche sur un seul exercice fiscal", false, 10);
        return;
      }
    if (this.accountingRecordSearch.startDate) {
      this.accountingRecordSearch.startDate = new Date(this.accountingRecordSearch.startDate.setHours(12));
      if (this.accountingRecordSearch.endDate)
        this.accountingRecordSearch.endDate = new Date(this.accountingRecordSearch.endDate.setHours(12));
      this.accountingRecordService.exportGrandLivre(this.accountingRecordSearch);
    }
  }

  exportJournal() {
    if (this.accountingRecordSearch.startDate && this.accountingRecordSearch.endDate)
      if (this.accountingRecordSearch.startDate.getFullYear() != this.accountingRecordSearch.endDate.getFullYear()) {
        this.appService.displaySnackBar("Merci de saisir une plage de recherche sur un seul exercice fiscal", false, 10);
        return;
      }
    if (this.accountingRecordSearch.startDate) {
      this.accountingRecordSearch.startDate = new Date(this.accountingRecordSearch.startDate.setHours(12));
      if (this.accountingRecordSearch.endDate)
        this.accountingRecordSearch.endDate = new Date(this.accountingRecordSearch.endDate.setHours(12));
      this.accountingRecordService.exportJournal(this.accountingRecordSearch);
    }
  }

  exportAccountingAccount() {
    if (this.accountingRecordSearch.startDate && this.accountingRecordSearch.endDate)
      if (this.accountingRecordSearch.startDate.getFullYear() != this.accountingRecordSearch.endDate.getFullYear()) {
        this.appService.displaySnackBar("Merci de saisir une plage de recherche sur un seul exercice fiscal", false, 10);
        return;
      }
    if (this.accountingRecordSearch.startDate) {
      this.accountingRecordSearch.startDate = new Date(this.accountingRecordSearch.startDate.setHours(12));
      if (this.accountingRecordSearch.endDate)
        this.accountingRecordSearch.endDate = new Date(this.accountingRecordSearch.endDate.setHours(12));
      this.accountingRecordService.exportAccountingAccount(this.accountingRecordSearch);
    }
  }

  createAccountingRecords(event: any) {
    this.appService.openRoute(event, '/accounting/add', null);
  }

  searchRecords() {
    this.restoreTotalDivPosition();
    if (!this.accountingRecordSearch.tiersId && !this.accountingRecordSearch.idPayment) {
      if (this.tiersToDisplay == undefined && (!this.accountingRecordSearch.startDate || !this.accountingRecordSearch.endDate)) {
        this.appService.displaySnackBar("🙄 Merci de saisir une plage de recherche", false, 10);
        return;
      }

      if (this.accountingRecordSearch.startDate && this.accountingRecordSearch.endDate)
        if (this.accountingRecordSearch.startDate.getFullYear() != this.accountingRecordSearch.endDate.getFullYear() && this.accountingRecordSearch.startDate.getFullYear() != new Date(this.constantService.getDateAccountingClosureForAll()).getFullYear()) {
          this.appService.displaySnackBar("Merci de saisir une plage de recherche sur un seul exercice fiscal", false, 10);
          return;
        }
    }
    if (this.accountingRecordSearch.tiersId || this.accountingRecordSearch.idPayment) {
      this.setCurentFiscalYear();
    }
    if (this.accountingRecordSearch.startDate)
      this.accountingRecordSearch.startDate = new Date(this.accountingRecordSearch.startDate.setHours(12));
    if (this.accountingRecordSearch.endDate)
      this.accountingRecordSearch.endDate = new Date(this.accountingRecordSearch.endDate.setHours(12));

    if (!this.tiersToDisplay && !this.accountingRecordSearch.idPayment && !this.accountingAccountId)
      this.userPreferenceService.setUserSearchBookmark(this.accountingRecordSearch, "accounting-record");
    this.accountingRecordSearchService.searchAccountingRecords(this.accountingRecordSearch).subscribe(response => {
      this.accountingRecords = response;
      this.computeBalanceAndDebitAndCreditAccumulation();

    });
  }

  setCurentMonth() {
    let d = new Date();
    this.accountingRecordSearch.startDate = new Date(d.getFullYear(), d.getMonth(), 1, 12, 0, 0);
    let d2 = new Date();
    this.accountingRecordSearch.endDate = new Date(d2.getFullYear(), d2.getMonth() + 1, 0, 12, 0, 0);
  }

  setCurentFiscalYear() {
    let d = new Date();
    this.accountingRecordSearch.startDate = new Date(d.getFullYear(), 0, 1, 12, 0, 0);
    let d2 = new Date();
    this.accountingRecordSearch.endDate = new Date(d2.getFullYear(), 11, 31, 12, 0, 0);
  }

  computeBalanceAndDebitAndCreditAccumulation() {
    if (this.accountingRecords) {
      let credit: number = 0;
      let debit: number = 0;
      let balance: number = 0;
      for (let accountingRecord of this.accountingRecords) {
        if (accountingRecord.creditAmount) {
          credit += accountingRecord.creditAmount;
          balance += accountingRecord.creditAmount;
        }
        if (accountingRecord.debitAmount) {
          debit += accountingRecord.debitAmount;
          balance -= accountingRecord.debitAmount;
        }
        accountingRecord.balance = balance;
        accountingRecord.debitAccumulation = debit;
        accountingRecord.creditAccumulation = credit;
      }

      let accumulatedData = [];
      let totalLine = {} as any;
      totalLine.label = "Total";
      totalLine.debit = debit.toFixed(2);
      totalLine.credit = credit.toFixed(2);
      accumulatedData.push(totalLine);

      let balanceLine = {} as any;
      balanceLine.label = "Balance";
      balanceLine.credit = balance.toFixed(2);
      accumulatedData.push(balanceLine);

      this.accumulatedDataSource.data = accumulatedData;

    }
  }

  dropTotalDiv(event: CdkDragEnd) {
    this.userPreferenceService.setUserTotalDivPosition(event.source.getFreeDragPosition());
  }

  restoreTotalDivPosition() {
    this.currentUserPosition = this.userPreferenceService.getUserTotalDivPosition();
  }

  restoreDefaultTotalDivPosition() {
    this.userPreferenceService.setUserTotalDivPosition({ x: 0, y: 0 });
    this.restoreTotalDivPosition();
  }

  downloadBillingClosureReceipt() {
    if (this.tiersToDisplay || this.responsableToDisplay)
      this.accountingRecordService.downloadBillingClosureReceipt(this.tiersToDisplay ? this.tiersToDisplay : this.responsableToDisplay!.tiers, this.responsableToDisplay);
  }

  sendBillingClosureReceipt() {
    if (this.tiersToDisplay) {
      const dialogRef = this.confirmationDialog.open(ConfirmDialogComponent, {
        maxWidth: "400px",
        data: {
          title: "Envoyer les relevés de compte",
          content: "Êtes-vous sûr de vouloir envoyer le(s) relevé(s) de compte de ces tiers/responsables ?",
          closeActionText: "Annuler",
          validationActionText: "Envoyer"
        }
      });

      dialogRef.afterClosed().subscribe(dialogResult => {
        if (dialogResult && this.tiersToDisplay)
          this.accountingRecordService.sendBillingClosureReceipt(this.tiersToDisplay, this.responsableToDisplay).subscribe(res => { });
      });
    }
  }

  getLetteringBalance() {
    let balance = 0;
    if (this.toLetteredValues)
      for (let value of this.toLetteredValues) {
        if (value.creditAmount && value.creditAmount > 0)
          balance += value.creditAmount;
        if (value.debitAmount && value.debitAmount > 0)
          balance -= value.debitAmount;
      }
    return balance;
  }

  canImportSageFile() {
    return this.habilitationService.canImportSageFile();
  }

  importSageFile() {
    this.uploadAttachementDialogRef = this.uploadAttachementDialog.open(UploadAttachementDialogComponent, {
    });
    this.uploadAttachementDialogRef.componentInstance.entity = { id: 1 } as IAttachment;
    this.uploadAttachementDialogRef.componentInstance.entityType = SAGE_ENTITY_TYPE.entityType;
    this.uploadAttachementDialogRef.componentInstance.forcedAttachmentType = this.constantService.getAttachmentTypeBillingClosure();
    this.uploadAttachementDialogRef.afterClosed().subscribe(response => { });
  }
}
