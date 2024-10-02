import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { CompetentAuthority } from 'src/app/modules/miscellaneous/model/CompetentAuthority';

@Component({
  selector: 'select-competent-authority-dialog',
  templateUrl: './select-competent-authority-dialog.component.html',
  styleUrls: ['./select-competent-authority-dialog.component.css']
})
export class SelectCompetentAuthorityDialogComponent implements OnInit {

  constructor(public dialogRef: MatDialogRef<SelectCompetentAuthorityDialogComponent>,
    private formBuilder: FormBuilder) { }

  choosedCompetentAuthority: CompetentAuthority | undefined;
  title: string = "Autorité compétente";
  text: string = "Merci d'indiquer l'autorité compétente de l'affaire associée à cette prestation : ";

  competentAuthorityForm = this.formBuilder.group({
  });

  ngOnInit() {
  }

  onConfirm(): void {
    this.dialogRef.close(this.choosedCompetentAuthority);
  }

  onClose(): void {
    this.dialogRef.close(null);
  }
}
