import { moveItemInArray } from '@angular/cdk/drag-drop';
import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { UntypedFormBuilder, ValidatorFn, Validators } from '@angular/forms';
import { MatAutocompleteSelectedEvent } from '@angular/material/autocomplete';
import { MatDialog } from '@angular/material/dialog';
import { Observable } from 'rxjs';
import { map, startWith } from 'rxjs/operators';
import { UserNoteService } from 'src/app/services/user.notes.service';
import { SpecialOffer } from '../../../model/SpecialOffer';
import { SpecialOfferService } from '../../../services/special.offer.service';
import { SpecialOffersDialogComponent } from '../../special-offers-dialog/special-offers-dialog.component';
import { GenericChipsComponent } from '../generic-chips/generic-chips.component';

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
    private specialOfferService: SpecialOfferService, private userNoteService2: UserNoteService,) {
    super(formBuild, userNoteService2)
  }

  ngOnInit() {
    this.specialOfferService.getSpecialOffers().subscribe(response => {
      this.specialOffers = response;
    })
    if (this.form != undefined) {
      let validators: ValidatorFn[] = [] as Array<ValidatorFn>;
      if (this.isMandatory) {
        if (this.conditionnalRequired != undefined) {
          validators.push(this.checkFieldFilledIfIsConditionalRequired());
        } else {
          validators.push(Validators.required);
        }
      }

      if (this.customValidators != undefined && this.customValidators != null && this.customValidators.length > 0)
        validators.push(...this.customValidators);

      this.form.addControl(this.propertyName, this.formBuild.control('', validators));

      this.filteredSpecialOffers = this.form.get(this.propertyName)?.valueChanges.pipe(
        startWith(''),
        map(value => (typeof value === 'string') ? this._filterByCode(this.specialOffers, value) : [])
      );

      this.form.markAllAsTouched();
    }
  }


  validateInput(value: string): boolean {
    return true;
  }

  openSpecialOffersDialog() {
    let dialogSpecialOffer = this.specialOfferDialog.open(SpecialOffersDialogComponent, {
      width: '90%'
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
