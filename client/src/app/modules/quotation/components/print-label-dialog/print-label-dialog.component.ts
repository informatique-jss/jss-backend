import { Component, OnInit, SimpleChanges } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { AddAffaireDialogComponent } from '../add-affaire-dialog/add-affaire-dialog.component';

@Component({
  selector: 'print-label-dialog',
  templateUrl: './print-label-dialog.component.html',
  styleUrls: ['./print-label-dialog.component.css']
})
export class PrintLabelDialogComponent implements OnInit {

  customerOrders: string[] = [];

  constructor(private formBuilder: FormBuilder,
    private dialogRef: MatDialogRef<AddAffaireDialogComponent>
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
      this.dialogRef.close(this.customerOrders);
    }
  }

  closeDialog() {
    this.dialogRef.close(null);
  }
}
