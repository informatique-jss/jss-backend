import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { MailRedirectionType } from 'src/app/modules/quotation/model/MailRedirectionType';
import { MailRedirectionTypeService } from 'src/app/modules/quotation/services/mail.redirection.type.service';
import { UserNoteService } from 'src/app/services/user.notes.service';
import { GenericSelectComponent } from '../generic-select/generic-select.component';

@Component({
  selector: 'select-mail-redirection',
  templateUrl: './select-mail-redirection.component.html',
  styleUrls: ['./select-mail-redirection.component.css']
})
export class SelectMailRedirectionComponent extends GenericSelectComponent<MailRedirectionType> implements OnInit {

  types: MailRedirectionType[] = [] as Array<MailRedirectionType>;

  constructor(private formBuild: UntypedFormBuilder, private mailRedirectionTypeService: MailRedirectionTypeService, private userNoteService2: UserNoteService,) {
    super(formBuild, userNoteService2)
  }

  initTypes(): void {
    this.mailRedirectionTypeService.getMailRedirectionTypes().subscribe(response => {
      this.types = response;
    })
  }
}
