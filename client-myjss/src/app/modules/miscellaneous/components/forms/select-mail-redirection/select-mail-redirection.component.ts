import { Component, Input, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { SHARED_IMPORTS } from '../../../../../libs/SharedImports';
import { MailRedirectionType } from '../../../../quotation/model/MailRedirectionType';
import { MailRedirectionTypeService } from '../../../../quotation/services/mail.redirection.type.service';
import { GenericSelectComponent } from '../generic-select/generic-select.component';

@Component({
  selector: 'select-mail-redirection',
  templateUrl: '../generic-select/generic-select.component.html',
  styleUrls: ['../generic-select/generic-select.component.css'],
  standalone: true,
  imports: [SHARED_IMPORTS]
})
export class SelectMailRedirectionComponent extends GenericSelectComponent<MailRedirectionType> implements OnInit {

  @Input() types: MailRedirectionType[] = [] as Array<MailRedirectionType>;

  constructor(private formBuild: UntypedFormBuilder,
    private mailRedirectionTypeService: MailRedirectionTypeService,
  ) {
    super(formBuild)
  }

  initTypes(): void {
    this.mailRedirectionTypeService.getMailRedirectionTypes().subscribe(response => {
      this.types = response;
    })
  }
}
