import { Component, Input, OnInit } from '@angular/core';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { QUOTATION_STATUS_SENT_TO_CUSTOMER } from 'src/app/libs/Constants';
import { Affaire } from 'src/app/modules/quotation/model/Affaire';
import { QuotationSearch } from 'src/app/modules/quotation/model/QuotationSearch';
import { QuotationStatus } from 'src/app/modules/quotation/model/QuotationStatus';
import { QuotationStatusService } from '../../services/quotation-status.service';

@Component({
  selector: 'suggested-quotations-dialog',
  templateUrl: './suggested-quotations-dialog.component.html',
  styleUrls: ['./suggested-quotations-dialog.component.css']
})
export class SuggestedQuotationsDialogComponent implements OnInit {

  @Input() selectedAffaire: Affaire | undefined;
  quotationSearch: QuotationSearch = {} as QuotationSearch;

  QUOTATION_STATUS_SENT_TO_CUSTOMER = QUOTATION_STATUS_SENT_TO_CUSTOMER;

  constructor(public confirmationDialog: MatDialog,
    private quotationStatusService: QuotationStatusService,
    private suggestionsDialogRef: MatDialogRef<SuggestedQuotationsDialogComponent>) { }

  ngOnInit() {
    if (this.selectedAffaire) {
      this.quotationSearch.affaires = [this.selectedAffaire];
      this.quotationStatusService.getQuotationStatus().subscribe(response => {
        if (response) {
          this.quotationSearch.quotationStatus = [] as Array<QuotationStatus>;
          this.quotationSearch.quotationStatus.push(this.quotationStatusService.getQuotationStatusByCode(response, QUOTATION_STATUS_SENT_TO_CUSTOMER)!);
        }
      });
    }
  }

  onConfirm(): void {
    this.suggestionsDialogRef.close();
  }

  onClose(): void {
    this.suggestionsDialogRef.close(null);
  }
}
