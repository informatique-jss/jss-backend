import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { PRINT_LABEL_RECIPIENT_AC, PRINT_LABEL_RECIPIENT_CUSTOMER } from 'src/app/libs/Constants';
import { AppService } from 'src/app/services/app.service';
import { GenericSelectComponent } from '../generic-select/generic-select.component';

@Component({
  selector: 'select-print-label-recipient',
  templateUrl: '../generic-select/generic-select.component.html',
  styleUrls: ['../generic-select/generic-select.component.css']
})

export class SelectPrintLabelRecipientComponent extends GenericSelectComponent<string> implements OnInit {
  types: string[] = [] as Array<string>;

  constructor(private formBuild: UntypedFormBuilder, private appService3: AppService) {
    super(formBuild, appService3)
  }

  initTypes(): void {
    this.types = [];
    this.types.push(PRINT_LABEL_RECIPIENT_CUSTOMER);
    this.types.push(PRINT_LABEL_RECIPIENT_AC);
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

  displayLabel(object: string): string {
    if (!object)
      return "";
    return object;

  }
}
