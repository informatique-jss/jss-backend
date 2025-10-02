import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { ReportingWidget } from 'src/app/modules/reporting/model/ReportingWidget';
import { ReportingWidgetService } from 'src/app/modules/reporting/services/reporting-widget.service';
import { AppService } from 'src/app/services/app.service';
import { ConstantService } from '../../../services/constant.service';
import { GenericLocalAutocompleteComponent } from '../generic-local-autocomplete/generic-local-autocomplete.component';

@Component({
  selector: 'autocomplete-reporting-widget',
  templateUrl: '../generic-local-autocomplete/generic-local-autocomplete.component.html',
  styleUrls: ['../generic-local-autocomplete/generic-local-autocomplete.component.css'],
})
export class AutocompleteReportingWidgetComponent extends GenericLocalAutocompleteComponent<ReportingWidget> implements OnInit {

  types: ReportingWidget[] = [] as Array<ReportingWidget>;

  constructor(private formBuild: UntypedFormBuilder, private reportingWidgetService: ReportingWidgetService,
    private constantService: ConstantService, private appService3: AppService) {
    super(formBuild, appService3)
  }


  filterEntities(types: ReportingWidget[], value: string): ReportingWidget[] {
    const filterValue = (value != undefined && value != null && value.toLowerCase != undefined) ? value.toLowerCase() : "";
    return types.filter(country =>
      country.label != undefined && (country.label.toLowerCase().includes(filterValue)));
  }

  initTypes(): void {
    this.reportingWidgetService.getReportingWidgets().subscribe(response => {
      this.types = response;
    });
  }
}
