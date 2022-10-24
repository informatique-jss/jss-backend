import { Component, OnInit } from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';
import { formatDateForSortTable, formatDateTimeForSortTable, formatEurosForSortTable } from 'src/app/libs/FormatHelper';
import { SortTableAction } from 'src/app/modules/miscellaneous/model/SortTableAction';
import { SortTableColumn } from 'src/app/modules/miscellaneous/model/SortTableColumn';
import { AccountingRecord } from '../../model/AccountingRecord';
import { AccountingRecordService } from '../../services/accounting.record.service';

@Component({
  selector: 'delete-accounting-record-dialog',
  templateUrl: './delete-accounting-record-dialog.component.html',
  styleUrls: ['./delete-accounting-record-dialog.component.css']
})
export class DeleteAccountingRecordDialogComponent implements OnInit {

  temporaryOperationId: number | undefined;
  operationId: number | undefined;
  accountingRecords: AccountingRecord[] | undefined;

  displayedColumns: SortTableColumn[] = [] as Array<SortTableColumn>;
  tableAction: SortTableAction[] = [] as Array<SortTableAction>;
  formatDateForSortTable: any;
  formatDateTimeForSortTable: any;
  formatEurosForSortTable: any;

  constructor(private accountingRecordService: AccountingRecordService,
    public dialogRef: MatDialogRef<DeleteAccountingRecordDialogComponent>) { }

  ngOnInit() {
    this.displayedColumns = [];
    this.displayedColumns.push({ id: "operationId", fieldName: "operationId", label: "N° d'écriture" } as SortTableColumn);
    this.displayedColumns.push({ id: "accountingDateTime", fieldName: "accountingDateTime", label: "Date d'écriture", valueFonction: formatDateForSortTable } as SortTableColumn);
    this.displayedColumns.push({ id: "operationDateTime", fieldName: "operationDateTime", label: "Date d'opération", valueFonction: formatDateTimeForSortTable } as SortTableColumn);
    this.displayedColumns.push({ id: "accountingJournal", fieldName: "accountingJournal", label: "Journal", valueFonction: (element: any, elements: any[], column: SortTableColumn, columns: SortTableColumn[]) => { if (element && column) return element.accountingJournal.label; return "" } } as SortTableColumn);
    this.displayedColumns.push({ id: "accountingAccountNumber", fieldName: "accountingAccountNumber", label: "N° de compte", valueFonction: (element: any, elements: any[], column: SortTableColumn, columns: SortTableColumn[]) => { if (element && column) return element.accountingAccount.accountingAccountNumber + "-" + element.accountingAccount.accountingAccountSubNumber; return "" } } as SortTableColumn);
    this.displayedColumns.push({ id: "accountingAccountLabel", fieldName: "accountingAccountLabel", label: "Libellé du compte", valueFonction: (element: any, elements: any[], column: SortTableColumn, columns: SortTableColumn[]) => { if (element && column) return element.accountingAccount.label; return "" }, isShrinkColumn: true } as SortTableColumn);
    this.displayedColumns.push({ id: "accountingDocumentNumber", fieldName: "accountingDocumentNumber", label: "N° de pièce justificative" } as SortTableColumn);
    this.displayedColumns.push({ id: "accountingDocumentDate", fieldName: "accountingDocumentDate", label: "Date pièce justificative", valueFonction: formatDateForSortTable } as SortTableColumn);
    this.displayedColumns.push({ id: "debitAmount", fieldName: "debitAmount", label: "Débit", valueFonction: formatEurosForSortTable } as SortTableColumn);
    this.displayedColumns.push({ id: "creditAmount", fieldName: "creditAmount", label: "Crédit", valueFonction: formatEurosForSortTable } as SortTableColumn);
    this.displayedColumns.push({ id: "letteringNumber", fieldName: "letteringNumber", label: "Lettrage" } as SortTableColumn);
    this.displayedColumns.push({ id: "letteringDate", fieldName: "letteringDateTime", label: "Date de lettrage", valueFonction: formatDateForSortTable } as SortTableColumn);
    this.displayedColumns.push({ id: "invoice", fieldName: "invoice.id", label: "Facture" } as SortTableColumn);

    if (this.temporaryOperationId)
      this.accountingRecordService.getAccountingRecordsByTemporaryOperationId(this.temporaryOperationId).subscribe(response => {
        this.accountingRecords = response;
      });
    if (this.operationId)
      this.accountingRecordService.getAccountingRecordsByOperationId(this.operationId).subscribe(response => {
        this.accountingRecords = response;
      });
  }

  onConfirm(): void {
    if (this.temporaryOperationId) {
      this.accountingRecordService.deleteRecordsByTemporaryOperationId(this.temporaryOperationId).subscribe(response => {
        this.dialogRef.close();
      })
    } else if (this.operationId) {
      this.accountingRecordService.doCounterPartByOperationId(this.operationId).subscribe(response => {
        this.dialogRef.close();
      })
    } else {
      this.dialogRef.close();
    }
  }

  onClose(): void {
    this.dialogRef.close(null);
  }

  removedLetteringInvoices(): number[] {
    let invoiceList = [] as Array<number>;
    if (this.accountingRecords)
      for (let accountingRecord of this.accountingRecords)
        if (accountingRecord.letteringDateTime && accountingRecord.invoice && invoiceList.indexOf(accountingRecord.invoice.id) < 0)
          invoiceList.push(accountingRecord.invoice.id);
    return invoiceList;
  }
}
