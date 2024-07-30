import { moveItemInArray } from '@angular/cdk/drag-drop';
import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { MatAutocompleteSelectedEvent } from '@angular/material/autocomplete';
import { MatDialog } from '@angular/material/dialog';
import { Observable } from 'rxjs';
import { map, startWith } from 'rxjs/operators';
import { SpecialOffer } from '../../../model/SpecialOffer';
import { SpecialOfferService } from '../../../services/special.offer.service';
import { SpecialOffersDialogComponent } from '../../special-offers-dialog/special-offers-dialog.component';
import { GenericChipsComponent } from '../generic-chips/generic-chips.component';
import { AppService } from 'src/app/services/app.service';

@Component({
  selector: 'chips-special-offers',
  templateUrl: './chips-special-offers.component.html',
  styleUrls: ['./chips-special-offers.component.css']
})
export class ChipsSpecialOffersComponent extends GenericChipsComponent<SpecialOffer> implements OnInit {

  specialOffers: SpecialOffer[] = [] as Array<SpecialOffer>;
  filteredSpecialOffers: Observable<SpecialOffer[]> | undefined;
  @ViewChild('specialOfferInput') specialOfferInput: ElementRef<HTMLInputElement> | undefined;

  constructor(private formBuild: UntypedFormBuilder, public specialOfferDialog: MatDialog,
    private specialOfferService: SpecialOfferService, private appService3: AppService) {
    super(formBuild, appService3)
  }

  callOnNgInit(): void {
    this.specialOfferService.getSpecialOffers().subscribe(response => {
      this.specialOffers = response;
    })
    if (this.form)
      this.filteredSpecialOffers = this.form.get(this.propertyName)?.valueChanges.pipe(
        startWith(''),
        map(value => (typeof value === 'string') ? this._filterByCode(this.specialOffers, value) : [])
      );
  }

  validateInput(value: string): boolean {
    return true;
  }

  openSpecialOffersDialog() {
    let dialogSpecialOffer = this.specialOfferDialog.open(SpecialOffersDialogComponent, {
      width: '90%',
      height: '80%'
    });
    dialogSpecialOffer.afterClosed().subscribe(response => {
      if (!this.model)
        this.model = [] as Array<SpecialOffer>;
      if (response && response != null) {
        if (this.model.map(specialOffer => specialOffer.id).indexOf(response.id) < 0) {
          this.model.push(response);
          this.modelChange.emit(this.model);
        }
      }
    });
  }

  setValueToObject(value: string, object: SpecialOffer): SpecialOffer {
    return object;
  }

  getValueFromObject(object: SpecialOffer): string {
    return object.label;
  }

  private _filterByCode(inputList: any, value: string): any {
    const filterValue = (value != undefined && value.toLowerCase != undefined) ? value.toLowerCase() : "";
    return inputList.filter((input: any) => input.code != undefined && input.code.toLowerCase().includes(filterValue));
  }

  changeSpecialOfferOrder(event: any) {
    if (!this.isDisabled)
      moveItemInArray(this.model!, event.previousIndex, event.currentIndex);
  }

  addSpecialOffer(event: MatAutocompleteSelectedEvent): void {
    if (this.form != undefined) {
      if (!this.model)
        this.model = [] as Array<SpecialOffer>;
      // Do not add twice
      if (this.model.map(specialOffer => specialOffer.id).indexOf(event.option.value.id) >= 0)
        return;
      if (event.option && event.option.value && event.option.value.id)
        this.model.push(event.option.value);
      this.modelChange.emit(this.model);
      this.form.get(this.propertyName)?.setValue(null);
      this.specialOfferInput!.nativeElement.value = '';
    }
  }

  deleteAllSpecialOffers() {
    if (this.form != undefined) {
      this.model = [] as Array<SpecialOffer>;
      this.modelChange.emit(this.model);
      this.form.get(this.propertyName)?.setValue(null);
      this.specialOfferInput!.nativeElement.value = '';
    }
  }
}
