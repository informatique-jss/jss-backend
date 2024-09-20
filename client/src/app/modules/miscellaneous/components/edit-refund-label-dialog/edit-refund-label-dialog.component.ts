import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';

@Component({
  selector: 'app-edit-refund-label-dialog',
  templateUrl: './edit-refund-label-dialog.component.html',
  styleUrls: ['./edit-refund-label-dialog.component.css']
})
export class EditRefundLabelDialogComponent implements OnInit {

  refundLabel: string = "";

  constructor(
    public dialogRef: MatDialogRef<EditRefundLabelDialogComponent>,
    private formBuilder: FormBuilder,
    @Inject(MAT_DIALOG_DATA) public data: any
  ) {
  }

  editRefundLabelForm = this.formBuilder.group({});

  ngOnInit() {
    //  if (this.data.refundLabel)
    // this.refundLabel = this.data.refundLabel;
  }
  onConfirm() {
    if (this.editRefundLabelForm.valid)
      this.dialogRef.close(this.refundLabel);
  }

  onClose() {
    this.dialogRef.close(null);
  }
}
