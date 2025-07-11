import { CdkDragEnter, CdkDropList, DragRef, moveItemInArray } from '@angular/cdk/drag-drop';
import { Component, OnInit, ViewChild } from '@angular/core';
import { MatTabChangeEvent } from '@angular/material/tabs';
import { combineLatest, map } from 'rxjs';
import { CUSTOMER_ORDER_STATUS_BEING_PROCESSED, CUSTOMER_ORDER_STATUS_OPEN, CUSTOMER_ORDER_STATUS_TO_BILLED, CUSTOMER_ORDER_STATUS_WAITING_DEPOSIT, FORMALITE_AUTHORITY_REJECTED, FORMALITE_AUTHORITY_TECHNICAL_BLOCKING, FORMALITE_AUTHORITY_VALIDATED, FORMALITE_STATUS_WAITING_DOCUMENT, FORMALITE_STATUS_WAITING_DOCUMENT_AUTHORITY, FORMALITE_WAITING_FINAL_DOCUMENT_AUTHORITY, QUOTATION_STATUS_OPEN, QUOTATION_STATUS_REFUSED_BY_CUSTOMER, QUOTATION_STATUS_SENT_TO_CUSTOMER, QUOTATION_STATUS_TO_VERIFY, SIMPLE_PROVISION_STATUS_WAITING_DOCUMENT, SIMPLE_PROVISION_STATUS_WAITING_DOCUMENT_AUTHORITY, SIMPLE_PROVISION_WAITING_FINAL_DOCUMENT_AUTHORITY } from 'src/app/libs/Constants';
import { InvoiceSearch } from 'src/app/modules/invoicing/model/InvoiceSearch';
import { PaymentSearch } from 'src/app/modules/invoicing/model/PaymentSearch';
import { RefundSearch } from 'src/app/modules/invoicing/model/RefundSearch';
import { IWorkflowElement } from 'src/app/modules/miscellaneous/model/IWorkflowElement';
import { ConstantService } from 'src/app/modules/miscellaneous/services/constant.service';
import { Employee } from 'src/app/modules/profile/model/Employee';
import { EmployeeService } from 'src/app/modules/profile/services/employee.service';
import { AffaireSearch } from 'src/app/modules/quotation/model/AffaireSearch';
import { AnnouncementStatus } from 'src/app/modules/quotation/model/AnnouncementStatus';
import { CustomerOrderStatus } from 'src/app/modules/quotation/model/CustomerOrderStatus';
import { DomiciliationStatus } from 'src/app/modules/quotation/model/DomiciliationStatus';
import { FormaliteStatus } from 'src/app/modules/quotation/model/FormaliteStatus';
import { OrderingSearch } from 'src/app/modules/quotation/model/OrderingSearch';
import { OrderingSearchTagged } from 'src/app/modules/quotation/model/OrderingSearchTagged';
import { QuotationSearch } from 'src/app/modules/quotation/model/QuotationSearch';
import { QuotationStatus } from 'src/app/modules/quotation/model/QuotationStatus';
import { SimpleProvisionStatus } from 'src/app/modules/quotation/model/SimpleProvisonStatus';
import { AnnouncementStatusService } from 'src/app/modules/quotation/services/announcement.status.service';
import { CustomerOrderStatusService } from 'src/app/modules/quotation/services/customer.order.status.service';
import { DomiciliationStatusService } from 'src/app/modules/quotation/services/domiciliation-status.service';
import { FormaliteStatusService } from 'src/app/modules/quotation/services/formalite.status.service';
import { QuotationStatusService } from 'src/app/modules/quotation/services/quotation-status.service';
import { SimpleProvisionStatusService } from 'src/app/modules/quotation/services/simple.provision.status.service';
import { AppService } from 'src/app/services/app.service';
import { HabilitationsService } from 'src/app/services/habilitations.service';
import { UserPreferenceService } from 'src/app/services/user.preference.service';
import { ActiveDirectoryGroupService } from '../../../miscellaneous/services/active.directory.group.service';

@Component({
  selector: 'dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css'],
  standalone: false
})
export class DashboardComponent implements OnInit {
  @ViewChild(CdkDropList) placeholder!: CdkDropList;

  private target: CdkDropList | null = null;
  private targetIndex!: number;
  private source: CdkDropList | null = null;
  private sourceIndex!: number;
  private dragRef: DragRef | null = null;

