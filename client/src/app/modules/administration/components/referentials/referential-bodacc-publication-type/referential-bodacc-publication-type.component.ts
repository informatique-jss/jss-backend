import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import { AppService } from 'src/app/app.service';
import { ActType } from 'src/app/modules/quotation/model/ActType';
import { BodaccPublicationType } from 'src/app/modules/quotation/model/BodaccPublicationType';
import { BodaccPublicationTypeService } from 'src/app/modules/quotation/services/bodacc-publication-type.service';
import { GenericReferentialComponent } from '../generic-referential/generic-referential-component';

@Component({
  selector: 'referential-bodacc-publication-type',
  templateUrl: './../generic-referential/generic-referential.component.html',
  styleUrls: ['./../generic-referential/generic-referential.component.css']
})
export class ReferentialBodaccPublicationTypeComponent extends GenericReferentialComponent<BodaccPublicationType> implements OnInit {
  constructor(private bodaccPublicationTypeService: BodaccPublicationTypeService,
    private formBuilder2: FormBuilder,
    private appService2: AppService,) {
    super(formBuilder2, appService2);
  }

  getAddOrUpdateObservable(): Observable<ActType> {
    return this.bodaccPublicationTypeService.addOrUpdateBodaccPublicationType(this.selectedEntity!);
  }
  getGetObservable(): Observable<ActType[]> {
    return this.bodaccPublicationTypeService.getBodaccPublicationTypes();
  }
}
