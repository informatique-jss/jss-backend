import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { REPORTING_DATASET_ANNOUNCEMENT, REPORTING_DATASET_CUSTOMER_ORDER, REPORTING_DATASET_PROVISION, REPORTING_DATASET_PROVISION_PRODUCTION, REPORTING_DATASET_QUOTATION, REPORTING_DATASET_RECOVERY, REPORTING_DATASET_TIERS, REPORTING_DATASET_TURNOVER_VAT_AMOUNT } from 'src/app/libs/Constants';
import { REPORTING_DATASET_TURNOVER_AMOUNT } from '../../../../../libs/Constants';
import { GenericSelectComponent } from '../generic-select/generic-select.component';
import { AppService } from 'src/app/services/app.service';

@Component({
  selector: 'select-reporting-dataset',
  templateUrl: '../generic-select/generic-select.component.html',
  styleUrls: ['../generic-select/generic-select.component.css']
})
export class SelectReportingDatasetComponent extends GenericSelectComponent<string> implements OnInit {

  types: string[] = [] as Array<string>;

  constructor(private formBuild: UntypedFormBuilder, private appService3: AppService) {
    super(formBuild, appService3)
  }

  initTypes(): void {
    this.types = [];
    this.types.push(REPORTING_DATASET_QUOTATION);
    this.types.push(REPORTING_DATASET_TURNOVER_AMOUNT);
    this.types.push(REPORTING_DATASET_CUSTOMER_ORDER);
    this.types.push(REPORTING_DATASET_PROVISION);
    this.types.push(REPORTING_DATASET_ANNOUNCEMENT);
    this.types.push(REPORTING_DATASET_TIERS);
    this.types.push(REPORTING_DATASET_TURNOVER_VAT_AMOUNT);
    this.types.push(REPORTING_DATASET_PROVISION_PRODUCTION);
    this.types.push(REPORTING_DATASET_RECOVERY);
    this.types = this.types.sort((a, b) => a.localeCompare(b));
  }

  compareWithId = this.compareWithLabel;

  compareWithLabel(o1: any, o2: any): boolean {
    if (o1 == null && o2 != null || o1 != null && o2 == null)
      return false;
    if (o1 && o2)
      return o1 == o2;
    return false
  }
}
