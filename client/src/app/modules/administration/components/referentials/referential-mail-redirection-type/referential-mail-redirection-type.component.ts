import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import { AppService } from 'src/app/app.service';
import { MailRedirectionType } from 'src/app/modules/quotation/model/MailRedirectionType';
import { MailRedirectionTypeService } from 'src/app/modules/quotation/services/mail.redirection.type.service';
import { GenericReferentialComponent } from '../generic-referential/generic-referential-component';

@Component({
  selector: 'referential-mail-redirection-type',
  templateUrl: './../generic-referential/generic-referential.component.html',
  styleUrls: ['./../generic-referential/generic-referential.component.css']
})
export class ReferentialMailRedirectionTypeComponent extends GenericReferentialComponent<MailRedirectionType> implements OnInit {
  constructor(private mailRedirectionTypeService: MailRedirectionTypeService,
    private formBuilder2: FormBuilder,
    private appService2: AppService,) {
    super(formBuilder2, appService2);
  }

  getAddOrUpdateObservable(): Observable<MailRedirectionType> {
    return this.mailRedirectionTypeService.addOrUpdateMailRedirectionType(this.selectedEntity!);
  }
  getGetObservable(): Observable<MailRedirectionType[]> {
    return this.mailRedirectionTypeService.getMailRedirectionTypes();
  }
}
