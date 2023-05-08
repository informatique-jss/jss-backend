import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { AzureReceiptInvoice } from '../../model/AzureReceiptInvoice';
import { AzureReceiptInvoiceService } from '../../services/azure.receipt.invoice.service';

@Component({
  selector: 'app-receipt-reconciliation-edit-dialog',
  templateUrl: './receipt-reconciliation-edit-dialog.component.html',
  styleUrls: ['./receipt-reconciliation-edit-dialog.component.css']
})
export class ReceiptReconciliationEditDialogComponent implements OnInit {

  constructor(
    public dialogRef: MatDialogRef<ReceiptReconciliationEditDialogComponent>,
    private formBuilder: FormBuilder,
    private azureReceiptInvoiceService: AzureReceiptInvoiceService,
  ) { }

  azureReceiptInvoice: AzureReceiptInvoice | undefined;

  invoiceForm = this.formBuilder.group({});

  ngOnInit() {
  }

  onConfirm(): void {
    if (this.azureReceiptInvoice && this.invoiceForm.valid) {
      this.azureReceiptInvoiceService.addOrUpdateAzureReceiptInvoice(this.azureReceiptInvoice).subscribe(response => {
        this.dialogRef.close(true);
      })
    }
  }

  onClose(): void {
    this.dialogRef.close(false);
  }
}
