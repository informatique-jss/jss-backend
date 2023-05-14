import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';

@Component({
  selector: 'app-debours-amount-invoiced-dialog',
  templateUrl: './debours-amount-invoiced-dialog.component.html',
  styleUrls: ['./debours-amount-invoiced-dialog.component.css']
})
export class DeboursAmountInvoicedDialogComponent implements OnInit {

  constructor(public dialogRef: MatDialogRef<DeboursAmountInvoicedDialogComponent>, private formBuilder: FormBuilder) { }

  invoicedAmount: number = 0;

  amountForm = this.formBuilder.group({});

  ngOnInit() {
  }

  onConfirm(): void {
    this.dialogRef.close(this.invoicedAmount ? this.invoicedAmount : 0);
  }

  onClose(): void {
    this.dialogRef.close(null);
  }


}
