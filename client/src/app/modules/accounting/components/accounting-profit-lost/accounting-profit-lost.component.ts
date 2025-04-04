import { CdkDragEnd, Point } from '@angular/cdk/drag-drop';
import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { ConstantService } from 'src/app/modules/miscellaneous/services/constant.service';
import { AppService } from 'src/app/services/app.service';
import { UserPreferenceService } from 'src/app/services/user.preference.service';
import { AccountingBalanceSearch } from '../../model/AccountingBalanceSearch';
import { AccountingBalanceViewTitle } from '../../model/AccountingBalanceViewTitle';
import { AccountingBalanceViewTitleService } from '../../services/accounting.balance.view.title.service';

@Component({
  selector: 'accounting-profit-lost',
  templateUrl: './accounting-profit-lost.component.html',
  styleUrls: ['./accounting-profit-lost.component.css']
})
export class AccountingProfitLostComponent implements OnInit {

  accountingBalanceSearch: AccountingBalanceSearch = {} as AccountingBalanceSearch;
  profitAndLost: AccountingBalanceViewTitle[] | undefined;

  constructor(private accountingBalanceViewTitleService: AccountingBalanceViewTitleService,
    private formBuilder: FormBuilder,
    private appService: AppService,
    private userPreferenceService: UserPreferenceService,
    private constantService: ConstantService
  ) { }

  currentUserPosition: Point = { x: 0, y: 0 };

  ngOnInit() {
    this.accountingBalanceSearch.startDate = new Date(new Date().getFullYear(), 0, 1);
    this.accountingBalanceSearch.endDate = new Date(new Date().getFullYear(), 11, 31);
  }

  accountingprofitAndLostForm = this.formBuilder.group({
  });

  putEndDateSameYear() {
    if (this.accountingBalanceSearch.startDate && this.accountingBalanceSearch.endDate
      && this.accountingBalanceSearch.startDate.getFullYear() != this.accountingBalanceSearch.endDate.getFullYear()) {
      this.accountingBalanceSearch.endDate = new Date(this.accountingBalanceSearch.startDate.getFullYear(), 11, 31);
    }
  }

  refreshprofitAndLost() {
    if (!this.accountingBalanceSearch.startDate || !this.accountingBalanceSearch.endDate) {
      this.appService.displaySnackBar("🙄 Merci de saisir une plage de recherche", false, 10);
      return;
    }
    this.restoreTotalDivPosition();
    this.accountingBalanceViewTitleService.getProfitLost(this.accountingBalanceSearch).subscribe(response => {
      this.profitAndLost = response;
    })
  }

  getSoldeN(subtitle: AccountingBalanceViewTitle): number {
    let total = 0;
    if (subtitle && subtitle.items)
      for (let item of subtitle.items)
        if (item.soldeN)
          total += item.soldeN;
    return total;
  }

  getSoldeN1(subtitle: AccountingBalanceViewTitle): number {
    let total = 0;
    if (subtitle && subtitle.items)
      for (let item of subtitle.items)
        if (item.soldeN1)
          total += item.soldeN1;
    return total;
  }

  setCurentMonth() {
    let d = new Date();
    this.accountingBalanceSearch.startDate = new Date(d.getFullYear(), d.getMonth(), 1, 12, 0, 0);
    let d2 = new Date();
    this.accountingBalanceSearch.endDate = new Date(d2.getFullYear(), d2.getMonth() + 1, 0, 12, 0, 0);
  }

  setCurentFiscalYear() {
    let d = new Date(this.constantService.getDateAccountingClosureForAll());
    this.accountingBalanceSearch.startDate = new Date(d.getFullYear(), d.getMonth(), d.getDate(), 12, 0, 0);
    let d2 = new Date();
    this.accountingBalanceSearch.endDate = new Date(d2.getFullYear() + 1, 0, 1, 12, 0, 0);
  }

  dropTotalDiv(event: CdkDragEnd) {
    this.userPreferenceService.setUserTotalDivPosition(event.source.getFreeDragPosition());
  }

  restoreTotalDivPosition() {
    this.currentUserPosition = this.userPreferenceService.getUserTotalDivPosition();
  }

  restoreDefaultTotalDivPosition() {
    this.userPreferenceService.setUserTotalDivPosition({ x: 0, y: 0 });
    this.restoreTotalDivPosition();
  }

  exportProfitAndLost() {
    if (this.accountingBalanceSearch.startDate && this.accountingBalanceSearch.endDate)
      this.accountingBalanceViewTitleService.exportProfitAndLost(this.accountingBalanceSearch.startDate, this.accountingBalanceSearch.endDate);
  }
}

