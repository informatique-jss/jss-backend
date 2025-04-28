import { Component, OnInit, SimpleChanges } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { TypeDocument } from '../../model/guichet-unique/referentials/TypeDocument';
import { SelectMultiServiceTypeDialogComponent } from '../select-multi-service-type-dialog/select-multi-service-type-dialog.component';

@Component({
  selector: 'app-select-document-type-dialog',
  templateUrl: './select-document-type-dialog.component.html',
  styleUrls: ['./select-document-type-dialog.component.css']
})
export class SelectDocumentTypeDialogComponent implements OnInit {

  selectedDocumentType: TypeDocument | undefined;

  constructor(private formBuilder: FormBuilder,
    private dialog: MatDialog,
    public dialogRef: MatDialogRef<SelectMultiServiceTypeDialogComponent>
  ) { }

  documentTypeForm = this.formBuilder.group({});

  ngOnInit() {
  }

  ngOnChanges(changes: SimpleChanges) {
  }

  getFormStatus(): boolean {
    return this.documentTypeForm.valid;
  }

  validateDocumentType() {
    if (this.selectedDocumentType)
      this.dialogRef.close(this.selectedDocumentType);
  }

  closeDialog() {
    this.dialogRef.close(null);
  }

}
