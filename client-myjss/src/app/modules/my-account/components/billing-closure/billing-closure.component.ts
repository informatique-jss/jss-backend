import { Component, OnInit } from '@angular/core';
import { NgbDropdownModule } from '@ng-bootstrap/ng-bootstrap';
import { combineLatest } from 'rxjs';
import { capitalizeName } from '../../../../libs/FormatHelper';
import { SHARED_IMPORTS } from '../../../../libs/SharedImports';
import { TrustHtmlPipe } from '../../../../libs/TrustHtmlPipe';
import { Responsable } from '../../../profile/model/Responsable';
import { UserScope } from '../../../profile/model/UserScope';
import { LoginService } from '../../../profile/services/login.service';
import { UserScopeService } from '../../../profile/services/user.scope.service';
import { Affaire } from '../../model/Affaire';
import { BillingClosureReceiptValue } from '../../model/BillingClosureReceiptValue';
import { BillingClosureService } from '../../services/billing.closure.service';

@Component({
  selector: 'app-billing-closure',
  templateUrl: './billing-closure.component.html',
  styleUrls: ['./billing-closure.component.css'],
  standalone: true,
  imports: [SHARED_IMPORTS, TrustHtmlPipe, NgbDropdownModule]
})
export class BillingClosureComponent implements OnInit {

  userScope: UserScope[] | undefined;
  currentUser: Responsable | undefined;
  userScopeSelected: boolean[] = [];
  receiptValues: BillingClosureReceiptValue[] | undefined;
  currentSort: string = "createdDateAsc";
  isFirstLoading: boolean = true;

  capitalizeName = capitalizeName;

  constructor(
    private userScopeService: UserScopeService,
    private loginService: LoginService,
    private billingClosureService: BillingClosureService,
  ) { }

  ngOnInit() {
    this.userScopeService.getUserScope().subscribe(response => {
      this.userScope = response;
      this.loginService.getCurrentUser().subscribe(currentUser => {
        this.currentUser = currentUser;
        if (this.userScope)
          for (let scope of this.userScope)
            this.userScopeSelected[scope.responsableViewed.id] = false;
        if (this.currentUser)
          this.userScopeSelected[this.currentUser.id] = true;
        this.refreshClosure();
      })
    })
  }

  refreshClosure() {
    let promises = [];
    if (this.userScope) {
      for (let id in this.userScopeSelected) {
        if (this.userScopeSelected[id]) {
          promises.push(this.billingClosureService.getBillingClosureReceiptValueForResponsable(parseInt(id), false));
        }
      }

      if (promises.length == 0)
        this.isFirstLoading = false;

      combineLatest(promises).subscribe(response => {
        this.receiptValues = [];
        if (response)
          for (let billingClosureValues of response)
            this.receiptValues.push(...billingClosureValues.filter((b: BillingClosureReceiptValue) => b.eventDateTime));

        this.allAffaires = [];
        this.allResponsables = [];
        this.getAllAffaire();
        this.getAllResponsables();
        this.isFirstLoading = false;
      })
    }


    // this.setBookmark();
  }


  changeFilter() {
    this.receiptValues = [];
    this.isFirstLoading = true;
    this.refreshClosure();
  }

  selectAll() {
    if (this.userScopeSelected)
      for (let selected in this.userScopeSelected)
        this.userScopeSelected[selected] = true;
    this.changeFilter();
  }

  unselectAll() {
    if (this.userScopeSelected)
      for (let selected in this.userScopeSelected)
        this.userScopeSelected[selected] = false;
    this.receiptValues = [];
    this.isFirstLoading = false;
  }

  getResponsableLabel(value: BillingClosureReceiptValue) {
    if (value && value.responsable)
      return this.getResponsableLabelForResponsable(value.responsable);
    return "";
  }

  getAffaireLabelForAffaire(affaire: Affaire) {
    if (affaire)
      if (affaire.denomination)
        return affaire.denomination;
      else
        return affaire.firstname + ' ' + affaire.lastname;
    return "";
  }

  getResponsableLabelForResponsable(responsable: Responsable) {
    if (responsable)
      return responsable.firstname + ' ' + responsable.lastname;
    return "";
  }

  allAffaires: string[] = [];
  getAllAffaire(): string[] {
    if (this.allAffaires && this.allAffaires.length > 0)
      return this.allAffaires;

    if (this.receiptValues)
      for (let value of this.receiptValues)
        if (value.affaireLists && this.allAffaires.indexOf(value.affaireLists) < 0)
          this.allAffaires.push(value.affaireLists);

    this.allAffaires.sort((a: string, b: string) => a.localeCompare(b));
    return this.allAffaires;
  }

  allResponsables: Responsable[] = [];
  getAllResponsables(): Responsable[] {
    if (this.allResponsables && this.allResponsables.length > 0)
      return this.allResponsables;
    if (this.receiptValues)
      for (let value of this.receiptValues)
        if (value.responsable)
          this.allResponsables[value.responsable.id] = value.responsable;

    this.allResponsables.sort((a: Responsable, b: Responsable) => this.getResponsableLabelForResponsable(a).localeCompare(this.getResponsableLabelForResponsable(b)));

    let tempRespo = [];
    if (this.allResponsables)
      for (let responsable of this.allResponsables)
        tempRespo.push(responsable);

    this.allResponsables = tempRespo.filter(n => n);
    return this.allResponsables;
  }

  changeSort(sortType: string) {
    this.currentSort = sortType;
  }

  getTotalSolde(affaire: string | undefined, responsable: Responsable | undefined) {
    let solde = 0;
    if (this.receiptValues)
      for (let value of this.receiptValues) {
        if (!affaire && !responsable || affaire && affaire == value.affaireLists || responsable && value.responsable.id == responsable.id)
          if (value.creditAmount)
            solde += value.creditAmount;
          else
            solde -= value.debitAmount;
      }
    return solde;
  }

}
