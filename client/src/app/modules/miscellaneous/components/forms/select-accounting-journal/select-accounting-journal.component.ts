import { ChangeDetectorRef, Component, Input, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { AccountingJournal } from 'src/app/modules/accounting/model/AccountingJournal';
import { AccountingJournalService } from 'src/app/modules/accounting/services/accounting.journal.service';
import { GenericSelectComponent } from '../generic-select/generic-select.component';

@Component({
  selector: 'select-accounting-journal',
  templateUrl: './select-accounting-journal.component.html',
  styleUrls: ['./select-accounting-journal.component.css']
})
export class SelectAccountingJournalComponent extends GenericSelectComponent<AccountingJournal> implements OnInit {

  types: AccountingJournal[] = [] as Array<AccountingJournal>;

  @Input() excludedJournals: string[] | undefined;

  constructor(private changeDetectorRef: ChangeDetectorRef,
    private formBuild: UntypedFormBuilder,
    private accountingJournalService: AccountingJournalService) {
    super(changeDetectorRef, formBuild);
  }

  initTypes(): void {
    this.accountingJournalService.getAccountingJournals().subscribe(response => {
      if (this.excludedJournals) {
        this.types = [] as Array<AccountingJournal>;
        for (let type of response) {
          let excludedItem = false;
          for (let excluded of this.excludedJournals) {
            if (excluded == type.code)
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
