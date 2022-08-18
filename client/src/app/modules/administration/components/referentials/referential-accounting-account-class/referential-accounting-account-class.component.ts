import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import { AppService } from 'src/app/app.service';
import { AccountingAccountClass } from 'src/app/modules/accounting/model/AccountingAccountClass';
import { AccountingAccountClassService } from 'src/app/modules/accounting/services/accounting.account.class.service';
import { GenericReferentialComponent } from '../generic-referential/generic-referential-component';

@Component({
  selector: 'referential-accounting-account-class',
  templateUrl: './../generic-referential/generic-referential.component.html',
  styleUrls: ['./../generic-referential/generic-referential.component.css']
})
export class ReferentialAccountingAccountClassComponent extends GenericReferentialComponent<AccountingAccountClass> implements OnInit {
  constructor(private accountingAccountClassService: AccountingAccountClassService,
    private formBuilder2: FormBuilder,
    private appService2: AppService,) {
    super(formBuilder2, appService2);
  }

  getAddOrUpdateObservable(): Observable<AccountingAccountClass> {
    return this.accountingAccountClassService.addOrUpdateAccountingAccountClass(this.selectedEntity!);
  }
  getGetObservable(): Observable<AccountingAccountClass[]> {
    return this.accountingAccountClassService.getAccountingAccountClasses();
  }
}
