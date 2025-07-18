import { Component, OnInit, TemplateRef, ViewChild } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { NgbAccordionModule, NgbDropdownModule, NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { Subscription } from 'rxjs';
import { CUSTOMER_ORDER_STATUS_ABANDONED, CUSTOMER_ORDER_STATUS_BEING_PROCESSED, CUSTOMER_ORDER_STATUS_BILLED, CUSTOMER_ORDER_STATUS_OPEN, CUSTOMER_ORDER_STATUS_PAYED, CUSTOMER_ORDER_STATUS_TO_BILLED, CUSTOMER_ORDER_STATUS_WAITING_DEPOSIT } from '../../../../libs/Constants';
import { capitalizeName, formatDateFrance } from '../../../../libs/FormatHelper';
import { SHARED_IMPORTS } from '../../../../libs/SharedImports';
import { AppService } from '../../../main/services/app.service';
import { UserPreferenceService } from '../../../main/services/user.preference.service';
import { AssoAffaireOrder } from '../../model/AssoAffaireOrder';
import { CustomerOrder } from '../../model/CustomerOrder';
import { InvoiceLabelResult } from '../../model/InvoiceLabelResult';
import { MailComputeResult } from '../../model/MailComputeResult';
import { Service } from '../../model/Service';
import { AssoAffaireOrderService } from '../../services/asso.affaire.order.service';
import { CustomerOrderService } from '../../services/customer.order.service';
import { InvoiceLabelResultService } from '../../services/invoice.label.result.service';
import { MailComputeResultService } from '../../services/mail.compute.result.service';

declare var bootstrap: any;

@Component({
  selector: 'app-orders',
  templateUrl: './orders.component.html',
  styleUrls: ['./orders.component.css'],
  standalone: true,
  imports: [SHARED_IMPORTS, NgbDropdownModule, NgbAccordionModule]
})
export class OrdersComponent implements OnInit {
  @ViewChild('cancelQuotationModal') cancelQuotationModal!: TemplateRef<any>;
  cancelQuotationModalInstance: any | undefined;

  statusFilterOpen: boolean = false;
  statusFilterWaitingDeposit: boolean = false;
  statusFilterBeingProcessed: boolean = false;
  statusFilterBilled: boolean = false;
  statusFilterToBilled: boolean = false;
  statusFilterPayed: boolean = false;

  currentSort: string = "createdDateDesc";
  currentPage: number = 0;

  orders: CustomerOrder[] = [];

  hideSeeMore: boolean = false;
  isFirstLoading: boolean = false;

  capitalizeName = capitalizeName;
  CUSTOMER_ORDER_STATUS_BILLED = CUSTOMER_ORDER_STATUS_BILLED;
  CUSTOMER_ORDER_STATUS_DRAFT = CUSTOMER_ORDER_STATUS_OPEN;

  ordersAssoAffaireOrders: AssoAffaireOrder[][] = [];
  ordersInvoiceLabelResult: InvoiceLabelResult[] = [];
  ordersMailComputeResult: MailComputeResult[] = [];
  currentSearchRef: Subscription | undefined;

  constructor(
    private customerOrderService: CustomerOrderService,
    private assoAffaireOrderService: AssoAffaireOrderService,
    private appService: AppService,
    private invoiceLabelResultService: InvoiceLabelResultService,
    private mailComputeResultService: MailComputeResultService,
    private userPreferenceService: UserPreferenceService,
    private activatedRoute: ActivatedRoute,
    private modalService: NgbModal
  ) { }

  ngOnInit() {
    this.retrieveBookmark();
    this.refreshOrders();
  }

  refreshOrders() {
    if (!this.statusFilterOpen && !this.statusFilterWaitingDeposit && !this.statusFilterBeingProcessed && !this.statusFilterToBilled && !this.statusFilterBilled && !this.statusFilterPayed) {
      this.orders = [];
      return;
    }

    this.setBookmark();

    let inputSearchStatus = this.activatedRoute.snapshot.params['statusCode'];
    if (inputSearchStatus) {
      this.statusFilterOpen = false;
      this.statusFilterWaitingDeposit = false;
      this.statusFilterBeingProcessed = false;
      this.statusFilterBilled = false;
      this.statusFilterToBilled = false;
      this.statusFilterPayed = false;

      if (inputSearchStatus == CUSTOMER_ORDER_STATUS_BEING_PROCESSED)
        this.statusFilterBeingProcessed = true;
      if (inputSearchStatus == CUSTOMER_ORDER_STATUS_OPEN)
        this.statusFilterOpen = true;
      if (inputSearchStatus == CUSTOMER_ORDER_STATUS_BILLED)
        this.statusFilterBilled = true;
    }


    let status: string[] = [];
    if (this.statusFilterOpen)
      status.push(CUSTOMER_ORDER_STATUS_OPEN);
    if (this.statusFilterWaitingDeposit)
      status.push(CUSTOMER_ORDER_STATUS_WAITING_DEPOSIT);
    if (this.statusFilterBeingProcessed)
      status.push(CUSTOMER_ORDER_STATUS_BEING_PROCESSED);
    if (this.statusFilterToBilled)
      status.push(CUSTOMER_ORDER_STATUS_TO_BILLED);
    if (this.statusFilterBilled)
      status.push(CUSTOMER_ORDER_STATUS_BILLED);
    if (this.statusFilterPayed)
      status.push(CUSTOMER_ORDER_STATUS_PAYED);

    if (this.currentPage == 0)
      this.isFirstLoading = true;

    if (this.currentSearchRef)
      this.currentSearchRef.unsubscribe();

    this.appService.showLoadingSpinner();
    this.currentSearchRef = this.customerOrderService.searchOrdersForCurrentUser(status, this.currentPage, this.currentSort).subscribe(response => {
      this.appService.hideLoadingSpinner();
      if (response) {
        this.orders.push(...response);
        if (response.length < 10)
          this.hideSeeMore = true;
      }
      this.isFirstLoading = false;
    })
  }

  changeFilter() {
    this.currentPage = 0;
    this.orders = [];
    this.hideSeeMore = false;
    this.refreshOrders();
  }

  changeSort(sorter: string) {
    this.currentPage = 0;
    this.orders = [];
    this.currentSort = sorter;
    this.hideSeeMore = false;
    this.refreshOrders();
  }

  loadMore() {
    this.currentPage++;
    this.refreshOrders();
  }

  loadOrderDetails(order: CustomerOrder) {
    if (!this.ordersAssoAffaireOrders[order.id]) {
      this.assoAffaireOrderService.getAssoAffaireOrdersForCustomerOrder(order).subscribe(response => {
        this.ordersAssoAffaireOrders[order.id] = response;
      })
      this.invoiceLabelResultService.getInvoiceLabelComputeResultForCustomerOrder(order.id).subscribe(response => {
        this.ordersInvoiceLabelResult[order.id] = response;
      })
      this.mailComputeResultService.getMailComputeResultForBillingForCustomerOrder(order.id).subscribe(response => {
        this.ordersMailComputeResult[order.id] = response;
      })
    }
  }

  openOrderDetails(event: any, order: CustomerOrder) {
    this.appService.openRoute(event, "account/orders/details/" + order.id, undefined);
  }


  quotationToCancel: CustomerOrder | undefined;
  finalCancelDraft(event: any) {
    if (this.quotationToCancel && this.quotationToCancel.id) {
      this.appService.showLoadingSpinner();
      this.customerOrderService.cancelCustomerOrder(this.quotationToCancel.id).subscribe(response => {
        this.quotationToCancel = undefined;
        this.currentPage = 0;
        this.orders = [];
        this.refreshOrders();
      });
    }
  }

  cancelDraft(order: CustomerOrder) {
    if (this.cancelQuotationModalInstance) {
      return;
    }

    this.quotationToCancel = order;
    this.cancelQuotationModalInstance = this.modalService.open(this.cancelQuotationModal, {
    });

    this.cancelQuotationModalInstance.result.finally(() => {
      this.cancelQuotationModalInstance = undefined;
    });
  }

  setBookmark() {
    this.userPreferenceService.setUserSearchBookmark(this.statusFilterOpen, "order-statusFilterOpen");
    this.userPreferenceService.setUserSearchBookmark(this.statusFilterWaitingDeposit, "order-statusFilterWaitingDeposit");
    this.userPreferenceService.setUserSearchBookmark(this.statusFilterBeingProcessed, "order-statusFilterBeingProcessed");
    this.userPreferenceService.setUserSearchBookmark(this.statusFilterBilled, "order-statusFilterBilled");
    this.userPreferenceService.setUserSearchBookmark(this.statusFilterToBilled, "order-statusFilterToBilled");
    this.userPreferenceService.setUserSearchBookmark(this.statusFilterPayed, "order-statusFilterPayed");
    this.userPreferenceService.setUserSearchBookmark(this.currentSort, "order-currentSort");
  }

  retrieveBookmark() {
    this.currentSort = this.userPreferenceService.getUserSearchBookmark("order-currentSort");
    if (!this.currentSort)
      this.currentSort = "createdDateDesc";

    let atLeastOne = false;
    this.statusFilterOpen = false;
    this.statusFilterWaitingDeposit = false;
    this.statusFilterBeingProcessed = false;
    this.statusFilterBilled = false;
    this.statusFilterToBilled = false;
    this.statusFilterPayed = false;

    if (this.userPreferenceService.getUserSearchBookmark("order-statusFilterOpen")) {
      this.statusFilterOpen = true;
      atLeastOne = true;
    }
    if (this.userPreferenceService.getUserSearchBookmark("order-statusFilterWaitingDeposit")) {
      this.statusFilterWaitingDeposit = true;
      atLeastOne = true;
    }
    if (this.userPreferenceService.getUserSearchBookmark("order-statusFilterBeingProcessed")) {
      this.statusFilterBeingProcessed = true;
      atLeastOne = true;
    }
    if (this.userPreferenceService.getUserSearchBookmark("order-statusFilterToBilled")) {
      this.statusFilterToBilled = true;
      atLeastOne = true;
    }
    if (this.userPreferenceService.getUserSearchBookmark("order-statusFilterBilled")) {
      this.statusFilterBilled = true;
      atLeastOne = true;
    }
    if (this.userPreferenceService.getUserSearchBookmark("order-statusFilterPayed")) {
      this.statusFilterPayed = true;
      atLeastOne = true;
    }

    if (!atLeastOne)
      this.statusFilterBeingProcessed = true;
  }

  getCustomerOrderStatusLabel = getCustomerOrderStatusLabel;
  getClassForCustomerOrderStatus = getClassForCustomerOrderStatus;
  getLastMissingAttachmentQueryDateLabel = getLastMissingAttachmentQueryDateLabel;
  getCustomerOrderBillingMailList(order: CustomerOrder) {
    return getCustomerOrderBillingMailList(this.ordersMailComputeResult[order.id]);
  }
}


export function getCustomerOrderStatusLabel(order: CustomerOrder) {
  if (order.customerOrderStatus.label) {
    if (order.customerOrderStatus.code == CUSTOMER_ORDER_STATUS_OPEN)
      return "Brouillon";
    if (order.customerOrderStatus.code == CUSTOMER_ORDER_STATUS_BILLED && order.isPayed)
      return "Payée";
    return order.customerOrderStatus.label;
  }
  return "";
}

export function getClassForCustomerOrderStatus(order: CustomerOrder) {
  if (order.customerOrderStatus.code == CUSTOMER_ORDER_STATUS_OPEN)
    return "bg-dark text-dark";
  if (order.customerOrderStatus.code == CUSTOMER_ORDER_STATUS_WAITING_DEPOSIT)
    return "bg-danger text-danger";
  if (order.customerOrderStatus.code == CUSTOMER_ORDER_STATUS_BEING_PROCESSED)
    return "bg-info text-info";
  if (order.customerOrderStatus.code == CUSTOMER_ORDER_STATUS_TO_BILLED)
    return "bg-info text-info";
  if (order.customerOrderStatus.code == CUSTOMER_ORDER_STATUS_BILLED && order.isPayed)
    return "bg-success text-success";
  if (order.customerOrderStatus.code == CUSTOMER_ORDER_STATUS_BILLED)
    return "bg-ok text-dark";
  if (order.customerOrderStatus.code == CUSTOMER_ORDER_STATUS_ABANDONED)
    return "bg-danger text-danger";
  return "bg-dark text-light";
}

export function getCustomerOrderBillingMailList(mailComputeResult: MailComputeResult) {
  let listMail = [];
  if (mailComputeResult) {
    if (mailComputeResult.recipientsMailCc)
      for (let recipient of mailComputeResult.recipientsMailTo)
        listMail.push(recipient.mail);
    for (let recipient of mailComputeResult.recipientsMailCc)
      listMail.push(recipient.mail);
  }
  return listMail.join(", ");
}

export function getLastMissingAttachmentQueryDateLabel(service: Service) {
  if (service && service.lastMissingAttachmentQueryDateTime)
    return formatDateFrance(service.lastMissingAttachmentQueryDateTime);
  return "";
}
