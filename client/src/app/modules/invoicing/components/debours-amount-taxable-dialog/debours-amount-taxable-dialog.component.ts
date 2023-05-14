import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';

@Component({
  selector: 'debours-amount-taxable-dialog',
  templateUrl: './debours-amount-taxable-dialog.component.html',
  styleUrls: ['./debours-amount-taxable-dialog.component.css']
})
export class DeboursAmountTaxableDialogComponent implements OnInit {

  constructor(public dialogRef: MatDialogRef<DeboursAmountTaxableDialogComponent>, private formBuilder: FormBuilder) { }

  nonTaxableAmount: number = 0;
  maxAmount: number = 0;

  amountForm = this.formBuilder.group({});

  ngOnInit() {
  }

  onConfirm(): void {
    this.dialogRef.close(this.nonTaxableAmount ? this.nonTaxableAmount : 0);
  }

  onClose(): void {
    this.dialogRef.close(null);
  }

  valueModified() {
    if (this.nonTaxableAmount > this.maxAmount)
      this.nonTaxableAmount = this.maxAmount;
  }

}
