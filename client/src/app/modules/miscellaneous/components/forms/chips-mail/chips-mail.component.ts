import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { validateEmail } from 'src/app/libs/CustomFormsValidatorsHelper';
import { prepareMail } from 'src/app/libs/MailHelper';
import { UserNoteService } from 'src/app/services/user.notes.service';
import { Mail } from '../../../model/Mail';
import { GenericChipsComponent } from '../generic-chips/generic-chips.component';

@Component({
  selector: 'chips-mail',
  templateUrl: './chips-mail.component.html',
  styleUrls: ['./chips-mail.component.css']
})
export class ChipsMailComponent extends GenericChipsComponent<Mail> implements OnInit {


  constructor(private formBuild: UntypedFormBuilder, private userNoteService2: UserNoteService,) {
    super(formBuild, userNoteService2)
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