  announcementStatus: AnnouncementStatus[] = [] as Array<AnnouncementStatus>;
  formaliteStatus: FormaliteStatus[] = [] as Array<FormaliteStatus>;
  simpleProvisionStatus: SimpleProvisionStatus[] = [] as Array<SimpleProvisionStatus>;
  domiciliationStatus: DomiciliationStatus[] = [] as Array<DomiciliationStatus>;
  statusTypes: IWorkflowElement<any>[] = [] as Array<IWorkflowElement<any>>;

  customerOrderStatus: CustomerOrderStatus[] = [] as Array<CustomerOrderStatus>;
  quotationStatus: QuotationStatus[] = [] as Array<QuotationStatus>;

  currentEmployee: Employee | undefined;
  items: Array<string> = [];
  itemsSize: Array<string> = [];
  checkboxes: any = [];
  boxSizesSelected: any = [];

  AFFAIRE_IN_PROGRESS = "Mes prestations en cours";
  AFFAIRE_TO_DO = "Mes prestations à faire";
  AFFAIRE_SIMPLE_PROVISION_WAITING_AUTHORITY = "Mes formalités simples en attente de l'autorité compétente"
  AFFAIRE_SIMPLE_PROVISION_AUTHORITY_REJECTED = "Mes formalités rejetées par l'AC"
  AFFAIRE_SIMPLE_PROVISION_AUTHORITY_VALIDATED = "Mes formalités validées par l'AC"
  AFFAIRE_WAITING_FINAL_DOCUMENT_AUTHORITY = "Mes formalités en attente d'éléments définitifs par l'AC"
  AFFAIRE_SIMPLE_PROVISION_STATUS_WAITING_DOCUMENT = "Mes formalités simples en attente de documents"
  AFFAIRE_MISSING_ATTACHMENT_QUERY_TO_MANUALLY_REMINDER = "Mes formalités en PM à relancer manuellement"
  AFFAIRE_DOCUMENT_AUTHORITY_TECHNICAL_BLOCKING = "Mes formalités en blocage technique par l'autorité compétente"

  affaireSearchInProgress: AffaireSearch = {} as AffaireSearch;
  affaireSearchToDo: AffaireSearch = {} as AffaireSearch;
  affaireSearchWaitingAuthority: AffaireSearch = {} as AffaireSearch;
  affaireSearcAuthorityRejected: AffaireSearch = {} as AffaireSearch;
  affaireSearcAuthorityValidated: AffaireSearch = {} as AffaireSearch;
  affairTechnicalBlockingAuthority: AffaireSearch = {} as AffaireSearch;
  affaireWaitingFinalDocumentAuthority: AffaireSearch = {} as AffaireSearch;
  affaireSearchWaitingDocument: AffaireSearch = {} as AffaireSearch;
  affaireSearchMissingAttachmentQueryManually: AffaireSearch = {} as AffaireSearch;

  ORDER_OPEN = "Mes commandes ouvertes";
  ALL_ORDER_OPEN = "Commandes ouvertes";
  ORDER_BEING_PROCESSED = "Mes commandes en cours";
  ORDER_TO_BILLED = "Commandes en attente de facturation";
  ORDERS_AWAITING_DEPOSIT = "Mes commandes en attente d’acompte";
  ORDER_GROUP_TAGGED_WITH_COMMENT = "Mes commandes où je suis tagué"

  orderingSearchOpen: OrderingSearch = {} as OrderingSearch;
  orderingSearchAllOpen: OrderingSearch = {} as OrderingSearch;
  orderingSearchBeingProcessed: OrderingSearch = {} as OrderingSearch;
  orderingSearchToBilled: OrderingSearch = {} as OrderingSearch;
  orderingSearchToAwaitingDeposit: OrderingSearch = {} as OrderingSearch;
  orderingSearchTagged: OrderingSearchTagged = {} as OrderingSearchTagged;

  QUOTATION_OPEN = "Mes devis ouverts";
  QUOTATION_TO_VERIFY = "Mes devis à vérifier";
  QUOTATION_REFUSED = "Mes devis refusés";
  QUOTATION_SENT = "Mes devis envoyés";
  quotationSearchOpen: QuotationSearch = {} as QuotationSearch;
  quotationSearchToVerify: QuotationSearch = {} as QuotationSearch;
  quotationSearchRefused: QuotationSearch = {} as QuotationSearch;
  quotationSearchSent: QuotationSearch = {} as QuotationSearch;

