import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { ConstantService } from 'src/app/modules/miscellaneous/services/constant.service';
import { Tiers } from 'src/app/modules/tiers/model/Tiers';
import { AppService } from '../../../../services/app.service';
import { Affaire } from '../../model/Affaire';

@Component({
  selector: 'refund-payment-dialog',
  templateUrl: './refund-payment-dialog.component.html',
  styleUrls: ['./refund-payment-dialog.component.css']
})
export class RefundPaymentDialogComponent implements OnInit {


  constructor(public dialogRef: MatDialogRef<RefundPaymentDialogComponent>,
    private constantService: ConstantService,
    private appService: AppService,
    private formBuilder: FormBuilder) { }

  affaire: Affaire | undefined;
  tiers: Tiers | undefined;

  title: string = "Rembourser le paiement";
  text: string = "Merci d'indiquer le tiers et éventuellement l'affaire à rembourser : ";

  refundPaymentForm = this.formBuilder.group({
  });

  ngOnInit() {
  }

  onConfirm(): void {
    if (this.tiers)
      this.dialogRef.close({ "tiers": this.tiers, "affaire": this.affaire });
    else
      this.appService.displaySnackBar("Merci de choisir un tiers", true, 10);
  }

  onClose(): void {
    this.dialogRef.close(null);
  }
}
