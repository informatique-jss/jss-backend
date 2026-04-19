import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { AppService } from 'src/app/services/app.service';
import { Voucher } from '../../model/Voucher';
import { VoucherService } from '../../services/voucher.service';

@Component({
    selector: 'new-voucher-dialog',
    templateUrl: './new-voucher-dialog.component.html',
    styleUrls: ['./new-voucher-dialog.component.css'],
    standalone: false
})
export class NewVoucherDialogComponent implements OnInit {
  constructor(public dialogRef: MatDialogRef<NewVoucherDialogComponent>, private formBuilder: FormBuilder,
    private appService: AppService,
    private voucherService: VoucherService
  ) { }

  voucher: Voucher = {} as Voucher;
  voucherForm = this.formBuilder.group({});
  minDate: Date = new Date();
  maxDate: Date = new Date();

  ngOnInit() {
    this.maxDate.setDate(this.maxDate.getDate() + 365);
  }

  onConfirm(): void {
    if (this.voucher.code) {
      this.voucherService.getVoucherByCode(this.voucher.code.toUpperCase()).subscribe(response => {
        if (response) {
          this.appService.displaySnackBar("Ce code existe déjà", true, 3000);
          return;
        }
      });
    }
    if (!this.voucher.startDate || !this.voucher.endDate) {
      this.appService.displaySnackBar("Veuillez remplir les champs dates", true, 3000);
      return;
    }
    if (this.voucher.startDate >= this.voucher.endDate) {
      this.appService.displaySnackBar("Veuillez renseigner une date de début antérieure à la date de fin", true, 3000);
      return;
    }
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
    if (this.voucher.totalLimit && this.voucher.perUserLimit && this.voucher.perUserLimit > this.voucher.totalLimit) {
      this.appService.displaySnackBar("Veuillez modifier le nombre d'utilisation total", true, 3000);
      return;
    }
    if (this.voucher.discountRate && (this.voucher.discountRate < 0 || this.voucher.discountRate > 100)) {
      this.appService.displaySnackBar("Veuillez corriger le pourcentage de réduction saisi", true, 3000);
      return;
    }
    else
      this.dialogRef.close(this.voucher);
  }

  onClose(): void {
    this.dialogRef.close(null);
  }

}
