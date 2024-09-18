import { Component, OnInit, SimpleChanges } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { PaperSetType } from 'src/app/modules/miscellaneous/model/PaperSetType';
import { PaperSet } from '../../model/PaperSet';

@Component({
  selector: 'app-select-paper-set-type-dialog',
  templateUrl: './select-paper-set-type-dialog.component.html',
  styleUrls: ['./select-paper-set-type-dialog.component.css']
})
export class SelectPaperSetTypeDialogComponent implements OnInit {
  selectedPaperSetType: PaperSetType | undefined;
  excludedPaperSetTypes: PaperSetType[] = [];
  newPaperSet: PaperSet = {} as PaperSet;

  constructor(private formBuilder: FormBuilder,
    public dialogRef: MatDialogRef<SelectPaperSetTypeDialogComponent>
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
    if (this.selectedPaperSetType) {
      this.newPaperSet.paperSetType = this.selectedPaperSetType;
      this.dialogRef.close(this.newPaperSet);
    }
  }

  closeDialog() {
    this.dialogRef.close(null);
  }

}
