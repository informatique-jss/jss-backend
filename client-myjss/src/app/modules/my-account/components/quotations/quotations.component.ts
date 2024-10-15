import { Component, OnInit } from '@angular/core';
import { AppService } from '../../../../libs/app.service';
import { CUSTOMER_ORDER_STATUS_ABANDONED, QUOTATION_STATUS_ABANDONED, QUOTATION_STATUS_OPEN, QUOTATION_STATUS_QUOTATION_WAITING_CONFRERE, QUOTATION_STATUS_REFUSED_BY_CUSTOMER, QUOTATION_STATUS_SENT_TO_CUSTOMER, QUOTATION_STATUS_TO_VERIFY, QUOTATION_STATUS_VALIDATED_BY_CUSTOMER } from '../../../../libs/Constants';
import { capitalizeName } from '../../../../libs/FormatHelper';
import { UserPreferenceService } from '../../../../libs/user.preference.service';
import { AssoAffaireOrder } from '../../model/AssoAffaireOrder';
import { InvoiceLabelResult } from '../../model/InvoiceLabelResult';
import { MailComputeResult } from '../../model/MailComputeResult';
import { Quotation } from '../../model/Quotation';
import { AssoAffaireOrderService } from '../../services/asso.affaire.order.service';
import { InvoiceLabelResultService } from '../../services/invoice.label.result.service';
import { MailComputeResultService } from '../../services/mail.compute.result.service';
import { QuotationService } from '../../services/quotation.service';

declare var bootstrap: any;

@Component({
  selector: 'app-quotations',
  templateUrl: './quotations.component.html',
  styleUrls: ['./quotations.component.css']
})
export class QuotationsComponent implements OnInit {

  statusFilterOpen: boolean = false;
  statusFilterToVerify: boolean = false;
  statusFilterWaitingConfrere: boolean = false;
  statusFilterSendToCustomer: boolean = true;
  statusFilterValidatedByCustomer: boolean = false;
  statusFilterRefusedByCustomer: boolean = false;
  statusFilterAbandonned: boolean = false;

  currentSort: string = "createdDateDesc";
  currentPage: number = 0;

  quotations: Quotation[] = [];

  hideSeeMore: boolean = false;
  isFirstLoading: boolean = false;

  capitalizeName = capitalizeName;

  quotationsAssoAffaireOrders: AssoAffaireOrder[][] = [];
  quotationsInvoiceLabelResult: InvoiceLabelResult[] = [];
  quotationsMailComputeResult: MailComputeResult[] = [];

  QUOTATION_STATUS_REFUSED_BY_CUSTOMER = QUOTATION_STATUS_REFUSED_BY_CUSTOMER;
  QUOTATION_STATUS_VALIDATED_BY_CUSTOMER = QUOTATION_STATUS_VALIDATED_BY_CUSTOMER;

  constructor(
    private quotationService: QuotationService,
    private assoAffaireOrderService: AssoAffaireOrderService,
    private appService: AppService,
    private invoiceLabelResultService: InvoiceLabelResultService,
    private mailComputeResultService: MailComputeResultService,
    private userPreferenceService: UserPreferenceService,
  ) { }

  ngOnInit() {
    this.retrieveBookmark();
    this.refreshQuotations();
  }

