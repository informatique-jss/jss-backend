import { Component, OnInit, SimpleChanges } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { AppService } from 'src/app/services/app.service';
import { AttachmentTypeMailQuery } from '../../model/AttachmentTypeMailQuery';
import { AddAffaireDialogComponent } from '../add-affaire-dialog/add-affaire-dialog.component';

@Component({
  selector: 'app-select-attachment-type-dialog',
  templateUrl: './select-attachment-type-dialog.component.html',
  styleUrls: ['./select-attachment-type-dialog.component.css']
})
export class SelectAttachmentTypeDialogComponent implements OnInit {

  query: AttachmentTypeMailQuery = {} as AttachmentTypeMailQuery;

  constructor(private formBuilder: FormBuilder,
    private appService: AppService,
    private dialogRef: MatDialogRef<AddAffaireDialogComponent>
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
