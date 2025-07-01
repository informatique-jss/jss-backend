import { Component, Input, OnInit } from '@angular/core';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { Quotation } from 'src/app/modules/quotation/model/Quotation';
import { SortTableColumn } from '../../../model/SortTableColumn';

@Component({
  selector: 'app-suggested-quotations-dialog',
  templateUrl: './suggested-quotations-dialog.component.html',
  styleUrls: ['./suggested-quotations-dialog.component.css']
})
export class SuggestedQuotationsDialogComponent implements OnInit {

  @Input() suggestedQuotations: Quotation[] | undefined;
  quotationColumns: SortTableColumn<Quotation>[] = [] as Array<SortTableColumn<Quotation>>;
  outQuotation = {} as Quotation;

  constructor(public confirmationDialog: MatDialog,
    private suggestionsDialogRef: MatDialogRef<SuggestedQuotationsDialogComponent>) { }

  ngOnInit() {
    this.quotationColumns = [];
    this.quotationColumns.push({ id: "id", fieldName: "id", label: "N° de devis" } as SortTableColumn<Quotation>);
  }

  chooseQuotation(chosenQuotation: Quotation) {
    if (this.suggestedQuotations)
      this.suggestedQuotations.forEach(quotation => {
        if (quotation.id == chosenQuotation.id)
          this.outQuotation = quotation;
        return;
      });

  }

  onConfirm(): void {
    this.suggestionsDialogRef.close(this.outQuotation);
  }

  onClose(): void {
    this.suggestionsDialogRef.close(null);
  }
}
