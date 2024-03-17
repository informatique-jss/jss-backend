import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { QuotationAbandonReason } from 'src/app/modules/miscellaneous/model/QuotationAbandonReason';

@Component({
  selector: 'quotation-abandon-reason-dialog',
  templateUrl: './quotation-abandon-reason-dialog.html',
  styleUrls: ['./quotation-abandon-reason-dialog.css'],
})

export class QuotationAbandonReasonDialog implements OnInit {
  reasonTypeForm = this.formBuilder.group({});
  abandonReason: QuotationAbandonReason | undefined;

  constructor(
    public dialogRef: MatDialogRef<QuotationAbandonReasonDialog>,
    private formBuilder: FormBuilder,
  ) { }

  ngOnInit() { }

  onConfirm(): void {
    this.dialogRef.close(this.abandonReason);
  }

  onClose(): void {
    this.dialogRef.close();
  }
}