  INVOICE_TO_ASSOCIATE = "Factures à associer";
  invoiceSearchToAssociate: InvoiceSearch = {} as InvoiceSearch;

  PAYMENT_TO_ASSOCIATE = "Paiements entrants à associer";
  paymentSearchToAssociate: PaymentSearch = {} as PaymentSearch;

  REFUND_TO_EMIT = "Remboursements à émettre";
  refundSearchToEmit: RefundSearch = {} as RefundSearch;

  LOG_TO_REVIEW = "Logs à revoir";

  PROVISION_BOARD = "Suivi d'équipe";

  allItems: Array<string> = [this.QUOTATION_REFUSED, this.PAYMENT_TO_ASSOCIATE, this.INVOICE_TO_ASSOCIATE, this.QUOTATION_TO_VERIFY,
  this.QUOTATION_OPEN, this.ORDER_TO_BILLED, this.ORDER_BEING_PROCESSED, this.ORDERS_AWAITING_DEPOSIT, this.ORDER_OPEN, this.ALL_ORDER_OPEN, this.AFFAIRE_SIMPLE_PROVISION_WAITING_AUTHORITY,
  this.AFFAIRE_SIMPLE_PROVISION_STATUS_WAITING_DOCUMENT, this.AFFAIRE_IN_PROGRESS, this.AFFAIRE_TO_DO, this.QUOTATION_SENT, this.AFFAIRE_DOCUMENT_AUTHORITY_TECHNICAL_BLOCKING,
  this.PROVISION_BOARD, this.AFFAIRE_SIMPLE_PROVISION_AUTHORITY_REJECTED, this.AFFAIRE_SIMPLE_PROVISION_AUTHORITY_VALIDATED, this.AFFAIRE_WAITING_FINAL_DOCUMENT_AUTHORITY, this.AFFAIRE_MISSING_ATTACHMENT_QUERY_TO_MANUALLY_REMINDER, this.ORDER_GROUP_TAGGED_WITH_COMMENT].sort((a, b) => a.localeCompare(b));

  BOX_SIZE_X_SMALL = "Zoom très petit";
  BOX_SIZE_SMALL = "Zoom petit";
  BOX_SIZE_MEDIUM = "Zoom moyen";
  BOX_SIZE_LARGE = "Zoom grand";
  BOX_SIZE_X_LARGE = "Zoom très grand";
  BOX_SIZE_FULL_WIDTH = "Pleine largeur";

  allBoxSizes: Array<string> = [this.BOX_SIZE_X_SMALL, this.BOX_SIZE_SMALL, this.BOX_SIZE_MEDIUM, this.BOX_SIZE_LARGE, this.BOX_SIZE_X_LARGE, this.BOX_SIZE_FULL_WIDTH];

  constructor(private appService: AppService,
    private employeeService: EmployeeService,
    private formaliteStatusService: FormaliteStatusService,
    private domiciliationStatusService: DomiciliationStatusService,
    private announcementStatusService: AnnouncementStatusService,
    private simpleProvisionStatusService: SimpleProvisionStatusService,
    private userPreferenceService: UserPreferenceService,
    private customerOrderStatusService: CustomerOrderStatusService,
    private quotationStatusService: QuotationStatusService,
    private constantService: ConstantService,
    private habilitationsService: HabilitationsService,
    private activeDirectoryGroupService: ActiveDirectoryGroupService,
  ) { }

