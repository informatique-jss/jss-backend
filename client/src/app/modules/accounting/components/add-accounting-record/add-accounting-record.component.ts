import { Location } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Subject } from 'rxjs';
import { formatDateForSortTable, formatEurosForSortTable } from 'src/app/libs/FormatHelper';
import { SortTableAction } from 'src/app/modules/miscellaneous/model/SortTableAction';
import { SortTableColumn } from 'src/app/modules/miscellaneous/model/SortTableColumn';
import { ConstantService } from 'src/app/modules/miscellaneous/services/constant.service';
import { AppService } from 'src/app/services/app.service';
import { AccountingJournal } from '../../model/AccountingJournal';
import { AccountingRecord } from '../../model/AccountingRecord';
import { AccountingRecordService } from '../../services/accounting.record.service';

@Component({
  selector: 'add-accounting-record',
  templateUrl: './add-accounting-record.component.html',
  styleUrls: ['./add-accounting-record.component.css']
})
export class AddAccountingRecordComponent implements OnInit {

  accountingRecords: AccountingRecord[] = new Array<AccountingRecord>;
  accountingRecord: AccountingRecord = {} as AccountingRecord;
  isEditing: boolean = false;

  constructor(private formBuilder: FormBuilder,
    private appService: AppService,
    private accountingRecordService: AccountingRecordService,
    private constantService: ConstantService,
    private location: Location,
  ) {
  }

  accountingRecordForm = this.formBuilder.group({});
  displayedColumns: SortTableColumn[] = [] as Array<SortTableColumn>;
  tableAction: SortTableAction[] = [] as Array<SortTableAction>;

  accountingJournalSales: AccountingJournal = this.constantService.getAccountingJournalSales();
  accountingJournalPurchases: AccountingJournal = this.constantService.getAccountingJournalPurchases();
  accountingJournalANouveau: AccountingJournal = this.constantService.getAccountingJournalANouveau();

  refreshTable: Subject<void> = new Subject<void>();

  ngOnInit() {
    this.addAccountingRecord();

    // Column init
    this.displayedColumns = [];
    this.displayedColumns.push({ id: "accountingJournal", fieldName: "accountingJournal", label: "Journal", valueFonction: (element: any, elements: any[], column: SortTableColumn, columns: SortTableColumn[]) => { if (element && column && element.accountingJournal) return element.accountingJournal.label; return "" } } as SortTableColumn);
    this.displayedColumns.push({ id: "accountingAccountNumber", fieldName: "accountingAccountNumber", label: "N° de compte", valueFonction: (element: any, elements: any[], column: SortTableColumn, columns: SortTableColumn[]) => { if (element && column && element.accountingAccount) return element.accountingAccount.principalAccountingAccount.code + "-" + element.accountingAccount.accountingAccountSubNumber; return "" } } as SortTableColumn);
    this.displayedColumns.push({ id: "accountingAccountLabel", fieldName: "accountingAccountLabel", label: "Libellé du compte", valueFonction: (element: any, elements: any[], column: SortTableColumn, columns: SortTableColumn[]) => { if (element && column && element.accountingAccount) return element.accountingAccount.label; return "" }, isShrinkColumn: true } as SortTableColumn);
    this.displayedColumns.push({ id: "accountingDocumentNumber", fieldName: "manualAccountingDocumentNumber", label: "N° de pièce justificative" } as SortTableColumn);
    this.displayedColumns.push({ id: "accountingDocumentDate", fieldName: "manualAccountingDocumentDate", label: "Date pièce justificative", valueFonction: formatDateForSortTable } as SortTableColumn);
    this.displayedColumns.push({ id: "manualAccountingDocumentDeadline", fieldName: "manualAccountingDocumentDeadline", label: "Date limite de paiement", valueFonction: formatDateForSortTable } as SortTableColumn);
    this.displayedColumns.push({ id: "debitAmount", fieldName: "debitAmount", label: "Débit", valueFonction: formatEurosForSortTable } as SortTableColumn);
    this.displayedColumns.push({ id: "creditAmount", fieldName: "creditAmount", label: "Crédit", valueFonction: formatEurosForSortTable } as SortTableColumn);
    this.displayedColumns.push({ id: "label", fieldName: "label", label: "Libellé", isShrinkColumn: true } as SortTableColumn);
    this.displayedColumns.push({ id: "debitAccumulation", fieldName: "debitAccumulation", label: "Cumul débit", valueFonction: formatEurosForSortTable } as SortTableColumn);
    this.displayedColumns.push({ id: "creditAccumulation", fieldName: "creditAccumulation", label: "Cumul crédit", valueFonction: formatEurosForSortTable } as SortTableColumn);
    this.displayedColumns.push({ id: "balance", fieldName: "balance", label: "Solde", valueFonction: formatEurosForSortTable } as SortTableColumn);

    this.tableAction.push({
      actionIcon: "delete", actionName: "Supprimer l'opération", actionClick: (action: SortTableAction, element: any) => {
        this.accountingRecords.splice(this.accountingRecords.indexOf(element), 1);
      }, display: true,
    } as SortTableAction);
  }

  addAccountingRecord() {
    if (this.accountingRecordForm.valid) {
      let newRecord = {} as AccountingRecord;
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
        accountingRecord.balance = Math.round(balance * 100) / 100;
        accountingRecord.debitAccumulation = Math.round(debit * 100) / 100;
        accountingRecord.creditAccumulation = Math.round(credit * 100) / 100;
      }
    }
  }

  getTotalBalance(): number {
    if (this.accountingRecords) {
      let balance: number = 0;
      for (let accountingRecord of this.accountingRecords) {
        if (accountingRecord.creditAmount) {
          balance += parseFloat(accountingRecord.creditAmount + "");
        }
        if (accountingRecord.debitAmount) {
          balance -= parseFloat(accountingRecord.debitAmount + '');
        }
      }
      return Math.round(balance * 100) / 100;
    }
    return 0;
  }

  saveOperations() {
    if (this.accountingRecordForm.valid && this.accountingRecords && this.accountingRecords.length > 0) {
      if (this.getTotalBalance() == 0) {
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
}
