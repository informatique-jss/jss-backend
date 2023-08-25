import { Component, Input, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { AbandonReasonService } from '../../../miscellaneous/services/abandon.reason.service';
import { UserNoteService } from 'src/app/services/user.notes.service';
import { IAbandonReason } from 'src/app/modules/miscellaneous/model/AbandonReason';

@Component({
  selector: 'app-abandon-reason-inquiry-dialog',
  templateUrl: './abandon-reason-inquiry-dialog.html',
  styleUrls: ['./abandon-reason-inquiry-dialog.css'],
})

export class AbandonReasonInquiryDialog implements OnInit {
  @Input() reason: IAbandonReason = {} as IAbandonReason;

  reasonTypeForm!: FormGroup;

  constructor(
    public dialogRef: MatDialogRef<AbandonReasonInquiryDialog>,
    private formBuilder: FormBuilder,

    ) {}

  ngOnInit() {

  }


  onConfirm(): void {
    this.dialogRef.close(this.reason);
  }

  onClose(): void {
    this.dialogRef.close(this.reason);
  }
}
