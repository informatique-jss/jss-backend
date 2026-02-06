import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { formatEurosForSortTable } from 'src/app/libs/FormatHelper';
import { SortTableColumn } from 'src/app/modules/miscellaneous/model/SortTableColumn';
import { AccountingRecord } from '../../model/AccountingRecord';
import { AccountingRecordService } from '../../services/accounting.record.service';

@Component({
  selector: 'payslip-list',
  templateUrl: './payslip-list.component.html',
  styleUrls: ['./payslip-list.component.css']
})
export class PayslipListComponent implements OnInit {
  fnpForm = this.formBuilder.group({});
  displayedColumns: SortTableColumn<AccountingRecord>[] = [];
  accountingDate: Date = new Date();
  records: AccountingRecord[] = [];

  constructor(private accountingRecordService: AccountingRecordService,
    private formBuilder: FormBuilder,
  ) { }

  ngOnInit() {
    this.displayedColumns = [];
    this.displayedColumns.push({ id: "principalAccountingAccount", fieldName: "accountingAccount.principalAccountingAccount.code", label: "Compte principal" } as SortTableColumn<AccountingRecord>);
    this.displayedColumns.push({ id: "accountingAccountSubNumber", fieldName: "accountingAccount.accountingAccountSubNumber", label: "Compte" } as SortTableColumn<AccountingRecord>);
    this.displayedColumns.push({ id: "creditAmount", fieldName: "creditAmount", label: "Crédit", valueFonction: formatEurosForSortTable } as SortTableColumn<AccountingRecord>);
    this.displayedColumns.push({ id: "debitAmount", fieldName: "debitAmount", label: "Débit", valueFonction: formatEurosForSortTable } as SortTableColumn<AccountingRecord>);

    this.refresh();
  }

  refresh() {
    if (this.accountingDate) {
      this.accountingDate = new Date(this.accountingDate.setHours(12));
      this.accountingRecordService.getPayslipRecords(this.accountingDate).subscribe(response => this.records = response)
    }
  }

}

