import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import { TiersGroup } from 'src/app/modules/tiers/model/TiersGroup';
import { AppService } from 'src/app/services/app.service';
import { TiersGroupService } from '../../../../quotation/services/tiers-group.service';
import { GenericReferentialComponent } from '../generic-referential/generic-referential-component';

@Component({
  selector: 'referential-tiers-group',
  templateUrl: './../generic-referential/generic-referential.component.html',
  styleUrls: ['./../generic-referential/generic-referential.component.css']
})
export class ReferentialTiersGroupComponent extends GenericReferentialComponent<TiersGroup> implements OnInit {
  constructor(private tiersGroupService: TiersGroupService,
    private formBuilder2: FormBuilder,
    private appService2: AppService,) {
    super(formBuilder2, appService2);
  }

  getAddOrUpdateObservable(): Observable<TiersGroup> {
    return this.tiersGroupService.addOrUpdateTiersGroup(this.selectedEntity!);
  }
  getGetObservable(): Observable<TiersGroup[]> {
    return this.tiersGroupService.getTiersGroups();
  }

}
