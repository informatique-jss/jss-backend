import { Component, OnInit, TemplateRef, ViewChild } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { NgbAccordionModule, NgbDropdownModule, NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { Subscription } from 'rxjs';
import { QUOTATION_STATUS_ABANDONED, QUOTATION_STATUS_OPEN, QUOTATION_STATUS_QUOTATION_WAITING_CONFRERE, QUOTATION_STATUS_REFUSED_BY_CUSTOMER, QUOTATION_STATUS_SENT_TO_CUSTOMER, QUOTATION_STATUS_TO_VERIFY, QUOTATION_STATUS_VALIDATED_BY_CUSTOMER } from '../../../../libs/Constants';
import { capitalizeName } from '../../../../libs/FormatHelper';
import { SHARED_IMPORTS } from '../../../../libs/SharedImports';
import { AppService } from '../../../main/services/app.service';
import { UserPreferenceService } from '../../../main/services/user.preference.service';
import { AssoAffaireOrder } from '../../model/AssoAffaireOrder';
import { InvoiceLabelResult } from '../../model/InvoiceLabelResult';
import { MailComputeResult } from '../../model/MailComputeResult';
import { Quotation } from '../../model/Quotation';
import { AssoAffaireOrderService } from '../../services/asso.affaire.order.service';
import { InvoiceLabelResultService } from '../../services/invoice.label.result.service';
import { MailComputeResultService } from '../../services/mail.compute.result.service';
import { QuotationService } from '../../services/quotation.service';
import { getCustomerOrderBillingMailList } from '../orders/orders.component';

declare var bootstrap: any;

@Component({
  selector: 'app-quotations',
  templateUrl: './quotations.component.html',
  styleUrls: ['./quotations.component.css'],
  standalone: true,
  imports: [SHARED_IMPORTS, NgbDropdownModule, NgbAccordionModule]
})
export class QuotationsComponent implements OnInit {

  @ViewChild('cancelQuotationModal') cancelQuotationModal!: TemplateRef<any>;
  cancelQuotationModalInstance: any | undefined;

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
  QUOTATION_STATUS_OPEN = QUOTATION_STATUS_OPEN;

  currentSearchRef: Subscription | undefined;

  constructor(
    private quotationService: QuotationService,
    private assoAffaireOrderService: AssoAffaireOrderService,
    private appService: AppService,
    private invoiceLabelResultService: InvoiceLabelResultService,
    private mailComputeResultService: MailComputeResultService,
    private userPreferenceService: UserPreferenceService,
    private activatedRoute: ActivatedRoute,
    private modalService: NgbModal
  ) { }

  getQuotationStatusLabel = getQuotationStatusLabel;
  getClassForQuotationStatus = getClassForQuotationStatus;

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

    let inputSearchStatus = this.activatedRoute.snapshot.params['statusCode'];
    if (inputSearchStatus) {
      this.statusFilterOpen = false;
      this.statusFilterToVerify = false;
      this.statusFilterWaitingConfrere = false;
      this.statusFilterSendToCustomer = false;
      this.statusFilterValidatedByCustomer = false;
      this.statusFilterRefusedByCustomer = false;
      this.statusFilterAbandonned = false;

      if (inputSearchStatus == QUOTATION_STATUS_SENT_TO_CUSTOMER)
        this.statusFilterSendToCustomer = true;
      if (inputSearchStatus == QUOTATION_STATUS_OPEN)
        this.statusFilterOpen = true;
    }

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

    if (this.currentSearchRef)
      this.currentSearchRef.unsubscribe();

    this.appService.showLoadingSpinner();
    this.currentSearchRef = this.quotationService.searchQuotationsForCurrentUser(status, this.currentPage, this.currentSort).subscribe(response => {
      this.appService.hideLoadingSpinner();
      this.quotations.push(...response);
      this.isFirstLoading = false;
      if (response.length < 10)
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

  loadQuotationDetails(quotation: Quotation) {
    if (!this.quotationsAssoAffaireOrders[quotation.id]) {
      this.assoAffaireOrderService.getAssoAffaireOrdersForQuotation(quotation).subscribe(response => {
        this.quotationsAssoAffaireOrders[quotation.id] = response;
      })
      this.invoiceLabelResultService.getInvoiceLabelComputeResultForQuotation(quotation.id).subscribe(response => {
        this.quotationsInvoiceLabelResult[quotation.id] = response;
      })
      this.mailComputeResultService.getMailComputeResultForBillingForQuotation(quotation.id).subscribe(response => {
        this.quotationsMailComputeResult[quotation.id] = response;
      })
    }
  }

  getQuotationBillingMailList(quotation: Quotation) {
    return getCustomerOrderBillingMailList(this.quotationsMailComputeResult[quotation.id]);
  }

  openQuotationDetails(event: any, quotation: Quotation) {
    this.appService.openRoute(event, "account/quotations/details/" + quotation.id, undefined);
  }

  getQuotationValidityDate(quotation: Quotation) {
    return new Date(new Date(quotation.createdDate).getFullYear(), 11, 31);
  }


  quotationToCancel: Quotation | undefined;
  finalCancelDraft(event: any) {
    if (this.quotationToCancel && this.quotationToCancel.id) {
      this.appService.showLoadingSpinner();
      this.quotationService.cancelQuotation(this.quotationToCancel.id).subscribe(response => {
        if (this.quotationService.getCurrentDraftQuotationId() && parseInt(this.quotationService.getCurrentDraftQuotationId()!) == this.quotationToCancel!.id)
          this.quotationService.cleanStorageData();
        this.quotationToCancel = undefined;
        this.currentPage = 0;
        this.quotations = [];
        this.refreshQuotations();
      });
    }
  }

  cancelDraft(quotation: Quotation) {
    if (this.cancelQuotationModalInstance) {
      return;
    }

    this.quotationToCancel = quotation;
    this.cancelQuotationModalInstance = this.modalService.open(this.cancelQuotationModal, {
    });

    this.cancelQuotationModalInstance.result.finally(() => {
      this.cancelQuotationModalInstance = undefined;
    });
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


export function getQuotationStatusLabel(quotation: Quotation) {
  if (quotation.quotationStatus.label) {
    if (quotation.quotationStatus.code == QUOTATION_STATUS_SENT_TO_CUSTOMER)
      return "En attente de votre validation";
    if (quotation.quotationStatus.code == QUOTATION_STATUS_TO_VERIFY)
      return "En cours de vérification";
    return quotation.quotationStatus.label;
  }
  return "";
}

export function getClassForQuotationStatus(quotation: Quotation) {
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
  return "bg-dark text-light";
}
