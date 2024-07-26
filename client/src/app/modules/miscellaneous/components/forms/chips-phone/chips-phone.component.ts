import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { validateFrenchPhone, validateInternationalPhone } from 'src/app/libs/CustomFormsValidatorsHelper';
import { callNumber } from 'src/app/libs/MailHelper';
import { Phone } from '../../../model/Phone';
import { GenericChipsComponent } from '../generic-chips/generic-chips.component';

@Component({
  selector: 'chips-phone',
  templateUrl: './chips-phone.component.html',
  styleUrls: ['./chips-phone.component.css']
})
export class ChipsPhoneComponent extends GenericChipsComponent<Phone> implements OnInit {

  constructor(private formBuild: UntypedFormBuilder,) {
    super(formBuild)
  }

  callOnNgInit(): void {
  }

  validateInput(value: string): boolean {
    let a = validateFrenchPhone(value)
    let b = validateInternationalPhone(value);
    if (a != null && a.length > 0 || b != null && b.length > 0)
      return true;
    return false;
  }

  setValueToObject(value: string, object: Phone): Phone {
    object.phoneNumber = value.replace(/ /g, '');
    return object;
  }

  getValueFromObject(object: Phone): string {
    return object.phoneNumber;
  }

  call = function (phone: Phone) {
    callNumber(phone.phoneNumber);
  }
}
