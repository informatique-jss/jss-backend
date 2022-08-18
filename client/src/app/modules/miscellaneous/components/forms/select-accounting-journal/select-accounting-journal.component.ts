import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
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

  constructor(private changeDetectorRef: ChangeDetectorRef,
    private formBuild: UntypedFormBuilder,
    private accountingJournalService: AccountingJournalService) {
    super(changeDetectorRef, formBuild);
  }

  initTypes(): void {
    this.accountingJournalService.getAccountingJournals().subscribe(response => {
      this.types = response;
    })
  }
}
