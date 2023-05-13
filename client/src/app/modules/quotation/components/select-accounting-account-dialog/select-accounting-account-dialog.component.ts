import { Component, Input, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatLegacyDialogRef as MatDialogRef } from '@angular/material/legacy-dialog';
import { AccountingAccount } from 'src/app/modules/accounting/model/AccountingAccount';
import { PrincipalAccountingAccount } from 'src/app/modules/accounting/model/PrincipalAccountingAccount';
import { ConstantService } from 'src/app/modules/miscellaneous/services/constant.service';

@Component({
  selector: 'app-select-accounting-account-dialog',
  templateUrl: './select-accounting-account-dialog.component.html',
  styleUrls: ['./select-accounting-account-dialog.component.css']
})
export class SelectAccountingAccountDialogComponent implements OnInit {


  constructor(public dialogRef: MatDialogRef<SelectAccountingAccountDialogComponent>,
    private constantService: ConstantService,
    private formBuilder: FormBuilder) { }

  choosedAccountingAccount: AccountingAccount | undefined;
  title: string = "Compte comptable cible";
  text: string = "Merci d'indiquer le compte comptable de banque cible : ";

  @Input() filteredAccountPrincipal: PrincipalAccountingAccount = this.constantService.getPrincipalAccountingAccountBank();

  accountingAccountForm = this.formBuilder.group({
  });

  ngOnInit() {
  }

  onConfirm(): void {
    this.dialogRef.close(this.choosedAccountingAccount);
  }

  onClose(): void {
    this.dialogRef.close(null);
  }
}
