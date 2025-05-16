import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { formatDateHourFrance } from 'src/app/libs/FormatHelper';
import { Indicator } from 'src/app/modules/reporting/model/Indicator';
import { IndicatorGroup } from 'src/app/modules/reporting/model/IndicatorGroup';
import { IndicatorGroupService } from 'src/app/modules/reporting/services/indicator-group.service';
import { IndicatorService } from 'src/app/modules/reporting/services/indicator.service';
import { AppService } from 'src/app/services/app.service';
import { GenericSelectComponent } from '../generic-select/generic-select.component';

@Component({
  selector: 'select-indicator',
  templateUrl: './select-indicator.component.html',
  styleUrls: ['./select-indicator.component.css']
})
export class SelectIndicatorComponent extends GenericSelectComponent<Indicator> implements OnInit {

  types: Indicator[] = [] as Array<Indicator>;
  indicatorGroups: IndicatorGroup[] = [];


  constructor(private formBuild: UntypedFormBuilder,
    private indicatorGroupService: IndicatorGroupService,
    private indicatorService: IndicatorService,
    private appService3: AppService
  ) {
    super(formBuild, appService3)
  }

  initTypes(): void {
    this.indicatorGroupService.getIndicatorGroups().subscribe(resGroup => {
      this.indicatorGroups = resGroup;
      this.indicatorService.getIndicators().subscribe(respnose => {
        this.types = respnose;
      })
    })
  }

  override displayLabel(object: Indicator): string {
    let label = object.label;
    if (object.lastUpdate)
      label += " (mis à jour le " + formatDateHourFrance(object.lastUpdate) + ")";
    return label;
  }
}



