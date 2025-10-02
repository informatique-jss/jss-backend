import { Component, OnInit } from '@angular/core';
import { NgbDropdownModule } from '@ng-bootstrap/ng-bootstrap';
import { capitalizeName } from '../../../../libs/FormatHelper';
import { SHARED_IMPORTS } from '../../../../libs/SharedImports';
import { TrustHtmlPipe } from '../../../../libs/TrustHtmlPipe';
import { AppService } from '../../../main/services/app.service';
import { PlatformService } from '../../../main/services/platform.service';
import { Responsable } from '../../../profile/model/Responsable';
import { LoginService } from '../../../profile/services/login.service';
import { ResponsableService } from '../../../profile/services/responsable.service';
import { Affaire } from '../../model/Affaire';
import { BillingClosureReceiptValue } from '../../model/BillingClosureReceiptValue';
import { CustomerOrder } from '../../model/CustomerOrder';
import { BillingClosureService } from '../../services/billing.closure.service';
import { CustomerOrderService } from '../../services/customer.order.service';

@Component({
  selector: 'app-billing-closure',
  templateUrl: './billing-closure.component.html',
  styleUrls: ['./billing-closure.component.css'],
  standalone: true,
  imports: [SHARED_IMPORTS, TrustHtmlPipe, NgbDropdownModule]
})
export class BillingClosureComponent implements OnInit {

  currentUser: Responsable | undefined;
  receiptValues: BillingClosureReceiptValue[] | undefined;
  currentSort: string = "createdDateAsc";
  isFirstLoading: boolean = true;
  orderToPayInCb: number[] = [];
  totalToPayCb: number = 0;
  responsablesForCurrentUser: Responsable[] | undefined;
  responsableCheck: boolean[] = [];

  capitalizeName = capitalizeName;

  constructor(
    private loginService: LoginService,
    private billingClosureService: BillingClosureService,
    private customerOrderService: CustomerOrderService,
    private platformService: PlatformService,
    private appService: AppService,
    private responsableService: ResponsableService
  ) { }

  ngOnInit() {
    this.loginService.getCurrentUser().subscribe(currentUser => {
      this.currentUser = currentUser;
      this.refreshClosure();

      this.responsableService.getResponsablesForCurrentUser().subscribe(response => {
        this.responsablesForCurrentUser = response;
        if (this.responsablesForCurrentUser)
          for (let respo of this.responsablesForCurrentUser) {
            this.responsableCheck[respo.id] = true;
          }
      });
    })
  }

  refreshClosure() {
    this.receiptValues = [];
    this.isFirstLoading = true;
    if (this.currentUser) {
      this.billingClosureService.getBillingClosureReceiptValueForResponsable(this.currentUser.id, false, this.currentSort == 'createdDateDesc').subscribe(values => {
        this.receiptValues = [];
        if (values) {
          this.receiptValues = values.filter((b: BillingClosureReceiptValue) => b.eventDateTime);
          if (this.responsableCheck && this.responsableCheck.length > 0) {
            this.receiptValues = this.receiptValues.filter(b => !b.responsable || this.responsableCheck[b.responsable.id]);
          }
        }

        this.allAffaires = [];
        this.allResponsables = [];
        this.getAllAffaire();
        this.getAllResponsables();
        this.isFirstLoading = false;
      });
    }
  }

  exportReceipt() {
    if (this.currentUser) {
      this.appService.showLoadingSpinner();
      this.billingClosureService.downloadBillingClosureReceiptValueForResponsable(this.currentUser.id);
      this.appService.hideLoadingSpinner();
    }
  }

  changeFilter() {
    this.receiptValues = [];
    this.isFirstLoading = true;
    this.refreshClosure();
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
    if (this.currentSort.indexOf('createdDate') >= 0)
      this.refreshClosure();
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

  downloadInvoice(idCustomerOrder: number) {
    this.customerOrderService.downloadInvoice({ id: idCustomerOrder } as CustomerOrder);
  }

  addToPayCb(receiptValue: BillingClosureReceiptValue) {
    if (this.orderToPayInCb && this.orderToPayInCb.indexOf(receiptValue.idCustomerOrder) < 0 && receiptValue.debitAmount) {
      this.orderToPayInCb.push(receiptValue.idCustomerOrder);
      if (receiptValue.remainingDebitAmount)
        this.totalToPayCb += receiptValue.remainingDebitAmount;
      else
        this.totalToPayCb += receiptValue.debitAmount;
    }
  }

  removeFromPayCb(receiptValue: BillingClosureReceiptValue) {
    if (this.orderToPayInCb && this.orderToPayInCb.indexOf(receiptValue.idCustomerOrder) >= 0 && receiptValue.debitAmount) {
      this.orderToPayInCb.splice(this.orderToPayInCb.indexOf(receiptValue.idCustomerOrder));
      if (receiptValue.remainingDebitAmount)
        this.totalToPayCb -= receiptValue.remainingDebitAmount;
      else
        this.totalToPayCb -= receiptValue.debitAmount;
    }
  }

  payCb() {
    if (this.orderToPayInCb) {
      this.appService.showLoadingSpinner();
      this.customerOrderService.getCardPaymentLinkForPaymentInvoices(this.orderToPayInCb).subscribe(link => {
        this.appService.hideLoadingSpinner();
        if (this.platformService.isBrowser())
          this.platformService.getNativeWindow()!.open(link.link, "_blank");
        this.orderToPayInCb = [];
        this.totalToPayCb = 0;
      });
    }
  }

}
