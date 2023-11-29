import { Component, Inject, Input, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';

import { AbandonReason } from 'src/app/modules/miscellaneous/model/AbandonReason';
import { AbandonReasonService } from 'src/app/modules/miscellaneous/services/abandon.reason.service';
@Component({
  selector: 'app-abandon-reason-inquiry-dialog',
  templateUrl: './abandon-reason-inquiry-dialog.html',
  styleUrls: ['./abandon-reason-inquiry-dialog.css'],
})

export class AbandonReasonInquiryDialog implements OnInit {
  @Input() reason: AbandonReason = {} as AbandonReason;

  reasonTypeForm = this.formBuilder.group({
    id: ['', Validators.required],
    label: ['', [Validators.required]],
  });

  constructor(
    public dialogRef: MatDialogRef<AbandonReasonInquiryDialog>,
    private formBuilder: FormBuilder,
    @Inject(MAT_DIALOG_DATA) public data: any
  ) {}

  ngOnInit() {}

  onConfirm(): void {
    this.dialogRef.close({ abandonReason: this.reasonTypeForm.value });
  }

  onClose(): void {
    this.dialogRef.close();
  }
}
