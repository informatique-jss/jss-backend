import { Component, OnInit, SimpleChanges } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { AttachmentType } from 'src/app/modules/miscellaneous/model/AttachmentType';
import { SelectMultiServiceTypeDialogComponent } from '../select-multi-service-type-dialog/select-multi-service-type-dialog.component';

@Component({
  selector: 'app-select-attachment-type-dialog',
  templateUrl: './select-attachment-type-dialog.component.html',
  styleUrls: ['./select-attachment-type-dialog.component.css']
})
export class SelectAttachmentTypeDialogComponent implements OnInit {

  selectedAttachmentType: AttachmentType | undefined;

  constructor(private formBuilder: FormBuilder,
    private dialog: MatDialog,
    public dialogRef: MatDialogRef<SelectMultiServiceTypeDialogComponent>
  ) { }

  attachmentTypeForm = this.formBuilder.group({});

  ngOnInit() {
  }

  ngOnChanges(changes: SimpleChanges) {
  }

  getFormStatus(): boolean {
    return this.attachmentTypeForm.valid;
  }

  validateDocumentType() {
    if (this.selectedAttachmentType)
      this.dialogRef.close(this.selectedAttachmentType);
  }

  closeDialog() {
    this.dialogRef.close(null);
  }

}