  ngOnInit() {
    this.restoreTab();
    this.appService.changeHeaderTitle("Tableau de bord");
    this.employeeService.getCurrentEmployee().subscribe(response => {
      this.currentEmployee = response;

      combineLatest([
        this.domiciliationStatusService.getDomiciliationStatus(),
        this.announcementStatusService.getAnnouncementStatus(),
        this.formaliteStatusService.getFormaliteStatus(),
        this.simpleProvisionStatusService.getSimpleProvisionStatus(),
        this.customerOrderStatusService.getCustomerOrderStatus(),
        this.quotationStatusService.getQuotationStatus()
      ]).pipe(
        map(([domiciliationStatus, announcementStatus, formaliteStatus, simpleProvisionStatus, customerOrderStatus, quotationStatus]) => ({ domiciliationStatus, announcementStatus, formaliteStatus, simpleProvisionStatus, customerOrderStatus, quotationStatus })),
      ).subscribe(response => {
        this.domiciliationStatus = response.domiciliationStatus;
        this.statusTypes.push(...response.domiciliationStatus);
        this.announcementStatus = response.announcementStatus;
        this.statusTypes.push(...response.announcementStatus);
        this.formaliteStatus = response.formaliteStatus;
        this.statusTypes.push(...response.formaliteStatus);
        this.simpleProvisionStatus = response.simpleProvisionStatus;
        this.statusTypes.push(...response.simpleProvisionStatus);
        this.customerOrderStatus = response.customerOrderStatus;
        this.quotationStatus = response.quotationStatus;

        this.affaireSearchInProgress.assignedTo = this.currentEmployee;
        this.affaireSearchInProgress.status = this.statusTypes.filter(stauts => !stauts.isOpenState && !stauts.isCloseState);

        this.affaireSearchToDo.assignedTo = this.currentEmployee;
        this.affaireSearchToDo.status = this.statusTypes.filter(stauts => stauts.isOpenState);

        this.affaireSearchWaitingDocument.assignedTo = this.currentEmployee;
        this.affaireSearchWaitingDocument.status = this.simpleProvisionStatus.filter(stauts => stauts.code == SIMPLE_PROVISION_STATUS_WAITING_DOCUMENT);
        this.affaireSearchWaitingDocument.status.push(...this.formaliteStatus.filter(stauts => stauts.code == FORMALITE_STATUS_WAITING_DOCUMENT));

        this.affaireSearchWaitingAuthority.assignedTo = this.currentEmployee;
        this.affaireSearchWaitingAuthority.status = this.simpleProvisionStatus.filter(stauts => stauts.code == SIMPLE_PROVISION_STATUS_WAITING_DOCUMENT_AUTHORITY);
        this.affaireSearchWaitingAuthority.status.push(...this.formaliteStatus.filter(stauts => stauts.code == FORMALITE_STATUS_WAITING_DOCUMENT_AUTHORITY));

        this.affaireSearcAuthorityRejected.assignedTo = this.currentEmployee;
        this.affaireSearcAuthorityRejected.status = this.formaliteStatus.filter(stauts => stauts.code == FORMALITE_AUTHORITY_REJECTED);

        this.affaireSearchMissingAttachmentQueryManually.commercial = this.currentEmployee;
        this.affaireSearchMissingAttachmentQueryManually.isMissingQueriesToManualRemind = true;

        this.affaireSearcAuthorityValidated.assignedTo = this.currentEmployee;
        this.affaireSearcAuthorityValidated.status = this.formaliteStatus.filter(stauts => stauts.code == FORMALITE_AUTHORITY_VALIDATED);

        this.affairTechnicalBlockingAuthority.assignedTo = this.currentEmployee;
        this.affairTechnicalBlockingAuthority.status = this.formaliteStatus.filter(stauts => stauts.code == FORMALITE_AUTHORITY_TECHNICAL_BLOCKING);

        this.affaireWaitingFinalDocumentAuthority.assignedTo = this.currentEmployee;
        this.affaireWaitingFinalDocumentAuthority.status = [];
        this.affaireWaitingFinalDocumentAuthority.status.push(...this.formaliteStatus.filter(stauts => stauts.code == FORMALITE_WAITING_FINAL_DOCUMENT_AUTHORITY), ...this.simpleProvisionStatus.filter(stauts => stauts.code == SIMPLE_PROVISION_WAITING_FINAL_DOCUMENT_AUTHORITY));

        this.orderingSearchOpen.salesEmployee = this.currentEmployee!;
        this.orderingSearchOpen.customerOrderStatus = [this.customerOrderStatusService.getCustomerStatusByCode(this.customerOrderStatus, CUSTOMER_ORDER_STATUS_OPEN)!];

        this.orderingSearchAllOpen.customerOrderStatus = [this.customerOrderStatusService.getCustomerStatusByCode(this.customerOrderStatus, CUSTOMER_ORDER_STATUS_OPEN)!];

        this.orderingSearchBeingProcessed.salesEmployee = this.currentEmployee!;
        this.orderingSearchBeingProcessed.customerOrderStatus = [this.customerOrderStatusService.getCustomerStatusByCode(this.customerOrderStatus, CUSTOMER_ORDER_STATUS_BEING_PROCESSED)!];

        this.orderingSearchToAwaitingDeposit.salesEmployee = this.currentEmployee!;
        this.orderingSearchToAwaitingDeposit.customerOrderStatus = [this.customerOrderStatusService.getCustomerStatusByCode(this.customerOrderStatus, CUSTOMER_ORDER_STATUS_WAITING_DEPOSIT)!];

        this.orderingSearchToBilled.customerOrderStatus = [this.customerOrderStatusService.getCustomerStatusByCode(this.customerOrderStatus, CUSTOMER_ORDER_STATUS_TO_BILLED)!];

        this.orderingSearchTagged.salesEmployee = this.currentEmployee!;
        this.activeDirectoryGroupService.getActiveDirectoryGroups().subscribe(response => {
          let activeDirectoryGroups = response;
          for (let activeDirectoryGroup of activeDirectoryGroups) {
            if (this.orderingSearchTagged.salesEmployee.adPath.includes(activeDirectoryGroup.activeDirectoryPath))
              this.orderingSearchTagged.activeDirectoryGroup = activeDirectoryGroup;
          }
        });
        this.orderingSearchTagged.isOnlyDisplayUnread = true;

        this.quotationSearchOpen.salesEmployee = this.currentEmployee!;
        this.quotationSearchOpen.quotationStatus = [this.quotationStatusService.getQuotationStatusByCode(this.quotationStatus, QUOTATION_STATUS_OPEN)!];

        this.quotationSearchToVerify.salesEmployee = this.currentEmployee!;
        this.quotationSearchToVerify.quotationStatus = [this.quotationStatusService.getQuotationStatusByCode(this.quotationStatus, QUOTATION_STATUS_TO_VERIFY)!];

        this.quotationSearchRefused.salesEmployee = this.currentEmployee!;
        this.quotationSearchRefused.quotationStatus = [this.quotationStatusService.getQuotationStatusByCode(this.quotationStatus, QUOTATION_STATUS_REFUSED_BY_CUSTOMER)!];

        this.quotationSearchSent.salesEmployee = this.currentEmployee!;
        this.quotationSearchSent.quotationStatus = [this.quotationStatusService.getQuotationStatusByCode(this.quotationStatus, QUOTATION_STATUS_SENT_TO_CUSTOMER)!];

        this.invoiceSearchToAssociate.invoiceStatus = [this.constantService.getInvoiceStatusSend()];

        this.paymentSearchToAssociate.isHideAssociatedPayments = true;
        this.paymentSearchToAssociate.minAmount = 0;

        this.refundSearchToEmit.isHideExportedRefunds = true;
        this.refundSearchToEmit.isHideMatchedRefunds = true;

        if (this.canViewLogModule())
          this.allItems.push(this.LOG_TO_REVIEW);

        // restore bookmark
        let bookmark = this.userPreferenceService.getUserSearchBookmark("dashboard") as Array<string>;
        let bookmarkSize = this.userPreferenceService.getUserSearchBookmark("dashboardSize") as Array<string>;
        if (bookmark)
          this.items = bookmark;
        if (bookmarkSize)
          this.itemsSize = bookmarkSize;
        if (!this.itemsSize || this.itemsSize.length == 0)
          this.itemsSize.push(this.BOX_SIZE_SMALL);

        // clean items that doesn't exist anymore
        for (let item of this.items)
          if (this.allItems.indexOf(item) < 0)
            this.items.splice(this.items.indexOf(item));
        for (let size of this.itemsSize)
          if (this.allBoxSizes.indexOf(size) < 0)
            this.itemsSize.splice(this.itemsSize.indexOf(size));

        // init checkboxes
        for (let allItem of this.allItems)
          this.checkboxes.push({ id: allItem, value: false })

        // init sizes
        for (let allBoxSize of this.allBoxSizes)
          this.boxSizesSelected.push({ id: allBoxSize, value: false })

        this.updateCheckboxes();
        this.updateCheckboxesBoxSizes();

      });

    })
  }

