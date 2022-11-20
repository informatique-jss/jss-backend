
import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { JournalType } from 'src/app/modules/quotation/model/JournalType';
import { JournalTypeService } from 'src/app/modules/quotation/services/journal.type.service';
import { UserNoteService } from 'src/app/services/user.notes.service';
import { GenericRadioGroupComponent } from '../generic-radio-group/generic-radio-group.component';

@Component({
  selector: 'radio-group-journal-type',
  templateUrl: '../generic-radio-group/generic-radio-group-code.component.html',
  styleUrls: ['../generic-radio-group/generic-radio-group.component.css']
})
export class RadioGroupJournalTypeComponent extends GenericRadioGroupComponent<JournalType> implements OnInit {
  types: JournalType[] = [] as Array<JournalType>;

  constructor(
    private formBuild: UntypedFormBuilder, private journalTypeService: JournalTypeService, private userNoteService2: UserNoteService) {
    super(formBuild, userNoteService2);
  }

  initTypes(): void {
    this.journalTypeService.getJournalTypes().subscribe(response => { this.types = response })
  }
}
