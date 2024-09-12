import { Component, Input, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { AccountingJournal } from 'src/app/modules/accounting/model/AccountingJournal';
import { AccountingJournalService } from 'src/app/modules/accounting/services/accounting.journal.service';
import { GenericSelectComponent } from '../generic-select/generic-select.component';
import { AppService } from 'src/app/services/app.service';

@Component({
  selector: 'select-accounting-journal',
  templateUrl: './select-accounting-journal.component.html',
  styleUrls: ['./select-accounting-journal.component.css']
})
export class SelectAccountingJournalComponent extends GenericSelectComponent<AccountingJournal> implements OnInit {

  types: AccountingJournal[] = [] as Array<AccountingJournal>;

  @Input() excludedJournals: AccountingJournal[] | undefined;

  constructor(private formBuild: UntypedFormBuilder, private accountingJournalService: AccountingJournalService, private appService3: AppService) {
    super(formBuild, appService3)
  }

  initTypes(): void {
    this.accountingJournalService.getAccountingJournals().subscribe(response => {
      if (this.excludedJournals) {
        this.types = [] as Array<AccountingJournal>;
        for (let type of response) {
          let excludedItem = false;
          for (let excluded of this.excludedJournals) {
            if (excluded.id == type.id)
              excludedItem = true;
          }
          if (!excludedItem)
            this.types.push(type);
        }
        this.modelChange.emit(this.model);
      } else {
        this.types = response;
      }
    })
  }
}
