import { Component, OnInit, SimpleChanges } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { InvoiceLabelResult } from '../../../../../../../client-myjss/src/app/modules/my-account/model/InvoiceLabelResult';
import { CompetentAuthority } from '../../../miscellaneous/model/CompetentAuthority';
import { CustomerOrderService } from '../../services/customer.order.service';
import { AddAffaireDialogComponent } from '../add-affaire-dialog/add-affaire-dialog.component';

@Component({
  selector: 'app-print-registered-label-dialog',
  templateUrl: './print-registered-label-dialog.component.html',
  styleUrls: ['./print-registered-label-dialog.component.css']
})
export class PrintRegisteredLabelDialogComponent implements OnInit {

  competentAuthority: CompetentAuthority = {} as CompetentAuthority;
  invoiceLabelResult: InvoiceLabelResult = {} as InvoiceLabelResult;
  customerOrderId: number | undefined;

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
    if (this.customerOrderId) {
      this.customerOrderService.generateRegisteredLabel(this.customerOrderId).subscribe(response => {
        this.dialogRef.close();
      });
    }
  }
  closeDialog() {
    this.dialogRef.close(null);
  }
}