  toggleDisplay(table: string) {
    if (this.items.indexOf(table) >= 0)
      this.items.splice(this.items.indexOf(table), 1);
    else
      this.items.push(table);
    this.userPreferenceService.setUserSearchBookmark(this.items, "dashboard");
    setTimeout(() => {
      this.updateCheckboxes();
    });
  }

  toggleDisplaySize(size: string) {
    if (this.itemsSize.indexOf(size) >= 0)
      return;

    this.itemsSize = [];
    this.itemsSize.push(size);
    this.userPreferenceService.setUserSearchBookmark(this.itemsSize, "dashboardSize");
    setTimeout(() => {
      this.updateCheckboxesBoxSizes();
    });
  }

  canViewLogModule(): boolean {
    return this.habilitationsService.canViewLogModule();
  }

  updateCheckboxes() {
    for (let checkbox of this.checkboxes) {
      let found = false;
      if (this.items)
        for (let item of this.items)
          if (item == checkbox.id)
            found = true;
      checkbox.value = found;
    }
  }

  updateCheckboxesBoxSizes() {
    let currentSize = "";
    for (let boxSize of this.boxSizesSelected) {
      let found = false;
      if (this.itemsSize)
        for (let size of this.itemsSize)
          if (size == boxSize.id) {
            found = true;
            currentSize = size;
          }
      boxSize.value = found;
    }
    if (currentSize == this.BOX_SIZE_X_SMALL) {
      this.boxWidth = '300px';
      this.boxHeight = '150px';
    } else if (currentSize == this.BOX_SIZE_SMALL) {
      this.boxWidth = '400px';
      this.boxHeight = '300px';
    } else if (currentSize == this.BOX_SIZE_MEDIUM) {
      this.boxWidth = '500px';
      this.boxHeight = '400px';
    } else if (currentSize == this.BOX_SIZE_LARGE) {
      this.boxWidth = '650px';
      this.boxHeight = '500px';
    } else if (currentSize == this.BOX_SIZE_X_LARGE) {
      this.boxWidth = '750px';
      this.boxHeight = '600px';
    } else if (currentSize == this.BOX_SIZE_FULL_WIDTH) {
      this.boxWidth = 'calc(95vw - 220px)';
      this.boxHeight = '600px';
    }
  }

