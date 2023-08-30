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
    private abandonReasonService: AbandonReasonService,
    @Inject(MAT_DIALOG_DATA) public data: any,

    ) {}

  ngOnInit() {
  }


  onConfirm(): void {
    const selectedReasonControl = this.reasonTypeForm.value.label;
    Object.assign(this.reason, selectedReasonControl);
        this.abandonReasonService.addOrUpdateCustomerOrderAbandonReason(this.reason, this.data.id_quotation).subscribe(response => {
        this.dialogRef.close(true);
      });

  }


  onClose(): void {
    this.dialogRef.close();
  }
}
