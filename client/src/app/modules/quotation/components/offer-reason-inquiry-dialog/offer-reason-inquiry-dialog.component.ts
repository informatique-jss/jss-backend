import { Component, Inject, Input, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';

import { OfferReason } from 'src/app/modules/miscellaneous/model/OfferReason';
import { OfferReasonService } from 'src/app/modules/miscellaneous/services/offer.reason.service';
@Component({
  selector: 'app-offer-reason-inquiry-dialog',
  templateUrl: './offer-reason-inquiry-dialog.component.html',
  styleUrls: ['./offer-reason-inquiry-dialog.component.css'],
})

export class OfferReasonInquiryDialog implements OnInit {
  @Input() reason: OfferReason = {} as OfferReason;

  reasonTypeForm = this.formBuilder.group({
    id: ['', Validators.required],
    label: ['', [Validators.required]],
  });

  constructor(
    public dialogRef: MatDialogRef<OfferReasonInquiryDialog>,
    private formBuilder: FormBuilder,
    @Inject(MAT_DIALOG_DATA) public data: any
  ) {}

  ngOnInit() {}

  onConfirm(): void {
    this.dialogRef.close({ offerReason: this.reasonTypeForm.value });
  }

  onClose(): void {
    this.dialogRef.close();
  }
}
