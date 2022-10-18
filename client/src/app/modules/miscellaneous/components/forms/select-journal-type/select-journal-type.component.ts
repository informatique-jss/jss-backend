import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { JournalType } from 'src/app/modules/quotation/model/JournalType';
import { JournalTypeService } from 'src/app/modules/quotation/services/journal.type.service';
import { UserNoteService } from 'src/app/services/user.notes.service';
import { GenericMultipleSelectComponent } from '../generic-select/generic-multiple-select.component';

@Component({
  selector: 'select-journal-type',
  templateUrl: './select-journal-type.component.html',
  styleUrls: ['./select-journal-type.component.css']
})
export class SelectJournalTypeComponent extends GenericMultipleSelectComponent<JournalType> implements OnInit {

  types: JournalType[] = [] as Array<JournalType>;

  constructor(private formBuild: UntypedFormBuilder, private journalTypeService: JournalTypeService, private userNoteService2: UserNoteService,) {
    super(formBuild, userNoteService2)
  }

  initTypes(): void {
    this.journalTypeService.getJournalTypes().subscribe(response => {
      this.types = response;
    })
  }

  displayLabel(object: any): string {
    return object ? (object.code + " - " + object.label) : '';
  }
}