  refreshQuotations() {
    if (!this.statusFilterOpen && !this.statusFilterToVerify && !this.statusFilterWaitingConfrere && !this.statusFilterSendToCustomer && !this.statusFilterValidatedByCustomer && !this.statusFilterRefusedByCustomer && !this.statusFilterAbandonned) {
      this.quotations = [];
      return;
    }

    this.setBookmark();

    let status: string[] = [];
    if (this.statusFilterOpen)
      status.push(QUOTATION_STATUS_OPEN);
    if (this.statusFilterToVerify)
      status.push(QUOTATION_STATUS_TO_VERIFY);
    if (this.statusFilterWaitingConfrere)
      status.push(QUOTATION_STATUS_QUOTATION_WAITING_CONFRERE);
    if (this.statusFilterSendToCustomer)
      status.push(QUOTATION_STATUS_SENT_TO_CUSTOMER);
    if (this.statusFilterValidatedByCustomer)
      status.push(QUOTATION_STATUS_VALIDATED_BY_CUSTOMER);
    if (this.statusFilterRefusedByCustomer)
      status.push(QUOTATION_STATUS_REFUSED_BY_CUSTOMER);
    if (this.statusFilterAbandonned)
      status.push(QUOTATION_STATUS_ABANDONED);

    if (this.currentPage == 0)
      this.isFirstLoading = true;

    this.quotationService.searchQuotationsForCurrentUser(status, this.currentPage, this.currentSort).subscribe(response => {
      this.quotations.push(...response);
      this.isFirstLoading = false;
      this.initTooltips();
      if (response.length < 51)
        this.hideSeeMore = true;
    })
  }

  changeFilter() {
    this.currentPage = 0;
    this.quotations = [];
    this.hideSeeMore = false;
    this.refreshQuotations();
  }

  changeSort(sorter: string) {
    this.currentPage = 0;
    this.quotations = [];
    this.currentSort = sorter;
    this.hideSeeMore = false;
    this.refreshQuotations();
  }

  loadMore() {
    this.currentPage++;
    this.refreshQuotations();
  }

  loadQuotationDetails(event: any, quotation: Quotation) {
    if (!this.quotationsAssoAffaireOrders[quotation.id]) {
      this.assoAffaireOrderService.getAssoAffaireOrdersForQuotation(quotation).subscribe(response => {
        this.quotationsAssoAffaireOrders[quotation.id] = response;
        this.initTooltips();
      })
      this.invoiceLabelResultService.getInvoiceLabelComputeResultForQuotation(quotation).subscribe(response => {
        this.quotationsInvoiceLabelResult[quotation.id] = response;
      })
      this.mailComputeResultService.getMailComputeResultForBillingForQuotation(quotation).subscribe(response => {
        this.quotationsMailComputeResult[quotation.id] = response;
      })
    }
  }

