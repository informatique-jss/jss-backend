import { Component, OnInit, SimpleChanges } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { CompetentAuthority } from '../../../miscellaneous/model/CompetentAuthority';
import { AddAffaireDialogComponent } from '../add-affaire-dialog/add-affaire-dialog.component';

@Component({
  selector: 'choose-competent-authority-dialog',
  templateUrl: './choose-competent-authority-dialog.component.html',
  styleUrls: ['./choose-competent-authority-dialog.component.css']
})
export class ChooseCompetentAuthorityDialogComponent implements OnInit {

  competentAuthority: CompetentAuthority | undefined;
  label: string = "";
  title: string = "";

  constructor(private formBuilder: FormBuilder,
    private dialogRef: MatDialogRef<AddAffaireDialogComponent>
  ) { }

  ngOnInit() {
  }

  ngOnChanges(changes: SimpleChanges) {
  }

  competentAuthorityForm = this.formBuilder.group({
  });

  getFormStatus(): boolean {
    return this.competentAuthorityForm.valid;
  }

  chooseCompetentAuthority() {
    if (this.competentAuthority) {
      this.dialogRef.close(this.competentAuthority);
    }
  }

  closeDialog() {
    this.dialogRef.close(null);
  }
}