  /* Drag & drop functions */

  boxWidth = '500px';
  boxHeight = '400px';

  ngAfterViewInit() {
    const placeholderElement = this.placeholder.element.nativeElement;

    placeholderElement.style.display = 'none';
    placeholderElement.parentNode!.removeChild(placeholderElement);
  }

  onDropListDropped() {
    if (!this.target) {
      return;
    }

    const placeholderElement: HTMLElement =
      this.placeholder.element.nativeElement;
    const placeholderParentElement: HTMLElement =
      placeholderElement.parentElement!;

    placeholderElement.style.display = 'none';

    placeholderParentElement.removeChild(placeholderElement);
    placeholderParentElement.appendChild(placeholderElement);
    placeholderParentElement.insertBefore(
      this.source!.element.nativeElement,
      placeholderParentElement.children[this.sourceIndex]
    );

    if (this.placeholder._dropListRef.isDragging()) {
      this.placeholder._dropListRef.exit(this.dragRef!);
    }

    this.target = null;
    this.source = null;
    this.dragRef = null;

    if (this.sourceIndex !== this.targetIndex) {
      moveItemInArray(this.items, this.sourceIndex, this.targetIndex);
    }
  }

  onDropListEntered({ item, container }: CdkDragEnter) {
    if (container == this.placeholder) {
      return;
    }

    const placeholderElement: HTMLElement =
      this.placeholder.element.nativeElement;
    const sourceElement: HTMLElement = item.dropContainer.element.nativeElement;
    const dropElement: HTMLElement = container.element.nativeElement;
    const dragIndex: number = Array.prototype.indexOf.call(
      dropElement.parentElement!.children,
      this.source ? placeholderElement : sourceElement
    );
    const dropIndex: number = Array.prototype.indexOf.call(
      dropElement.parentElement!.children,
      dropElement
    );

    if (!this.source) {
      this.sourceIndex = dragIndex;
      this.source = item.dropContainer;

      placeholderElement.style.width = this.boxWidth + 'px';
      placeholderElement.style.height = this.boxHeight + 40 + 'px';

      sourceElement.parentElement!.removeChild(sourceElement);
    }

    this.targetIndex = dropIndex;
    this.target = container;
    this.dragRef = item._dragRef;

    placeholderElement.style.display = '';

    dropElement.parentElement!.insertBefore(
      placeholderElement,
      dropIndex > dragIndex ? dropElement.nextSibling : dropElement
    );

    this.placeholder._dropListRef.enter(
      item._dragRef,
      item.element.nativeElement.offsetLeft,
      item.element.nativeElement.offsetTop
    );
  }

  //Tabs management
  index: number = 0;
  onTabChange(event: MatTabChangeEvent) {
    this.userPreferenceService.setUserTabsSelectionIndex('dashboard', event.index);
  }

  restoreTab() {
    this.index = this.userPreferenceService.getUserTabsSelectionIndex('dashboard');
  }

}
