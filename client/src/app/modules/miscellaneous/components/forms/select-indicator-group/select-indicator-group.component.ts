import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { IndicatorGroup } from 'src/app/modules/reporting/model/IndicatorGroup';
import { IndicatorGroupService } from 'src/app/modules/reporting/services/indicator-group.service';
import { AppService } from 'src/app/services/app.service';
import { GenericSelectComponent } from '../generic-select/generic-select.component';

@Component({
  selector: 'select-indicator-group',
  templateUrl: './../generic-select/generic-select.component.html',
  styleUrls: ['./../generic-select/generic-select.component.html']
})

export class SelectIndicatorGroupComponent extends GenericSelectComponent<IndicatorGroup> implements OnInit {

  types: IndicatorGroup[] = [] as Array<IndicatorGroup>;

  constructor(private formBuild: UntypedFormBuilder,
    private indicatorGroupService: IndicatorGroupService,
    private appService3: AppService
  ) {
    super(formBuild, appService3)
  }

  initTypes(): void {
    this.indicatorGroupService.getIndicatorGroups().subscribe(response => {
      this.types = response;
    });
  }
}
