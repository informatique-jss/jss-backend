import { Component, OnInit, SimpleChanges } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatLegacyDialogRef as MatDialogRef } from '@angular/material/legacy-dialog';
import { CustomerOrderService } from '../../services/customer.order.service';
import { AddAffaireDialogComponent } from '../add-affaire-dialog/add-affaire-dialog.component';

@Component({
  selector: 'print-label-dialog',
  templateUrl: './print-label-dialog.component.html',
  styleUrls: ['./print-label-dialog.component.css']
})
export class PrintLabelDialogComponent implements OnInit {

  customerOrders: string[] = [];
  printLabel: boolean = true;
  printLetters: boolean = false;

  constructor(private formBuilder: FormBuilder,
    private dialogRef: MatDialogRef<AddAffaireDialogComponent>,
    private customerOrderService: CustomerOrderService
  ) { }

  ngOnInit() {
  }

  ngOnChanges(changes: SimpleChanges) {
  }

  printLabelForm = this.formBuilder.group({
  });

  getFormStatus(): boolean {
    return this.printLabelForm.valid;
  }

  generateLabel() {
    if (this.customerOrders && this.customerOrders.length > 0) {
      if (this.printLetters) {
        this.customerOrderService.generateMailingLabelDownload(this.customerOrders, this.printLabel, this.printLetters);
        this.dialogRef.close(this.customerOrders);
      }
      else
        this.customerOrderService.generateMailingLabel(this.customerOrders, this.printLabel, this.printLetters).subscribe(response => {
          this.dialogRef.close(this.customerOrders);
        });
    }
  }

  closeDialog() {
    this.dialogRef.close(null);
  }
}
