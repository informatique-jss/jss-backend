import { Component, Input, OnInit, SimpleChanges } from '@angular/core';
import { FormBuilder, Validators, FormGroup, FormControl } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { AbandonReasonService } from '../../../miscellaneous/services/abandon.reason.service';
import { SelectAbandonReasonComponent } from 'src/app/modules/miscellaneous/components/forms/select-abandon-reason/select-abandon-reason';
import { UserNoteService } from 'src/app/services/user.notes.service';
import { IAbandonReason } from 'src/app/modules/miscellaneous/model/AbandonReason';

@Component({
  selector: 'app-abandon-reason-inquiry-dialog',
  templateUrl: './abandon-reason-inquiry-dialog.html',
  styleUrls: ['./abandon-reason-inquiry-dialog.css'],
})

export class AbandonReasonInquiryDialog implements OnInit {
  @Input() reason: IAbandonReason = {} as IAbandonReason;

  attachmentTypeForm: FormGroup | undefined;

  constructor(public dialogRef: MatDialogRef<AbandonReasonInquiryDialog>,
    private formBuilder: FormBuilder,
    private abandonReasonService: AbandonReasonService,
    private userNoteService2: UserNoteService,
    ) {}

  ngOnInit() {

    const selectComponent = new SelectAbandonReasonComponent(
      this.formBuilder,
      this.abandonReasonService,
      this.userNoteService2,
    );
    selectComponent.initTypes();
  }

  onConfirm(): void {
    this.dialogRef.close(this.reason);
  }

  onClose(): void {
    this.dialogRef.close(this.reason);
  }
}

