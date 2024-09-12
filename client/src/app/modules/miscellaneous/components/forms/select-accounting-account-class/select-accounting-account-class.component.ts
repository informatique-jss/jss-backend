import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { AccountingAccountClass } from 'src/app/modules/accounting/model/AccountingAccountClass';
import { AccountingAccountClassService } from 'src/app/modules/accounting/services/accounting.account.class.service';
import { GenericSelectComponent } from '../generic-select/generic-select.component';
import { AppService } from 'src/app/services/app.service';

@Component({
  selector: 'select-accounting-account-class',
  templateUrl: './select-accounting-account-class.component.html',
  styleUrls: ['./select-accounting-account-class.component.css']
})
export class SelectAccountingAccountClassComponent extends GenericSelectComponent<AccountingAccountClass> implements OnInit {

  types: AccountingAccountClass[] = [] as Array<AccountingAccountClass>;

  constructor(private formBuild: UntypedFormBuilder, private accountingAccountClassService: AccountingAccountClassService, private appService3: AppService) {
    super(formBuild, appService3)
  }

  initTypes(): void {
    this.accountingAccountClassService.getAccountingAccountClasses().subscribe(response => {
      this.types = response;
    })
  }
}
