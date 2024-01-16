import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { QuotationAbandonReason } from 'src/app/modules/miscellaneous/model/QuotationAbandonReason';

@Component({
  selector: 'app-abandon-reason-inquiry-dialog',
  templateUrl: './abandon-reason-dialog.html',
  styleUrls: ['./abandon-reason-dialog.css'],
})

export class AbandonReasonDialog implements OnInit {
  reasonTypeForm = this.formBuilder.group({});
  abandonReason: QuotationAbandonReason | undefined;

  constructor(
    public dialogRef: MatDialogRef<AbandonReasonDialog>,
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
