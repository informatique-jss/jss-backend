import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { MatLegacyAutocompleteSelectedEvent as MatAutocompleteSelectedEvent } from '@angular/material/legacy-autocomplete';
import { map, Observable, startWith } from 'rxjs';
import { UserNoteService } from 'src/app/services/user.notes.service';
import { PaymentType } from '../../../model/PaymentType';
import { PaymentTypeService } from '../../../services/payment.type.service';
import { GenericChipsComponent } from '../generic-chips/generic-chips.component';

@Component({
  selector: 'chips-payment-type',
  templateUrl: './chips-payment-type.component.html',
  styleUrls: ['./chips-payment-type.component.css']
})
export class ChipsPaymentTypeComponent extends GenericChipsComponent<PaymentType> implements OnInit {

  paymentTypes: PaymentType[] = [] as Array<PaymentType>;
  filteredPaymentTypes: Observable<PaymentType[]> | undefined;
  @ViewChild('paymentTypeInput') PaymentTypeInput: ElementRef<HTMLInputElement> | undefined;

  constructor(private formBuild: UntypedFormBuilder,
    private paymentTypeService: PaymentTypeService, private userNoteService2: UserNoteService,) {
    super(formBuild, userNoteService2)
  }

  callOnNgInit(): void {
    this.paymentTypeService.getPaymentTypes().subscribe(response => {
      this.paymentTypes = response;
    })
    if (this.form)
      this.filteredPaymentTypes = this.form.get(this.propertyName)?.valueChanges.pipe(
        startWith(''),
        map(value => this._filterByName(this.paymentTypes, value))
      );
  }

  validateInput(value: string): boolean {
    return true;
  }

  setValueToObject(value: string, object: PaymentType): PaymentType {
    return object;
  }

  getValueFromObject(object: PaymentType): string {
    return object.label;
  }

  private _filterByName(inputList: any, value: string): any {
    const filterValue = (value != undefined && value.toLowerCase != undefined) ? value.toLowerCase() : "";
    return inputList.filter((input: any) => input.label != undefined && input.label.toLowerCase().includes(filterValue));
  }

  addPaymentType(event: MatAutocompleteSelectedEvent): void {
    if (this.form != undefined) {
      if (!this.model)
        this.model = [] as Array<PaymentType>;
      // Do not add twice
      if (this.model.map(PaymentType => PaymentType.code).indexOf(event.option.value.code) >= 0)
        return;
      if (event.option && event.option.value && event.option.value.code)
        this.model.push(event.option.value);
      this.modelChange.emit(this.model);
      this.form.get(this.propertyName)?.setValue(null);
      this.PaymentTypeInput!.nativeElement.value = '';
    }
  }
}
