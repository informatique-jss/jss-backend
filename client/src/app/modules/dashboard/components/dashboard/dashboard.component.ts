import { CdkDragEnter, CdkDropList, DragRef, moveItemInArray } from '@angular/cdk/drag-drop';
import { Component, OnInit, ViewChild } from '@angular/core';
import { combineLatest, map } from 'rxjs';
import { CUSTOMER_ORDER_STATUS_BEING_PROCESSED, CUSTOMER_ORDER_STATUS_OPEN, CUSTOMER_ORDER_STATUS_TO_BILLED, QUOTATION_STATUS_OPEN, QUOTATION_STATUS_REFUSED_BY_CUSTOMER, QUOTATION_STATUS_TO_VERIFY, SIMPLE_PROVISION_STATUS_WAITING_DOCUMENT, SIMPLE_PROVISION_STATUS_WAITING_DOCUMENT_AUTHORITY } from 'src/app/libs/Constants';
import { InvoiceSearch } from 'src/app/modules/invoicing/model/InvoiceSearch';
import { PaymentSearch } from 'src/app/modules/invoicing/model/PaymentSearch';
import { RefundSearch } from 'src/app/modules/invoicing/model/RefundSearch';
import { IWorkflowElement } from 'src/app/modules/miscellaneous/model/IWorkflowElement';
import { ConstantService } from 'src/app/modules/miscellaneous/services/constant.service';
import { Employee } from 'src/app/modules/profile/model/Employee';
import { EmployeeService } from 'src/app/modules/profile/services/employee.service';
import { AffaireSearch } from 'src/app/modules/quotation/model/AffaireSearch';
import { AnnouncementStatus } from 'src/app/modules/quotation/model/AnnouncementStatus';
import { BodaccStatus } from 'src/app/modules/quotation/model/BodaccStatus';
import { CustomerOrderStatus } from 'src/app/modules/quotation/model/CustomerOrderStatus';
import { DomiciliationStatus } from 'src/app/modules/quotation/model/DomiciliationStatus';
import { FormaliteStatus } from 'src/app/modules/quotation/model/FormaliteStatus';
import { OrderingSearch } from 'src/app/modules/quotation/model/OrderingSearch';
import { QuotationSearch } from 'src/app/modules/quotation/model/QuotationSearch';
import { QuotationStatus } from 'src/app/modules/quotation/model/QuotationStatus';
import { SimpleProvisionStatus } from 'src/app/modules/quotation/model/SimpleProvisonStatus';
import { AnnouncementStatusService } from 'src/app/modules/quotation/services/announcement.status.service';
import { BodaccStatusService } from 'src/app/modules/quotation/services/bodacc.status.service';
import { CustomerOrderStatusService } from 'src/app/modules/quotation/services/customer.order.status.service';
import { DomiciliationStatusService } from 'src/app/modules/quotation/services/domiciliation-status.service';
import { FormaliteStatusService } from 'src/app/modules/quotation/services/formalite.status.service';
import { QuotationStatusService } from 'src/app/modules/quotation/services/quotation-status.service';
import { SimpleProvisionStatusService } from 'src/app/modules/quotation/services/simple.provision.status.service';
import { AppService } from 'src/app/services/app.service';
import { HabilitationsService } from 'src/app/services/habilitations.service';
import { UserPreferenceService } from 'src/app/services/user.preference.service';

