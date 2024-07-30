import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { RecordType } from 'src/app/modules/quotation/model/RecordType';
import { RecordTypeService } from 'src/app/modules/quotation/services/record.type.service';
import { GenericRadioGroupComponent } from '../generic-radio-group/generic-radio-group.component';
import { AppService } from 'src/app/services/app.service';

@Component({
  selector: 'radio-group-record-type',
  templateUrl: '../generic-radio-group/generic-radio-group-code.component.html',
  styleUrls: ['../generic-radio-group/generic-radio-group.component.css']
})
export class RadioGroupRecordTypeComponent extends GenericRadioGroupComponent<RecordType> implements OnInit {
  types: RecordType[] = [] as Array<RecordType>;

  constructor(
    private formBuild: UntypedFormBuilder, private recordtypeService: RecordTypeService, private appService3: AppService) {
    super(formBuild, appService3);
  }

  initTypes(): void {
    this.recordtypeService.getRecordTypes().subscribe(response => { this.types = response })
  }
}
