import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';

@Component({
  selector: 'edit-amount-invoice-item-dialog',
  templateUrl: './edit-amount-invoice-item-dialog.component.html',
  styleUrls: ['./edit-amount-invoice-item-dialog.component.css']
})
export class EditAmountInvoiceItemDialogComponent implements OnInit {

  constructor(public dialogRef: MatDialogRef<EditAmountInvoiceItemDialogComponent>, private formBuilder: FormBuilder) { }

  label: string = "";
  amount: number = 0;
  title: string = "Montant à refacturer";

  amountForm = this.formBuilder.group({});

  ngOnInit() {
  }

  onConfirm(): void {
    this.dialogRef.close(this.amount);
  }

  onClose(): void {
    this.dialogRef.close(null);
  }

}
