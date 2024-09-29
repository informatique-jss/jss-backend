import { Location } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Subject, Subscription } from 'rxjs';
import { formatDateForSortTable, formatEurosForSortTable } from 'src/app/libs/FormatHelper';
import { SortTableAction } from 'src/app/modules/miscellaneous/model/SortTableAction';
import { SortTableColumn } from 'src/app/modules/miscellaneous/model/SortTableColumn';
import { ConstantService } from 'src/app/modules/miscellaneous/services/constant.service';
import { AppService } from 'src/app/services/app.service';
import { AccountingJournal } from '../../model/AccountingJournal';
import { AccountingRecord } from '../../model/AccountingRecord';
import { AccountingRecordService } from '../../services/accounting.record.service';
import { ActivatedRoute, UrlSegment } from '@angular/router';
@Component({
  selector: 'add-accounting-record',
  templateUrl: './add-accounting-record.component.html',
  styleUrls: ['./add-accounting-record.component.css']
})
export class AddAccountingRecordComponent implements OnInit {

  accountingRecords: AccountingRecord[] = new Array<AccountingRecord>;
  accountingRecord: AccountingRecord = {} as AccountingRecord;
  isEditing: boolean = false;
  minDate: Date = new Date();
  maxDate: Date = new Date();

  constructor(private formBuilder: FormBuilder,
    private appService: AppService,
    private accountingRecordService: AccountingRecordService,
    private constantService: ConstantService,
    private activatedRoute: ActivatedRoute,
    private location: Location,
  ) {
  }

  accountingRecordForm = this.formBuilder.group({});
  displayedColumns: SortTableColumn<AccountingRecord>[] = [] as Array<SortTableColumn<AccountingRecord>>;
  tableAction: SortTableAction<AccountingRecord>[] = [] as Array<SortTableAction<AccountingRecord>>;

  accountingJournalSales: AccountingJournal = this.constantService.getAccountingJournalSales();
  accountingJournalPurchases: AccountingJournal = this.constantService.getAccountingJournalPurchases();
  accountingJournalANouveau: AccountingJournal = this.constantService.getAccountingJournalANouveau();

  saveObservableSubscription: Subscription = new Subscription;

  refreshTable: Subject<void> = new Subject<void>();

  ngOnInit() {
    this.addAccountingRecord();
    let url: UrlSegment[] = this.activatedRoute.snapshot.url;
    let temporaryOperationId = this.activatedRoute.snapshot.params.temporaryOperationId;

    this.setOperationDateInterval();

    // Column init
    this.displayedColumns = [];
    this.displayedColumns.push({ id: "accountingJournal", fieldName: "accountingJournal", label: "Journal", valueFonction: (element: AccountingRecord, column: SortTableColumn<AccountingRecord>) => { if (element && column && element.accountingJournal) return element.accountingJournal.label; return "" } } as SortTableColumn<AccountingRecord>);
    this.displayedColumns.push({ id: "accountingAccountNumber", fieldName: "accountingAccountNumber", label: "N° de compte", valueFonction: (element: AccountingRecord, column: SortTableColumn<AccountingRecord>) => { if (element && column && element.accountingAccount) return element.accountingAccount.principalAccountingAccount.code + element.accountingAccount.accountingAccountSubNumber; return "" } } as SortTableColumn<AccountingRecord>);
    this.displayedColumns.push({ id: "accountingAccountLabel", fieldName: "accountingAccountLabel", label: "Libellé du compte", valueFonction: (element: AccountingRecord, column: SortTableColumn<AccountingRecord>) => { if (element && column && element.accountingAccount) return element.accountingAccount.label; return "" }, isShrinkColumn: true } as SortTableColumn<AccountingRecord>);
    this.displayedColumns.push({ id: "accountingDocumentNumber", fieldName: "manualAccountingDocumentNumber", label: "N° de pièce justificative" } as SortTableColumn<AccountingRecord>);
    this.displayedColumns.push({ id: "accountingDocumentDate", fieldName: "manualAccountingDocumentDate", label: "Date pièce justificative", valueFonction: formatDateForSortTable } as SortTableColumn<AccountingRecord>);
    this.displayedColumns.push({ id: "manualAccountingDocumentDeadline", fieldName: "manualAccountingDocumentDeadline", label: "Date limite de paiement", valueFonction: formatDateForSortTable } as SortTableColumn<AccountingRecord>);
    this.displayedColumns.push({ id: "operationDateTime", fieldName: "operationDateTime", label: "Date de l'opération", valueFonction: formatDateForSortTable } as SortTableColumn<AccountingRecord>);
    this.displayedColumns.push({ id: "debitAmount", fieldName: "debitAmount", label: "Débit", valueFonction: formatEurosForSortTable } as SortTableColumn<AccountingRecord>);
    this.displayedColumns.push({ id: "creditAmount", fieldName: "creditAmount", label: "Crédit", valueFonction: formatEurosForSortTable } as SortTableColumn<AccountingRecord>);
    this.displayedColumns.push({ id: "label", fieldName: "label", label: "Libellé", isShrinkColumn: true } as SortTableColumn<AccountingRecord>);
    this.displayedColumns.push({ id: "debitAccumulation", fieldName: "debitAccumulation", label: "Cumul débit", valueFonction: formatEurosForSortTable } as SortTableColumn<AccountingRecord>);
    this.displayedColumns.push({ id: "creditAccumulation", fieldName: "creditAccumulation", label: "Cumul crédit", valueFonction: formatEurosForSortTable } as SortTableColumn<AccountingRecord>);
    this.displayedColumns.push({ id: "balance", fieldName: "balance", label: "Solde", valueFonction: formatEurosForSortTable } as SortTableColumn<AccountingRecord>);

    this.tableAction.push({
      actionIcon: "delete", actionName: "Supprimer l'opération", actionClick: (action: SortTableAction<AccountingRecord>, element: AccountingRecord, event: any) => {
        this.accountingRecords.splice(this.accountingRecords.indexOf(element), 1);
      }, display: true,
    } as SortTableAction<AccountingRecord>);

    this.saveObservableSubscription = this.appService.saveObservable.subscribe(response => {
      if (response)
        this.saveOperations();
    });

    if (url[1].path == "edit" && temporaryOperationId) {
      this.accountingRecordService.getAccountingRecordsByTemporaryOperationId(temporaryOperationId).subscribe(response => {
        this.accountingRecords = response;
      });
    }
  }

