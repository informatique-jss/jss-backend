import { Component, OnInit, SimpleChanges } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { AppService } from 'src/app/services/app.service';
import { MissingAttachmentQuery } from '../../model/MissingAttachmentQuery';
import { SelectAttachmentsDialogComponent } from '../select-attachments-dialog/select-attachment-dialog.component';

@Component({
  selector: 'app-select-attachment-type-dialog',
  templateUrl: './select-attachment-type-dialog.component.html',
  styleUrls: ['./select-attachment-type-dialog.component.css']
})
export class SelectAttachmentTypeDialogComponent implements OnInit {

  query: MissingAttachmentQuery = {} as MissingAttachmentQuery;

  constructor(private formBuilder: FormBuilder,
    private appService: AppService,
    private dialog: MatDialog,
    public dialogRef: MatDialogRef<SelectAttachmentsDialogComponent>
  ) { }

  ngOnInit() {
  }

  ngOnChanges(changes: SimpleChanges) {
  }

  attachmentTypeForm = this.formBuilder.group({
  });

  getFormStatus(): boolean {
    return this.attachmentTypeForm.valid;
  }

  generateMail() {
    this.dialogRef.close(this.query);
  }

  closeDialog() {
    this.dialogRef.close(null);
  }
}
