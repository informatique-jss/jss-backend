import { Component, OnInit, SimpleChanges } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { PaperSetType } from 'src/app/modules/miscellaneous/model/PaperSetType';
import { SelectServiceTypeDialogComponent } from '../select-service-type-dialog/select-service-type-dialog.component';

@Component({
  selector: 'app-select-paper-set-type-dialog',
  templateUrl: './select-paper-set-type-dialog.component.html',
  styleUrls: ['./select-paper-set-type-dialog.component.css']
})
export class SelectPaperSetTypeDialogComponent implements OnInit {
  selectedPaperSetType: PaperSetType | undefined;
  excludedPaperSetTypes: PaperSetType[] = [];

  constructor(private formBuilder: FormBuilder,
    private dialog: MatDialog,
    public dialogRef: MatDialogRef<SelectServiceTypeDialogComponent>
  ) { }

  serviceTypeForm = this.formBuilder.group({});

  ngOnInit() {
  }

  ngOnChanges(changes: SimpleChanges) {
  }

  getFormStatus(): boolean {
    return this.serviceTypeForm.valid;
  }

  validatePaperSetType() {
    if (this.selectedPaperSetType)
      this.dialogRef.close(this.selectedPaperSetType);
  }

  closeDialog() {
    this.dialogRef.close(null);
  }

}
