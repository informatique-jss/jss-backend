import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { validateEmail } from 'src/app/libs/CustomFormsValidatorsHelper';
import { prepareMail } from 'src/app/libs/MailHelper';
import { AppService } from 'src/app/services/app.service';
import { Mail } from '../../../model/Mail';
import { GenericSingleChipsComponent } from '../generic-single-chips/generic-single-chips.component';

@Component({
  selector: 'single-chips-mail',
  templateUrl: './single-chips-mail.component.html',
  styleUrls: ['./single-chips-mail.component.css']
})
export class SingleChipsMailComponent extends GenericSingleChipsComponent<Mail> implements OnInit {


  constructor(private formBuild: UntypedFormBuilder, private appService3: AppService) {
    super(formBuild, appService3)
  }

  validateInput(value: string): boolean {
    let a = validateEmail(value);
    if (a != null && a.length > 0)
      return true;
    return false;
  }

  setValueToObject(value: string, object: Mail): Mail {
    object.mail = value;
    return object;
  }

  getValueFromObject(object: Mail): string {
    return object.mail;
  }

  prepareMail = function (mail: Mail) {
    prepareMail(mail.mail, null, null);
  }

  callOnNgInit(): void {
  }
}
