import { Component, OnInit } from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';
import { formatEurosForSortTable, formatPercentForSortTable } from 'src/app/libs/FormatHelper';
import { SpecialOfferFlatten } from 'src/app/modules/miscellaneous/model/SpecialOfferFlatten';
import { SortTableColumn } from '../../model/SortTableColumn';
import { SpecialOffer } from '../../model/SpecialOffer';
import { SpecialOfferService } from '../../services/special.offer.service';

@Component({
  selector: 'app-special-offers-dialog',
  templateUrl: './special-offers-dialog.component.html',
  styleUrls: ['./special-offers-dialog.component.css']
})
export class SpecialOffersDialogComponent implements OnInit {

  ddisplayedColumns: string[] = ['code', 'discountAmount', 'discountRate', 'vat', 'billingTypeLabel', 'billingTypePreTaxPrice'];

  specialOffersFlatten: SpecialOfferFlatten[] = [] as Array<SpecialOfferFlatten>;
  specialOffers: SpecialOffer[] = [] as Array<SpecialOffer>;

  filterValue: string = "";

  displayedColumns: SortTableColumn[] = [];
  searchText: string | undefined;

  constructor(private specialOfferService: SpecialOfferService,
    private specialOffersDialogRef: MatDialogRef<SpecialOffersDialogComponent>) { }

  ngOnInit() {
    this.specialOfferService.getSpecialOffers().subscribe(response => {
      this.specialOffers = response;
      // Flatten object to display
      if (response && response.length > 0) {
        response.forEach(specialOffer => {
          specialOffer.assoSpecialOfferBillingTypes.forEach(assoSpecialOfferBillingType => {
            let localSpecialOffer = {} as SpecialOfferFlatten;
            localSpecialOffer.id = specialOffer.id!;
            localSpecialOffer.code = specialOffer.code + ((specialOffer.label != null && specialOffer.label != "") ? " - " : "") + specialOffer.label;
            localSpecialOffer.billingTypeLabel = assoSpecialOfferBillingType.billingType.label;
            localSpecialOffer.discountAmount = assoSpecialOfferBillingType.discountAmount;
            localSpecialOffer.discountRate = assoSpecialOfferBillingType.discountRate;
            this.specialOffersFlatten.push(localSpecialOffer);
          });
        });
      }
    })
    this.displayedColumns = [];
    this.displayedColumns.push({ id: "code", fieldName: "code", label: "Libellé" } as SortTableColumn);
    this.displayedColumns.push({ id: "discountAmount", fieldName: "discountAmount", label: "Montant de la remise", valueFonction: formatEurosForSortTable } as SortTableColumn);
    this.displayedColumns.push({ id: "discountRate", fieldName: "discountRate", label: "Taux de remise", valueFonction: formatPercentForSortTable } as SortTableColumn);
    this.displayedColumns.push({ id: "vat", fieldName: "vat", label: "Taux de TVA", valueFonction: formatPercentForSortTable } as SortTableColumn);
    this.displayedColumns.push({ id: "billingTypeLabel", fieldName: "billingTypeLabel", label: "Poste de facturation" } as SortTableColumn);
    this.displayedColumns.push({ id: "billingTypePreTaxPrice", fieldName: "billingTypePreTaxPrice", label: "Prix HT du poste" } as SortTableColumn);
  }

  formatEurosForSortTable = formatEurosForSortTable;
  formatPercentForSortTable = formatPercentForSortTable;

  applyFilter(filterValue: any) {
    let filterValueCast = (filterValue as HTMLInputElement);
    filterValue = filterValueCast.value.trim();
    this.searchText = filterValue.toLowerCase();
  }

  chooseSpecialOffer(specialOfferFlatten: SpecialOfferFlatten) {
    console.log("kk");
    let outSpecialOffer = null;
    this.specialOffers.forEach(specialOffer => {
      if (specialOffer.id == specialOfferFlatten.id)
        outSpecialOffer = specialOffer;
      return;
    });
    this.specialOffersDialogRef.close(outSpecialOffer);
  }

  closeDialog() {
    this.specialOffersDialogRef.close(null);
  }


}
