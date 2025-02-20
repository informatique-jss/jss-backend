import { Component, OnInit, SimpleChanges } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { PRINT_LABEL_RECIPIENT_AC, PRINT_LABEL_RECIPIENT_CUSTOMER } from 'src/app/libs/Constants';
import { CompetentAuthority } from 'src/app/modules/miscellaneous/model/CompetentAuthority';
import { AppService } from 'src/app/services/app.service';
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
  isForClient: boolean = true;
  printRegisteredLetter: boolean = false;
  selectedCompetentAuthority: CompetentAuthority | undefined;
  selectedRecipient: string = PRINT_LABEL_RECIPIENT_CUSTOMER;
  competentAuthorityRecipient = PRINT_LABEL_RECIPIENT_AC;

  constructor(private formBuilder: FormBuilder,
    private dialogRef: MatDialogRef<AddAffaireDialogComponent>,
    private customerOrderService: CustomerOrderService,
    private appService: AppService,
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
      if (!this.isForClient && !this.selectedCompetentAuthority)
        this.appService.displaySnackBar("Veuillez choisir une autorité compétente destinataire", true, 30);
      if (this.printLetters) {
        this.customerOrderService.generateMailingLabelDownload(this.customerOrders, this.printLabel, this.selectedCompetentAuthority, this.printLetters, this.printRegisteredLetter);
        this.dialogRef.close(this.customerOrders);
      }
      else
        this.customerOrderService.generateMailingLabel(this.customerOrders, this.printLabel, this.selectedCompetentAuthority, this.printLetters, this.printRegisteredLetter).subscribe(response => {
          this.dialogRef.close(this.customerOrders);
        });
    }
  }

  closeDialog() {
    this.dialogRef.close(null);
  }
}
