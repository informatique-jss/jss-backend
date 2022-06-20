import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { RecordType } from 'src/app/modules/quotation/model/RecordType';
import { RecordTypeService } from 'src/app/modules/quotation/services/record.type.service';
import { GenericRadioGroupComponent } from '../generic-radio-group/generic-radio-group.component';

@Component({
  selector: 'radio-group-record-type',
  templateUrl: './radio-group-record-type.component.html',
  styleUrls: ['./radio-group-record-type.component.css']
})
export class RadioGroupRecordTypeComponent extends GenericRadioGroupComponent<RecordType> implements OnInit {
  types: RecordType[] = [] as Array<RecordType>;

  constructor(
    private formBuild: FormBuilder, private recordtypeService: RecordTypeService) {
    super(formBuild);
  }

  initTypes(): void {
    this.recordtypeService.getRecordTypes().subscribe(response => { this.types = response })
  }
}
