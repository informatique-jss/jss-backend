import { Component, OnInit } from '@angular/core';
import { AppService } from '../../../../libs/app.service';
import { CUSTOMER_ORDER_STATUS_BEING_PROCESSED, CUSTOMER_ORDER_STATUS_BILLED, CUSTOMER_ORDER_STATUS_OPEN, CUSTOMER_ORDER_STATUS_PAYED, CUSTOMER_ORDER_STATUS_TO_BILLED, CUSTOMER_ORDER_STATUS_WAITING_DEPOSIT } from '../../../../libs/Constants';
import { capitalizeName, formatDateFrance } from '../../../../libs/FormatHelper';
import { UserPreferenceService } from '../../../../libs/user.preference.service';
import { UserScope } from '../../../profile/model/UserScope';
import { UserScopeService } from '../../../profile/services/user.scope.service';
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
  styleUrls: ['./orders.component.css']
})
export class OrdersComponent implements OnInit {

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

  currentScope: UserScope[] = [];

  capitalizeName = capitalizeName;
  CUSTOMER_ORDER_STATUS_BILLED = CUSTOMER_ORDER_STATUS_BILLED;

  ordersAssoAffaireOrders: AssoAffaireOrder[][] = [];
  ordersInvoiceLabelResult: InvoiceLabelResult[] = [];
  ordersMailComputeResult: MailComputeResult[] = [];

  constructor(
    private customerOrderService: CustomerOrderService,
    private assoAffaireOrderService: AssoAffaireOrderService,
    private appService: AppService,
    private invoiceLabelResultService: InvoiceLabelResultService,
    private mailComputeResultService: MailComputeResultService,
    private userPreferenceService: UserPreferenceService,
    private userScopeService: UserScopeService,
  ) { }

  ngOnInit() {
    this.retrieveBookmark();
    this.refreshOrders();
    this.userScopeService.getUserScope().subscribe(response => {
      this.currentScope = response;
    })
  }

  refreshOrders() {
    if (!this.statusFilterOpen && !this.statusFilterWaitingDeposit && !this.statusFilterBeingProcessed && !this.statusFilterToBilled && !this.statusFilterBilled && !this.statusFilterPayed) {
      this.orders = [];
      return;
    }

    this.setBookmark();

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

    this.customerOrderService.searchOrdersForCurrentUser(status, this.currentPage, this.currentSort).subscribe(response => {
      if (response) {
        this.orders.push(...response);
        if (response.length < 51)
          this.hideSeeMore = true;
      }
      this.isFirstLoading = false;
      initTooltips();
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

  loadOrderDetails(event: any, order: CustomerOrder) {
    if (!this.ordersAssoAffaireOrders[order.id]) {
      this.assoAffaireOrderService.getAssoAffaireOrdersForCustomerOrder(order).subscribe(response => {
        this.ordersAssoAffaireOrders[order.id] = response;
        initTooltips();
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
    return "bg-dark text-dark";
  return "bg-light text-light";
}

export function initTooltips(forcedPlacement: string = 'right') {
  setTimeout(() => {
    try {
      if (bootstrap == undefined)
        return;
    } catch {
      return;
    }
    var tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    var tooltipList = tooltipTriggerList.map(function (tooltipTriggerEl) {
      return new bootstrap.Tooltip(tooltipTriggerEl, { placement: forcedPlacement })
    })
  }, 0);
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
