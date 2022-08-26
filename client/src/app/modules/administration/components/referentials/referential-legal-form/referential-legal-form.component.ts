import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import { LegalForm } from 'src/app/modules/miscellaneous/model/LegalForm';
import { LegalFormService } from 'src/app/modules/miscellaneous/services/legal.form.service';
import { AppService } from 'src/app/services/app.service';
import { GenericReferentialComponent } from '../generic-referential/generic-referential-component';

@Component({
  selector: 'referential-legal-form',
  templateUrl: './../generic-referential/generic-referential.component.html',
  styleUrls: ['./../generic-referential/generic-referential.component.css']
})
export class ReferentialLegalFormComponent extends GenericReferentialComponent<LegalForm> implements OnInit {
  constructor(private legalFormService: LegalFormService,
    private formBuilder2: FormBuilder,
    private appService2: AppService,) {
    super(formBuilder2, appService2);
  }
  getAddOrUpdateObservable(): Observable<LegalForm> {
    return this.legalFormService.addOrUpdateLegalForm(this.selectedEntity!);
  }
  getGetObservable(): Observable<LegalForm[]> {
    return this.legalFormService.getLegalForms();
  }
}
