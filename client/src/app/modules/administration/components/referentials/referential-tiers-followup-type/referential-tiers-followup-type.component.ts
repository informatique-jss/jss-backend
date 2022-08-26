import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import { TiersFollowupType } from 'src/app/modules/tiers/model/TiersFollowupType';
import { TiersFollowupTypeService } from 'src/app/modules/tiers/services/tiers.followup.type.service';
import { AppService } from 'src/app/services/app.service';
import { GenericReferentialComponent } from '../generic-referential/generic-referential-component';

@Component({
  selector: 'referential-tiers-followup-type',
  templateUrl: './../generic-referential/generic-referential.component.html',
  styleUrls: ['./../generic-referential/generic-referential.component.css']
})
export class ReferentialTiersFollowupTypeComponent extends GenericReferentialComponent<TiersFollowupType> implements OnInit {
  constructor(private tiersFollowupTypeService: TiersFollowupTypeService,
    private formBuilder2: FormBuilder,
    private appService2: AppService,) {
    super(formBuilder2, appService2);
  }

  getAddOrUpdateObservable(): Observable<TiersFollowupType> {
    return this.tiersFollowupTypeService.addOrUpdateTiersFollowupType(this.selectedEntity!);
  }
  getGetObservable(): Observable<TiersFollowupType[]> {
    return this.tiersFollowupTypeService.getTiersFollowupTypes();
  }
}
