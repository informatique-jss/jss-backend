import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';

@Component({
  selector: 'amount-dialog',
  templateUrl: './amount-dialog.component.html',
  styleUrls: ['./amount-dialog.component.css']
})
export class AmountDialogComponent implements OnInit {

  constructor(public dialogRef: MatDialogRef<AmountDialogComponent>, private formBuilder: FormBuilder) { }

  label: string = "";
  maxAmount: number = 0;
  amount: number = 0;
  title: string = "Montant à utiliser";

  amountForm = this.formBuilder.group({});

  ngOnInit() {
    this.amount = this.maxAmount;
  }

  onConfirm(): void {
    this.dialogRef.close(this.amount);
  }

  onClose(): void {
    this.dialogRef.close(null);
  }

  valueModified() {
    if (this.maxAmount > 0 && (this.amount > this.maxAmount || this.amount < 0))
      this.amount = this.maxAmount;
    if (this.maxAmount < 0 && (this.amount > 0 || this.amount < this.maxAmount))
      this.amount = this.maxAmount;
  }
}