  initTooltips() {
    setTimeout(() => {
      var tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
      var tooltipList = tooltipTriggerList.map(function (tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl, { placement: "right" })
      })
    }, 0);
  }

  getQuotationStatusLabel(quotation: Quotation) {
    if (quotation.quotationStatus.label) {
      if (quotation.quotationStatus.code == QUOTATION_STATUS_SENT_TO_CUSTOMER)
        return "En attente de votre validation";
      if (quotation.quotationStatus.code == QUOTATION_STATUS_TO_VERIFY)
        return "En cours de vérification";
      return quotation.quotationStatus.label;
    }
    return "";
  }

  getClassForQuotationStatus(quotation: Quotation) {
    if (quotation.quotationStatus.code == QUOTATION_STATUS_OPEN)
      return "bg-dark text-dark";
    if (quotation.quotationStatus.code == QUOTATION_STATUS_TO_VERIFY)
      return "bg-info text-info";
    if (quotation.quotationStatus.code == QUOTATION_STATUS_QUOTATION_WAITING_CONFRERE)
      return "bg-info text-info";
    if (quotation.quotationStatus.code == QUOTATION_STATUS_SENT_TO_CUSTOMER)
      return "bg-danger text-danger";
    if (quotation.quotationStatus.code == QUOTATION_STATUS_VALIDATED_BY_CUSTOMER)
      return "bg-success text-success";
    if (quotation.quotationStatus.code == QUOTATION_STATUS_REFUSED_BY_CUSTOMER)
      return "bg-danger text-danger";
    if (quotation.quotationStatus.code == QUOTATION_STATUS_ABANDONED)
      return "bg-dark text-dark";
    if (quotation.quotationStatus.code == CUSTOMER_ORDER_STATUS_ABANDONED)
      return "bg-danger text-danger";
    return "bg-light text-light";
  }

  getQuotationBillingMailList(quotation: Quotation) {
    let listMail = [];
    if (this.quotationsMailComputeResult[quotation.id]) {
      if (this.quotationsMailComputeResult[quotation.id].recipientsMailCc)
        for (let recipient of this.quotationsMailComputeResult[quotation.id].recipientsMailTo)
          listMail.push(recipient.mail);
      for (let recipient of this.quotationsMailComputeResult[quotation.id].recipientsMailCc)
        listMail.push(recipient.mail);
    }
    return listMail.join(", ");
  }

  openQuotationDetails(event: any, quotation: Quotation) {
    this.appService.openRoute(event, "account/quotations/details/" + quotation.id, undefined);
  }

  getQuotationValidityDate(quotation: Quotation) {
    return new Date(new Date(quotation.createdDate).getFullYear(), 11, 31);
  }

  setBookmark() {
    this.userPreferenceService.setUserSearchBookmark(this.statusFilterOpen, "quotation-statusFilterOpen");
    this.userPreferenceService.setUserSearchBookmark(this.statusFilterToVerify, "quotation-statusFilterToVerify");
    this.userPreferenceService.setUserSearchBookmark(this.statusFilterWaitingConfrere, "quotation-statusFilterWaitingConfrere");
    this.userPreferenceService.setUserSearchBookmark(this.statusFilterSendToCustomer, "quotation-statusFilterSendToCustomer");
    this.userPreferenceService.setUserSearchBookmark(this.statusFilterValidatedByCustomer, "quotation-statusFilterValidatedByCustomer");
    this.userPreferenceService.setUserSearchBookmark(this.statusFilterRefusedByCustomer, "quotation-statusFilterRefusedByCustomer");
    this.userPreferenceService.setUserSearchBookmark(this.statusFilterAbandonned, "quotation-statusFilterAbandonned");
    this.userPreferenceService.setUserSearchBookmark(this.currentSort, "quotation-currentSort");
  }

  retrieveBookmark() {
    this.currentSort = this.userPreferenceService.getUserSearchBookmark("quotation-currentSort");
    if (!this.currentSort)
      this.currentSort = "createdDateDesc";

    let atLeastOne = false;
    this.statusFilterOpen = false;
    this.statusFilterToVerify = false;
    this.statusFilterWaitingConfrere = false;
    this.statusFilterSendToCustomer = false;
    this.statusFilterValidatedByCustomer = false;
    this.statusFilterRefusedByCustomer = false;
    this.statusFilterAbandonned = false;

    if (this.userPreferenceService.getUserSearchBookmark("quotation-statusFilterOpen")) {
      this.statusFilterOpen = true;
      atLeastOne = true;
    }
    if (this.userPreferenceService.getUserSearchBookmark("quotation-statusFilterToVerify")) {
      this.statusFilterToVerify = true;
      atLeastOne = true;
    }
    if (this.userPreferenceService.getUserSearchBookmark("quotation-statusFilterWaitingConfrere")) {
      this.statusFilterWaitingConfrere = true;
      atLeastOne = true;
    }
    if (this.userPreferenceService.getUserSearchBookmark("quotation-statusFilterSendToCustomer")) {
      this.statusFilterSendToCustomer = true;
      atLeastOne = true;
    }
    if (this.userPreferenceService.getUserSearchBookmark("quotation-statusFilterValidatedByCustomer")) {
      this.statusFilterValidatedByCustomer = true;
      atLeastOne = true;
    }
    if (this.userPreferenceService.getUserSearchBookmark("quotation-statusFilterRefusedByCustomer")) {
      this.statusFilterRefusedByCustomer = true;
      atLeastOne = true;
    }
    if (this.userPreferenceService.getUserSearchBookmark("quotation-statusFilterAbandonned")) {
      this.statusFilterAbandonned = true;
      atLeastOne = true;
    }

    if (!atLeastOne)
      this.statusFilterSendToCustomer = true;
  }
}

