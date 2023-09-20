import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { ConstantService } from 'src/app/modules/miscellaneous/services/constant.service';
import { AppService } from 'src/app/services/app.service';
import { CompetentAuthority } from '../../../miscellaneous/model/CompetentAuthority';

@Component({
  selector: 'app-select-competent-authority-dialog',
  templateUrl: './select-competent-authority-dialog.component.html',
  styleUrls: ['./select-competent-authority-dialog.component.css']
})
export class SelectCompetentAuthorityDialogComponent implements OnInit {

  constructor(public dialogRef: MatDialogRef<SelectCompetentAuthorityDialogComponent>,
    private constantService: ConstantService,
    private appService: AppService,
    private formBuilder: FormBuilder) { }

  competentAuthority: CompetentAuthority | undefined;
  title: string = "Mettre le paiement en compte";
  text: string = "Merci d'indiquer l'autorité compétente destinatrice de ce paiement : ";

  accountPaymentForm = this.formBuilder.group({
  });

  ngOnInit() {
  }

  onConfirm(): void {
    if (this.competentAuthority)
      this.dialogRef.close(this.competentAuthority);
    else
      this.appService.displaySnackBar("Merci de choisir une autorité compétente", true, 10);
  }

  onClose(): void {
    this.dialogRef.close(null);
  }

}
