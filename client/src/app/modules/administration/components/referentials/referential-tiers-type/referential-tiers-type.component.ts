import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import { AppService } from 'src/app/app.service';
import { TiersType } from 'src/app/modules/tiers/model/TiersType';
import { TiersTypeService } from 'src/app/modules/tiers/services/tiers.type.service';
import { GenericReferentialComponent } from '../generic-referential/generic-referential-component';

@Component({
  selector: 'referential-tiers-type',
  templateUrl: './../generic-referential/generic-referential.component.html',
  styleUrls: ['./../generic-referential/generic-referential.component.css']
})
export class ReferentialTiersTypeComponent extends GenericReferentialComponent<TiersType> implements OnInit {
  constructor(private tiersTypeService: TiersTypeService,
    private formBuilder2: FormBuilder,
    private appService2: AppService,) {
    super(formBuilder2, appService2);
  }

  getAddOrUpdateObservable(): Observable<TiersType> {
    return this.tiersTypeService.addOrUpdateTiersType(this.selectedEntity!);
  }
  getGetObservable(): Observable<TiersType[]> {
    return this.tiersTypeService.getTiersTypes();
  }
}