@Component({
  selector: 'dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css'],
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
  bodaccStatus: BodaccStatus[] = [] as Array<BodaccStatus>;
  domiciliationStatus: DomiciliationStatus[] = [] as Array<DomiciliationStatus>;
  statusTypes: IWorkflowElement[] = [] as Array<IWorkflowElement>;

  customerOrderStatus: CustomerOrderStatus[] = [] as Array<CustomerOrderStatus>;
  quotationStatus: QuotationStatus[] = [] as Array<QuotationStatus>;



  currentEmployee: Employee | undefined;
  items: Array<string> = [];
  itemsSize: Array<string> = [];
  checkboxes: any = [];
  boxSizesSelected: any = [];

  AFFAIRE_IN_PROGRESS = "Mes prestations en cours";
  AFFAIRE_TO_DO = "Mes prestations à faire";
  AFFAIRE_RESPONSIBLE_TO_DO = "Mes prestations en responsabilité à faire";
  AFFAIRE_RESPONSIBLE_IN_PROGRESS = "Mes prestations en responsabilité en cours";
  AFFAIRE_SIMPLE_PROVISION_WAITING_AUTHORITY = "Mes formalités simples en attente de l'autorité compétente"
  AFFAIRE_SIMPLE_PROVISION_STATUS_WAITING_DOCUMENT = "Mes formalités simples en attente de documents"

  affaireSearchInProgress: AffaireSearch = {} as AffaireSearch;
  affaireSearchToDo: AffaireSearch = {} as AffaireSearch;
  affaireSearchResponsibleInProgress: AffaireSearch = {} as AffaireSearch;
  affaireSearchResponsibleToDo: AffaireSearch = {} as AffaireSearch;
  affaireSearchWaitingAuthority: AffaireSearch = {} as AffaireSearch;
  affaireSearchWaitingDocument: AffaireSearch = {} as AffaireSearch;

  ORDER_OPEN = "Mes commandes ouvertes";
  ORDER_BEING_PROCESSED = "Mes commandes en cours";
  ORDER_TO_BILLED = "Commandes en attente de facturation";

  orderingSearchOpen: OrderingSearch = {} as OrderingSearch;
  orderingSearchBeingProcessed: OrderingSearch = {} as OrderingSearch;
  orderingSearchToBilled: OrderingSearch = {} as OrderingSearch;

  QUOTATION_OPEN = "Mes devis ouverts";
  QUOTATION_TO_VERIFY = "Mes devis à vérifier";
  QUOTATION_REFUSED = "Mes devis refusés";
  quotationSearchOpen: QuotationSearch = {} as QuotationSearch;
  quotationSearchToVerify: QuotationSearch = {} as QuotationSearch;
  quotationSearchRefused: QuotationSearch = {} as QuotationSearch;

  INVOICE_TO_ASSOCIATE = "Factures à associer";
  invoiceSearchToAssociate: InvoiceSearch = {} as InvoiceSearch;

  PAYMENT_TO_ASSOCIATE = "Paiements entrants à associer";
  paymentSearchToAssociate: PaymentSearch = {} as PaymentSearch;

  REFUND_TO_EMIT = "Remboursements à émettre";
  refundSearchToEmit: RefundSearch = {} as RefundSearch;

  LOG_TO_REVIEW = "Logs à revoir";

  PROVISION_BOARD = "Suivi d'équipe";

  allItems: Array<string> = [this.QUOTATION_REFUSED, this.PAYMENT_TO_ASSOCIATE, this.INVOICE_TO_ASSOCIATE, this.QUOTATION_TO_VERIFY,
  this.QUOTATION_OPEN, this.ORDER_TO_BILLED, this.ORDER_BEING_PROCESSED, this.ORDER_OPEN,
  this.AFFAIRE_RESPONSIBLE_IN_PROGRESS, this.AFFAIRE_RESPONSIBLE_TO_DO, this.AFFAIRE_SIMPLE_PROVISION_WAITING_AUTHORITY,
  this.AFFAIRE_SIMPLE_PROVISION_STATUS_WAITING_DOCUMENT, this.AFFAIRE_IN_PROGRESS, this.AFFAIRE_TO_DO,
  this.PROVISION_BOARD].sort((a, b) => a.localeCompare(b));

  BOX_SIZE_X_SMALL = "Zoom très petit";
  BOX_SIZE_SMALL = "Zoom petit";
  BOX_SIZE_MEDIUM = "Zoom moyen";
  BOX_SIZE_LARGE = "Zoom grand";
  BOX_SIZE_X_LARGE = "Zoom très grand";

  allBoxSizes: Array<string> = [this.BOX_SIZE_X_SMALL, this.BOX_SIZE_SMALL, this.BOX_SIZE_MEDIUM, this.BOX_SIZE_LARGE, this.BOX_SIZE_X_LARGE];

  constructor(private appService: AppService,
    private employeeService: EmployeeService,
    private formaliteStatusService: FormaliteStatusService,
    private bodaccStatusService: BodaccStatusService,
    private domiciliationStatusService: DomiciliationStatusService,
    private announcementStatusService: AnnouncementStatusService,
    private simpleProvisionStatusService: SimpleProvisionStatusService,
    private userPreferenceService: UserPreferenceService,
    private customerOrderStatusService: CustomerOrderStatusService,
    private quotationStatusService: QuotationStatusService,
    private constantService: ConstantService,
    private habilitationsService: HabilitationsService,
  ) { }

  ngOnInit() {
    this.appService.changeHeaderTitle("Tableau de bord");
    this.employeeService.getCurrentEmployee().subscribe(response => {
      this.currentEmployee = response;

      combineLatest([
        this.bodaccStatusService.getBodaccStatus(),
        this.domiciliationStatusService.getDomiciliationStatus(),
        this.announcementStatusService.getAnnouncementStatus(),
        this.formaliteStatusService.getFormaliteStatus(),
        this.simpleProvisionStatusService.getSimpleProvisionStatus(),
        this.customerOrderStatusService.getCustomerOrderStatus(),
        this.quotationStatusService.getQuotationStatus()
      ]).pipe(
        map(([bodaccStatus, domiciliationStatus, announcementStatus, formaliteStatus, simpleProvisionStatus, customerOrderStatus, quotationStatus]) => ({ bodaccStatus, domiciliationStatus, announcementStatus, formaliteStatus, simpleProvisionStatus, customerOrderStatus, quotationStatus })),
      ).subscribe(response => {
        this.bodaccStatus = response.bodaccStatus;
        this.statusTypes.push(...response.bodaccStatus);
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

        this.affaireSearchResponsibleInProgress.responsible = this.currentEmployee;
        this.affaireSearchResponsibleInProgress.status = this.statusTypes.filter(stauts => stauts.isOpenState);

        this.affaireSearchResponsibleToDo.responsible = this.currentEmployee;
        this.affaireSearchResponsibleToDo.status = this.statusTypes.filter(stauts => stauts.isOpenState);

        this.affaireSearchWaitingDocument.responsible = this.currentEmployee;
        this.affaireSearchWaitingDocument.status = this.simpleProvisionStatus.filter(stauts => stauts.code == SIMPLE_PROVISION_STATUS_WAITING_DOCUMENT);

        this.affaireSearchWaitingAuthority.responsible = this.currentEmployee;
        this.affaireSearchWaitingAuthority.status = this.simpleProvisionStatus.filter(stauts => stauts.code == SIMPLE_PROVISION_STATUS_WAITING_DOCUMENT_AUTHORITY);

        this.orderingSearchOpen.salesEmployee = this.currentEmployee!;
        this.orderingSearchOpen.customerOrderStatus = [this.customerOrderStatusService.getCustomerStatusByCode(this.customerOrderStatus, CUSTOMER_ORDER_STATUS_OPEN)!];

        this.orderingSearchBeingProcessed.salesEmployee = this.currentEmployee!;
        this.orderingSearchBeingProcessed.customerOrderStatus = [this.customerOrderStatusService.getCustomerStatusByCode(this.customerOrderStatus, CUSTOMER_ORDER_STATUS_BEING_PROCESSED)!];

        this.orderingSearchToBilled.customerOrderStatus = [this.customerOrderStatusService.getCustomerStatusByCode(this.customerOrderStatus, CUSTOMER_ORDER_STATUS_TO_BILLED)!];

        this.quotationSearchOpen.salesEmployee = this.currentEmployee!;
        this.quotationSearchOpen.quotationStatus = [this.quotationStatusService.getQuotationStatusByCode(this.quotationStatus, QUOTATION_STATUS_OPEN)!];

        this.quotationSearchToVerify.salesEmployee = this.currentEmployee!;
        this.quotationSearchToVerify.quotationStatus = [this.quotationStatusService.getQuotationStatusByCode(this.quotationStatus, QUOTATION_STATUS_TO_VERIFY)!];

        this.quotationSearchRefused.salesEmployee = this.currentEmployee!;
        this.quotationSearchRefused.quotationStatus = [this.quotationStatusService.getQuotationStatusByCode(this.quotationStatus, QUOTATION_STATUS_REFUSED_BY_CUSTOMER)!];

        this.invoiceSearchToAssociate.invoiceStatus = [this.constantService.getInvoiceStatusSend()];

        this.paymentSearchToAssociate.isHideAssociatedPayments = true;
        this.paymentSearchToAssociate.paymentWays = [this.constantService.getPaymentWayInbound()];

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
      this.boxWidth = '700px';
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
}
