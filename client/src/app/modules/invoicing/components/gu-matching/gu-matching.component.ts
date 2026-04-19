import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { UploadAttachementDialogComponent } from 'src/app/modules/miscellaneous/components/upload-attachement-dialog/upload-attachement-dialog.component';
import { IAttachment } from 'src/app/modules/miscellaneous/model/IAttachment';
import { SortTableColumn } from 'src/app/modules/miscellaneous/model/SortTableColumn';
import { ConstantService } from 'src/app/modules/miscellaneous/services/constant.service';
import { INPI_INVOICING_EXTRACT_ENTITY_TYPE } from 'src/app/routing/search/search.component';
import { AppService } from 'src/app/services/app.service';
import { GuMatchingResult } from '../../model/GuMatchingResult';
import { GuMatchingService } from '../../services/gu.matching.service';

@Component({
  selector: 'gu-matching',
  templateUrl: './gu-matching.component.html',
  styleUrls: ['./gu-matching.component.css'],
  standalone: false
})
export class GuMatchingComponent implements OnInit {

  constructor(
    private formBuilder: FormBuilder,
    public confirmationDialog: MatDialog,
    public uploadAttachementDialog: MatDialog,
    private constantService: ConstantService,
    private guMatchingService: GuMatchingService,
    private appService: AppService
  ) { }


  uploadAttachementDialogRef: MatDialogRef<UploadAttachementDialogComponent> | undefined;
  guMatchingResults: GuMatchingResult[] | undefined;
  startDate: Date | undefined;
  endDate: Date | undefined;
  displayedColumns: SortTableColumn<GuMatchingResult>[] = [];
  guMatchingForm = this.formBuilder.group({
  });

  ngOnInit() {
    this.displayedColumns.push({ id: "date", fieldName: "date", label: "Date" } as SortTableColumn<GuMatchingResult>);
    this.displayedColumns.push({ id: "invoiceId", fieldName: "invoiceId", label: "Id" } as SortTableColumn<GuMatchingResult>);
    this.displayedColumns.push({ id: "liasseNumber", fieldName: "liasseNumber", label: "Numéro de liasse" } as SortTableColumn<GuMatchingResult>);
    this.displayedColumns.push({ id: "customerOrderId", fieldName: "customerOrderId", label: "Numéro de commande" } as SortTableColumn<GuMatchingResult>);
    this.displayedColumns.push({ id: "inpiAmount", fieldName: "inpiAmount", label: "Montant INPI" } as SortTableColumn<GuMatchingResult>);
    this.displayedColumns.push({ id: "osirisAmount", fieldName: "osirisAmount", label: "Montant Osiris" } as SortTableColumn<GuMatchingResult>);
    this.displayedColumns.push({ id: "matchingStatus", fieldName: "matchingStatus", label: "Résultat du rapprochement" } as SortTableColumn<GuMatchingResult>);
  }


  importInpiInvoicingExtractFile() {
    this.uploadAttachementDialogRef = this.uploadAttachementDialog.open(UploadAttachementDialogComponent, {
    });
    this.uploadAttachementDialogRef.componentInstance.entity = { id: 1 } as IAttachment;
    this.uploadAttachementDialogRef.componentInstance.entityType = INPI_INVOICING_EXTRACT_ENTITY_TYPE.entityType;
    this.uploadAttachementDialogRef.componentInstance.forcedAttachmentType = this.constantService.getAttachmentTypeBillingClosure();
    this.uploadAttachementDialogRef.afterClosed().subscribe(result => {


    });
  }

  matchInpiExtractWithOsiris() {
    if (this.guMatchingForm.valid && this.startDate && this.endDate) {
      if (this.startDate >= this.endDate) {
        this.appService.displaySnackBar("Veuillez renseigner une date de début antérieure à la date de fin", true, 10);
        return;
      }
      this.guMatchingService.getInpiExtractAndOsirisMatchingResult(this.startDate, this.endDate).subscribe(response => {
        if (response && response.length > 0) {
          this.guMatchingResults = response;
        }
        else {
          this.guMatchingResults = [];
          this.appService.displaySnackBar("Pas d'écart trouvé pour cette plage de recherche", true, 10);
        }
      })
    } else {
      this.appService.displaySnackBar("🙄 Merci de saisir une plage de recherche", true, 10);
    }

  }
}
