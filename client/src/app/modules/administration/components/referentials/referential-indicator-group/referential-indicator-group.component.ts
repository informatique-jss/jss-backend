import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import { IndicatorGroup } from 'src/app/modules/reporting/model/IndicatorGroup';
import { IndicatorGroupService } from 'src/app/modules/reporting/services/indicator-group.service';
import { AppService } from 'src/app/services/app.service';
import { GenericReferentialComponent } from '../generic-referential/generic-referential-component';

@Component({
  selector: 'referential-indicator-group',
  templateUrl: './../generic-referential/generic-referential.component.html',
  styleUrls: ['./../generic-referential/generic-referential.component.css']
})
export class ReferentialIndicatorGroupComponent extends GenericReferentialComponent<IndicatorGroup> implements OnInit {
  constructor(private indicatorGroupService: IndicatorGroupService,
    private formBuilder2: FormBuilder,
    private appService2: AppService,) {
    super(formBuilder2, appService2);
  }

  getAddOrUpdateObservable(): Observable<IndicatorGroup> {
    return this.indicatorGroupService.addOrUpdateIndicatorGroup(this.selectedEntity!);
  }
  getGetObservable(): Observable<IndicatorGroup[]> {
    return this.indicatorGroupService.getIndicatorGroups();
  }
}
