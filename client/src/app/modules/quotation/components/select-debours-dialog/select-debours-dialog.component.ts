import { Component, OnInit, SimpleChanges } from "@angular/core";
import { FormBuilder } from "@angular/forms";
import { MatDialogRef } from "@angular/material/dialog";
import { formatDateForSortTable, formatEurosForSortTable } from "src/app/libs/FormatHelper";
import { Payment } from "src/app/modules/invoicing/model/Payment";
import { SortTableColumn } from "src/app/modules/miscellaneous/model/SortTableColumn";
import { AppService } from "src/app/services/app.service";
import { ConstantService } from '../../../miscellaneous/services/constant.service';
import { Debour } from "../../model/Debour";
import { DebourPaymentAssociationRequest } from "../../model/DebourPaymentAssociationRequest";
import { Provision } from "../../model/Provision";
import { AddAffaireDialogComponent } from "../add-affaire-dialog/add-affaire-dialog.component";


@Component({
  selector: 'app-select-debour-type-dialog',
  templateUrl: './select-debours-dialog.component.html',
  styleUrls: ['./select-debours-dialog.component.css']
})
export class SelectDeboursDialogComponent implements OnInit {

  provision: Provision | undefined;
  selectedDebours: Debour[] = [];
  displayedColumns: SortTableColumn[] = [];
  payment: Payment | undefined;

  constructor(private formBuilder: FormBuilder,
    private appService: AppService,
    private constantService: ConstantService,
    private dialogRef: MatDialogRef<AddAffaireDialogComponent>
  ) { }

  ngOnInit() {
    this.displayedColumns = [];
    this.displayedColumns.push({ id: "billingType", fieldName: "billingType.label", label: "Débour" } as SortTableColumn);
    this.displayedColumns.push({ id: "competentAuthority", fieldName: "competentAuthority.label", label: "Autorité compétente" } as SortTableColumn);
    this.displayedColumns.push({ id: "debourAmount", fieldName: "debourAmount", label: "Montant", valueFonction: formatEurosForSortTable } as SortTableColumn);
    this.displayedColumns.push({ id: "paymentType", fieldName: "paymentType.label", label: "Type de paiement" } as SortTableColumn);
    this.displayedColumns.push({ id: "paymentDateTime", fieldName: "paymentDateTime", label: "Date de paiement", valueFonction: formatDateForSortTable } as SortTableColumn);
    this.displayedColumns.push({ id: "payment", fieldName: "payment.id", label: "Paiement associé" } as SortTableColumn);
    this.displayedColumns.push({ id: "comments", fieldName: "comments", label: "Commentaires", isShrinkColumn: true } as SortTableColumn);
  }

  ngOnChanges(changes: SimpleChanges) {
  }

  selectDebour(debour: Debour) {
    if (debour.competentAuthority && debour.competentAuthority.competentAuthorityType && !debour.competentAuthority.competentAuthorityType.isDirectCharge) {
      this.appService.displaySnackBar("Cette autorité compétente n'est pas à charge directe. L'association du paiement se fait sur le facture associée", true, 15);
      return;
    }

    if (debour.payment) {
      this.appService.displaySnackBar("Ce débour a déjà été rapproché", true, 15);
      return;
    }

    if (this.selectedDebours)
      for (let att of this.selectedDebours)
        if (att.id == debour.id)
          return;
    this.selectedDebours.push(debour);
  }

  deleteDebour(index: number) {
    this.selectedDebours.splice(index, 1);
  }

  debourForm = this.formBuilder.group({});

  associatePayment() {
    if (!this.payment) {
      this.appService.displaySnackBar("Aucun paiement sélectionné", true, 15);
    }
    else if (this.provision && this.payment && (Math.round(this.payment.paymentAmount * 100) / 100) == (Math.round(this.getSelectedDeboursSum() * 100) / 100)) {
      let association = {} as DebourPaymentAssociationRequest;
      association.payment = this.payment;
      association.debours = this.selectedDebours;
      this.dialogRef.close(association);
    } else {
      this.appService.displaySnackBar("Le montant des débours sélectionnés doit correspondre au montant du paiement", true, 15);
    }
  }

  getSelectedDeboursSum(): number {
    let total = 0;
    if (this.selectedDebours && this.selectedDebours.length)
      for (let debour of this.selectedDebours)
        total += debour.debourAmount;
    return total;
  }

  closeDialog() {
    this.dialogRef.close(null);
  }
}
