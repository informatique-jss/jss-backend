import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { AppService } from 'src/app/services/app.service';
import { AccountingBalanceSearch } from '../../model/AccountingBalanceSearch';
import { AccountingBalanceViewTitle } from '../../model/AccountingBalanceViewTitle';
import { AccountingBalanceViewTitleService } from '../../services/accounting.balance.view.title.service';

@Component({
  selector: 'accounting-bilan',
  templateUrl: './accounting-bilan.component.html',
  styleUrls: ['./accounting-bilan.component.css']
})
export class AccountingBilanComponent implements OnInit {

  accountingBalanceSearch: AccountingBalanceSearch = {} as AccountingBalanceSearch;
  bilan: AccountingBalanceViewTitle[] | undefined;

  constructor(private accountingBalanceViewTitleService: AccountingBalanceViewTitleService,
    private formBuilder: FormBuilder,
    private appService: AppService,
  ) { }

  ngOnInit() {
    this.accountingBalanceSearch.startDate = new Date(new Date().getFullYear(), 0, 1);
    this.accountingBalanceSearch.endDate = new Date(new Date().getFullYear(), 11, 31);
  }

  accountingBilanForm = this.formBuilder.group({
  });

  putEndDateSameYear() {
    if (this.accountingBalanceSearch.startDate && this.accountingBalanceSearch.endDate
      && this.accountingBalanceSearch.startDate.getFullYear() != this.accountingBalanceSearch.endDate.getFullYear()) {
      this.accountingBalanceSearch.endDate = new Date(this.accountingBalanceSearch.startDate.getFullYear(), 11, 31);
    }
  }

  refreshBilan() {
    if (!this.accountingBalanceSearch.startDate || !this.accountingBalanceSearch.endDate) {
      this.appService.displaySnackBar("🙄 Merci de saisir une plage de recherche", false, 10);
      return;
    }
    this.accountingBalanceViewTitleService.getBilan(this.accountingBalanceSearch).subscribe(response => {
      this.bilan = response;
    })
  }

  setCurentMonth() {
    let d = new Date();
    this.accountingBalanceSearch.startDate = new Date(d.getFullYear(), d.getMonth(), 1, 12, 0, 0);
    let d2 = new Date();
    this.accountingBalanceSearch.endDate = new Date(d2.getFullYear(), d2.getMonth() + 1, 0, 12, 0, 0);
  }

  setCurentFiscalYear() {
    let d = new Date();
    this.accountingBalanceSearch.startDate = new Date(d.getFullYear(), 0, 1, 12, 0, 0);
    let d2 = new Date();
    this.accountingBalanceSearch.endDate = new Date(d2.getFullYear() + 1, 0, 0, 12, 0, 0);
  }

  exportBilan() {
    if (this.accountingBalanceSearch.startDate && this.accountingBalanceSearch.endDate)
      this.accountingBalanceViewTitleService.exportBilan(this.accountingBalanceSearch.startDate, this.accountingBalanceSearch.endDate);
  }

  getBrutN(subtitle: AccountingBalanceViewTitle): number {
    let total = 0;
    if (subtitle && subtitle.items)
      for (let item of subtitle.items)
        if (item.brutN)
          total += item.brutN;
    return total;
  }

  getBrutN1(subtitle: AccountingBalanceViewTitle): number {
    let total = 0;
    if (subtitle && subtitle.items)
      for (let item of subtitle.items)
        if (item.brutN1)
          total += item.brutN1;
    return total;
  }

  getAmortissementN(subtitle: AccountingBalanceViewTitle): number {
    let total = 0;
    if (subtitle && subtitle.items)
      for (let item of subtitle.items)
        if (item.amortissementN)
          total += item.amortissementN;
    return total;
  }

  getAmortissementN1(subtitle: AccountingBalanceViewTitle): number {
    let total = 0;
    if (subtitle && subtitle.items)
      for (let item of subtitle.items)
        if (item.amortissementN1)
          total += item.amortissementN1;
    return total;
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


}
