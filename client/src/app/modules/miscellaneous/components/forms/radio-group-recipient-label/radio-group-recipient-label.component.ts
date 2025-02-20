import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { AppService } from 'src/app/services/app.service';
import { Recipient } from '../../../model/Recipient';
import { GenericRadioGroupComponent } from '../generic-radio-group/generic-radio-group.component';

@Component({
  selector: 'radio-group-recipient-label',
  templateUrl: '../generic-radio-group/generic-radio-group.component.html',
  styleUrls: ['../generic-radio-group/generic-radio-group.component.css']
})
export class RadioGroupRecipientLabelComponent extends GenericRadioGroupComponent<Recipient> implements OnInit {
  types: Recipient[] = [] as Array<Recipient>;

  constructor(
    private formBuild: UntypedFormBuilder,
    private appService3: AppService) {
    super(formBuild, appService3);
  }

  ngOnInit() {
  }

  initTypes(): void {
    let recipient = {} as Recipient;
    recipient.label = "client";
    recipient.id = 0;
    recipient.code = "client";
    let recipientBis = {} as Recipient;
    recipientBis.label = "AC";
    recipientBis.id = 1;
    recipientBis.code = "AC";
    this.types.push(recipient);
    this.types.push(recipientBis);
  }
}
