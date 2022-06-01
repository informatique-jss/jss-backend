import { Component, OnInit } from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';
import { MatTableDataSource } from '@angular/material/table';
import { SpecialOfferFlatten } from 'src/app/modules/miscellaneous/model/SpecialOfferFlatten';
import { BillingItem } from 'src/app/modules/miscellaneous/model/BillingItem';
import { SpecialOfferService } from 'src/app/modules/miscellaneous/services/special-offer.service';
import { SpecialOffer } from '../../model/SpecialOffer';

@Component({
  selector: 'app-special-offers-dialog',
  templateUrl: './special-offers-dialog.component.html',
  styleUrls: ['./special-offers-dialog.component.css']
})
export class SpecialOffersDialogComponent implements OnInit {

  displayedColumns: string[] = ['code', 'discountAmount', 'discountRate', 'vat', 'billingTypeLabel', 'billingTypePreTaxPrice'];

  specialOffersFlatten: SpecialOfferFlatten[] = [] as Array<SpecialOfferFlatten>;
  specialOffers: SpecialOffer[] = [] as Array<SpecialOffer>;
  specialOffersDataSource = new MatTableDataSource<SpecialOfferFlatten>();

  filterValue: string = "";

  constructor(private specialOfferService: SpecialOfferService,
    private specialOffersDialogRef: MatDialogRef<SpecialOffersDialogComponent>) { }

  ngOnInit() {
    this.specialOfferService.getSpecialOffers().subscribe(response => {
      this.specialOffers = response;
      // Flatten object to display
      if (response && response.length > 0) {
        response.forEach(specialOffer => {
          specialOffer.billingItems.forEach(billingItem => {
            let localSpecialOffer = {} as SpecialOfferFlatten;
            localSpecialOffer.id = specialOffer.id;
            localSpecialOffer.code = specialOffer.code + ((specialOffer.label != null && specialOffer.label != "") ? " - " : "") + specialOffer.label;
            localSpecialOffer.billingTypeLabel = billingItem.billingType.label;
            localSpecialOffer.billingTypePreTaxPrice = billingItem.billingType.preTaxPrice;
            localSpecialOffer.discountAmount = billingItem.discountAmount;
            localSpecialOffer.discountRate = billingItem.discountRate;
            localSpecialOffer.vat = billingItem.vat.rate;
            this.specialOffersFlatten.push(localSpecialOffer);
          });
        });
        this.specialOffersDataSource.data = this.specialOffersFlatten;
      }
    })
  }

  applyFilter(filterValue: any) {
    let filterValueCast = (filterValue as HTMLInputElement);
    filterValue = filterValueCast.value.trim();
    filterValue = filterValue.toLowerCase();
    this.specialOffersDataSource.filter = filterValue;
  }

  chooseSpecialOffer(specialOfferFlatten: SpecialOfferFlatten) {
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
