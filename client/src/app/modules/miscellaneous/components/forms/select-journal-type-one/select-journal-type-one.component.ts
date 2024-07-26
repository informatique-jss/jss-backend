import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { JournalType } from 'src/app/modules/quotation/model/JournalType';
import { JournalTypeService } from 'src/app/modules/quotation/services/journal.type.service';
import { GenericSelectComponent } from '../generic-select/generic-select.component';

@Component({
  selector: 'select-journal-type-one',
  templateUrl: './select-journal-type-one.component.html',
  styleUrls: ['./select-journal-type-one.component.css']
})
export class SelectJournalTypeOneComponent extends GenericSelectComponent<JournalType> implements OnInit {

  types: JournalType[] = [] as Array<JournalType>;

  constructor(private formBuild: UntypedFormBuilder, private journalTypeService: JournalTypeService,) {
    super(formBuild)
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
