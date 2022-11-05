import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { RecordType } from 'src/app/modules/quotation/model/RecordType';
import { RecordTypeService } from 'src/app/modules/quotation/services/record.type.service';
import { UserNoteService } from 'src/app/services/user.notes.service';
import { GenericRadioGroupComponent } from '../generic-radio-group/generic-radio-group.component';

@Component({
  selector: 'radio-group-record-type',
  templateUrl: './radio-group-record-type.component.html',
  styleUrls: ['./radio-group-record-type.component.css']
})
export class RadioGroupRecordTypeComponent extends GenericRadioGroupComponent<RecordType> implements OnInit {
  types: RecordType[] = [] as Array<RecordType>;

  constructor(
    private formBuild: UntypedFormBuilder, private recordtypeService: RecordTypeService, private userNoteService2: UserNoteService) {
    super(formBuild, userNoteService2);
  }

  initTypes(): void {
    this.recordtypeService.getRecordTypes().subscribe(response => { this.types = response })
  }
}
