import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { AccountingRecord } from '../../model/AccountingRecord';

@Component({
  selector: 'add-accounting-record',
  templateUrl: './add-accounting-record.component.html',
  styleUrls: ['./add-accounting-record.component.css']
})
export class AddAccountingRecordComponent implements OnInit {

  accountingRecords: AccountingRecord[] = new Array<AccountingRecord>;

  constructor(private formBuilder: FormBuilder) {
  }

  accountingRecordForm = this.formBuilder.group({});

  ngOnInit() {
    this.addAccountingRecord();
  }

  addAccountingRecord() {
    this.accountingRecords.push({} as AccountingRecord);
  }

  deleteAccountingRecord(accountingRecord: AccountingRecord) {
    if (this.accountingRecords)
      this.accountingRecords.splice(this.accountingRecords.indexOf(accountingRecord), 1);
  }

}
