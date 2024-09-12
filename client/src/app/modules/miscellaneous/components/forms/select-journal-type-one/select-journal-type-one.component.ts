import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { JournalType } from 'src/app/modules/quotation/model/JournalType';
import { JournalTypeService } from 'src/app/modules/quotation/services/journal.type.service';
import { GenericSelectComponent } from '../generic-select/generic-select.component';
import { AppService } from 'src/app/services/app.service';

@Component({
  selector: 'select-journal-type-one',
  templateUrl: './select-journal-type-one.component.html',
  styleUrls: ['./select-journal-type-one.component.css']
})
export class SelectJournalTypeOneComponent extends GenericSelectComponent<JournalType> implements OnInit {

  types: JournalType[] = [] as Array<JournalType>;

  constructor(private formBuild: UntypedFormBuilder, private journalTypeService: JournalTypeService, private appService3: AppService) {
    super(formBuild, appService3)
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
