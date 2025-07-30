import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { AppService } from 'src/app/services/app.service';
import { Voucher } from '../../model/Voucher';

@Component({
  selector: 'new-voucher-dialog',
  templateUrl: './new-voucher-dialog.component.html',
  styleUrls: ['./new-voucher-dialog.component.css']
})
export class NewVoucherDialogComponent implements OnInit {
  constructor(public dialogRef: MatDialogRef<NewVoucherDialogComponent>, private formBuilder: FormBuilder,
    private appService: AppService,
  ) { }

  voucher: Voucher = {} as Voucher;
  voucherForm = this.formBuilder.group({});

  ngOnInit() {

  }

  onConfirm(): void {
    if ((!this.voucher.discountAmount || this.voucher.discountAmount <= 0) && (!this.voucher.discountRate || this.voucher.discountRate <= 0)) {
      this.appService.displaySnackBar("Veuillez renseigner un montant ou un poucentage de réduction", true, 3000);
      return;
    }
    if (this.voucher.discountAmount && this.voucher.discountRate) {
      this.appService.displaySnackBar("Veuillez choisir entre un montant de réduction et un pourcentage", true, 3000);
      return;
    }
    if (!this.voucher.code || this.voucher.code.trim().length == 0) {
      this.appService.displaySnackBar("Veuillez renseigner un code d'utilisation du coupon", true, 3000);
      return;
    }
    else
      this.dialogRef.close(this.voucher);
  }

  onClose(): void {
    this.dialogRef.close(null);
  }

}
