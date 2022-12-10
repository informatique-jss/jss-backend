import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { Journal } from 'src/app/modules/pao/model/Journal';
import { JournalService } from 'src/app/modules/pao/services/journal.service';
import { UserNoteService } from 'src/app/services/user.notes.service';
import { formatDate } from '../../../../../libs/FormatHelper';
import { GenericSelectComponent } from '../generic-select/generic-select.component';

@Component({
  selector: 'select-journal',
  templateUrl: './../generic-select/generic-select.component.html',
  styleUrls: ['./../generic-select/generic-select.component.css']
})
export class SelectJournalComponent extends GenericSelectComponent<Journal> implements OnInit {

  types: Journal[] = [] as Array<Journal>;

  constructor(private formBuild: UntypedFormBuilder, private journalService: JournalService, private userNoteService2: UserNoteService,) {
    super(formBuild, userNoteService2)
  }

  initTypes(): void {
    this.journalService.getJournals().subscribe(response => {
      this.types = response.sort(function (a: Journal, b: Journal) {
        return new Date(b.journalDate).getTime() - new Date(a.journalDate).getTime();
      });
    })
  }

  displayLabel(object: any): string {
    return object ? (formatDate(object.journalDate) + " - " + object.label) : '';
  }
}
