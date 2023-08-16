import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { REPORTING_DATASET_CUSTOMER_ORDER, REPORTING_DATASET_QUOTATION } from 'src/app/libs/Constants';
import { UserNoteService } from 'src/app/services/user.notes.service';
import { REPORTING_DATASET_TIERS, REPORTING_DATASET_VAT } from '../../../../../libs/Constants';
import { GenericSelectComponent } from '../generic-select/generic-select.component';

@Component({
  selector: 'select-reporting-dataset',
  templateUrl: '../generic-select/generic-select.component.html',
  styleUrls: ['../generic-select/generic-select.component.css']
})
export class SelectReportingDatasetComponent extends GenericSelectComponent<string> implements OnInit {

  types: string[] = [] as Array<string>;

  constructor(private formBuild: UntypedFormBuilder,
    private userNoteService2: UserNoteService,) {
    super(formBuild, userNoteService2)
  }

  initTypes(): void {
    this.types = [];
    this.types.push(REPORTING_DATASET_CUSTOMER_ORDER);
    this.types.push(REPORTING_DATASET_VAT);
    this.types.push(REPORTING_DATASET_QUOTATION);
    this.types.push(REPORTING_DATASET_TIERS);
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
