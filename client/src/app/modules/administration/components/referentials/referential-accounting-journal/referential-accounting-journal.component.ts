import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import { AppService } from 'src/app/app.service';
import { AccountingJournal } from 'src/app/modules/accounting/model/AccountingJournal';
import { AccountingJournalService } from 'src/app/modules/accounting/services/accounting.journal.service';
import { GenericReferentialComponent } from '../generic-referential/generic-referential-component';

@Component({
  selector: 'referential-accounting-journal',
  templateUrl: './../generic-referential/generic-referential.component.html',
  styleUrls: ['./../generic-referential/generic-referential.component.css']
})
export class ReferentialAccountingJournalComponent extends GenericReferentialComponent<AccountingJournal> implements OnInit {
  constructor(private accountingJournalService: AccountingJournalService,
    private formBuilder2: FormBuilder,
    private appService2: AppService,) {
    super(formBuilder2, appService2);
  }

  getAddOrUpdateObservable(): Observable<AccountingJournal> {
    return this.accountingJournalService.addOrUpdateAccountingJournal(this.selectedEntity!);
  }
  getGetObservable(): Observable<AccountingJournal[]> {
    return this.accountingJournalService.getAccountingJournals();
  }
}