  ngOnDestroy() {
    this.saveObservableSubscription.unsubscribe();
  }

  addAccountingRecord() {
    if (this.accountingRecordForm.valid) {
      let newRecord = {} as AccountingRecord;
      if (this.accountingRecords && this.accountingRecords.length > 0)
        newRecord.accountingJournal = this.accountingRecords[this.accountingRecords.length - 1].accountingJournal;
      this.accountingRecords.push(newRecord);
      this.accountingRecord = newRecord;
      this.computeBalanceAndDebitAndCreditAccumulation();
      this.refreshTable.next();
    } else {
      this.appService.displaySnackBar("Veuillez remplir l'ensemble des champs avant d'ajouter une nouvelle opération", true, 15);
    }
  }

  selectAccountingRecord(element: AccountingRecord) {
    this.accountingRecord = element;
    this.computeBalanceAndDebitAndCreditAccumulation();
    this.refreshTable.next();
  }

  computeBalanceAndDebitAndCreditAccumulation() {
    if (this.accountingRecords) {
      let credit: number = 0;
      let debit: number = 0;
      let balance: number = 0;
      for (let accountingRecord of this.accountingRecords) {
        if (accountingRecord.creditAmount) {
          credit += parseFloat(accountingRecord.creditAmount + "");
          balance += parseFloat(accountingRecord.creditAmount + "");
        }
        if (accountingRecord.debitAmount) {
          debit += parseFloat(accountingRecord.debitAmount + "");
          balance -= parseFloat(accountingRecord.debitAmount + '');
        }
        accountingRecord.balance = balance;
        accountingRecord.debitAccumulation = debit;
        accountingRecord.creditAccumulation = credit;
      }
    }
  }

  getTotalBalance(): number {
    if (this.accountingRecords) {
      let balance: number = 0;
      for (let accountingRecord of this.accountingRecords) {
        if (accountingRecord.creditAmount) {
          balance += parseFloat(accountingRecord.creditAmount + "");
        } else if (accountingRecord.debitAmount) {
          balance -= parseFloat(accountingRecord.debitAmount + '');
        }
      }
      return balance;
    }
    return 0;
  }

  saveOperations() {
    if (this.accountingRecordForm.valid && this.accountingRecords && this.accountingRecords.length > 0) {
      let journalId = 0;

      for (let record of this.accountingRecords) {
        if (record.operationDateTime) {
          record.operationDateTime = new Date(record.operationDateTime);
          record.operationDateTime.setHours(12);
        }
        if (journalId > 0 && journalId != record.accountingJournal.id) {
          this.appService.displaySnackBar("Le journal doit être identique sur l'ensemble des lignes", true, 10);
          return;
        }
        journalId = record.accountingJournal.id;
      }

      if ((Math.round(this.getTotalBalance() * 100) / 100) == 0) {
        this.accountingRecordService.saveManualOperations(this.accountingRecords).subscribe(response => {
          if (response)
            this.location.back();
        });
      } else {
        this.appService.displaySnackBar("Veuillez à saisir un ensemble d'opérations équilibrées", true, 15);
      }
    } else {
      this.appService.displaySnackBar("Veuillez saisir au moins une opération valide", true, 15);
    }
  }

  setOperationDateInterval() {
    this.minDate.setFullYear(this.minDate.getFullYear() - 1);
    this.minDate.setMonth(0);
    this.minDate.setDate(1);
    this.maxDate.setDate(this.maxDate.getDate() + 1);
  }
}
