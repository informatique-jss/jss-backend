import { ChangeDetectorRef, Component, EventEmitter, Input, OnInit, Output, SimpleChanges } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { QUOTATION_STATUS_SENT_TO_CUSTOMER } from 'src/app/libs/Constants';
import { formatDateTimeForSortTable } from 'src/app/libs/FormatHelper';
import { instanceOfCustomerOrder, instanceOfQuotation } from 'src/app/libs/TypeHelper';
import { getCustomerOrderForIQuotation } from 'src/app/modules/invoicing/components/invoice-tools';
import { SortTableColumn } from 'src/app/modules/miscellaneous/model/SortTableColumn';
import { DocumentTypeService } from 'src/app/modules/miscellaneous/services/document.type.service';
import { Employee } from 'src/app/modules/profile/model/Employee';
import { TiersService } from 'src/app/modules/tiers/services/tiers.service';
import { IndexEntityService } from 'src/app/routing/search/index.entity.service';
import { formatEurosForSortTable } from '../../../../libs/FormatHelper';
import { IndexEntity } from '../../../../routing/search/IndexEntity';
import { AppService } from '../../../../services/app.service';
import { Document } from '../../../miscellaneous/model/Document';
import { DocumentType } from '../../../miscellaneous/model/DocumentType';
import { SortTableAction } from '../../../miscellaneous/model/SortTableAction';
import { ResponsableService } from '../../../tiers/services/responsable.service';
import { Confrere } from '../../model/Confrere';
import { IQuotation } from '../../model/IQuotation';
import { OrderingSearchResult } from '../../model/OrderingSearchResult';
import { QuotationSearchResult } from '../../model/QuotationSearchResult';
import { CustomerOrderService } from '../../services/customer.order.service';
import { OrderingSearchResultService } from '../../services/ordering.search.result.service';
import { QuotationSearchResultService } from '../../services/quotation.search.result.service';
import { QuotationService } from '../../services/quotation.service';

@Component({
  selector: 'ordering-customer',
  templateUrl: './ordering-customer.component.html',
  styleUrls: ['./ordering-customer.component.css']
})
export class OrderingCustomerComponent implements OnInit {


  @Input() quotation: IQuotation = {} as IQuotation;
  @Input() editMode: boolean = false;
  @Output() updateDocuments: EventEmitter<void> = new EventEmitter<void>();

  documentTypes: DocumentType[] = [] as Array<DocumentType>;
  billingDocument: Document = {} as Document;
  searchedTiers: IndexEntity | undefined;
  searchedResponsable: IndexEntity | undefined;

  customerOrderTableActions: SortTableAction<OrderingSearchResult>[] = [] as Array<SortTableAction<OrderingSearchResult>>;
  customerOrderDisplayedColumns: SortTableColumn<OrderingSearchResult>[] = [] as Array<SortTableColumn<OrderingSearchResult>>;

  customerOrderRecurringDisplayedColumns: SortTableColumn<OrderingSearchResult>[] = [] as Array<SortTableColumn<OrderingSearchResult>>;
  customerOrderRecurringTableActions: SortTableAction<OrderingSearchResult>[] = [] as Array<SortTableAction<OrderingSearchResult>>;

  quotationTableActions: SortTableAction<QuotationSearchResult>[] = [] as Array<SortTableAction<QuotationSearchResult>>;
  quotationDisplayedColumns: SortTableColumn<QuotationSearchResult>[] = [] as Array<SortTableColumn<QuotationSearchResult>>;

  customerOrderQuotations: QuotationSearchResult[] | undefined;
  quotationCustomerOrders: OrderingSearchResult[] | undefined;
  customerOrderRecurring: OrderingSearchResult[] | undefined;

  selectedCustomerOrder: IndexEntity | undefined;

  QUOTATION_STATUS_SENT_TO_CUSTOMER = QUOTATION_STATUS_SENT_TO_CUSTOMER;

  constructor(private formBuilder: UntypedFormBuilder,
    private tiersService: TiersService,
    private appService: AppService,
    protected cd: ChangeDetectorRef,
    private responsableService: ResponsableService,
    private indexEntityService: IndexEntityService,
    protected documentTypeService: DocumentTypeService,
    private customerOrderService: CustomerOrderService,
    private orderingSearchResultService: OrderingSearchResultService,
    private quotationSearchResultService: QuotationSearchResultService,
    private quotationService: QuotationService,
    public specialOfferDialog: MatDialog) { }

  ngOnChanges(changes: SimpleChanges) {
    if (changes.quotation) {
      if (this.quotation.responsable && this.quotation.responsable.id && !this.searchedResponsable) {
        this.indexEntityService.getResponsableByKeyword(this.quotation.responsable.id + "", false).subscribe(response => this.searchedResponsable = response[0]);
      }
      if (this.quotation.tiers && this.quotation.tiers.id && !this.searchedTiers) {
        this.indexEntityService.getIndividualTiersByKeyword(this.quotation.tiers.id + "").subscribe(response => this.searchedTiers = response[0]);
      }
      this.orderingCustomerForm.markAllAsTouched();

      if (changes.quotation && this.quotation.id && instanceOfCustomerOrder(this.quotation))
        this.quotationSearchResultService.getQuotationsForCustomerOrder(this.quotation).subscribe(quotations => this.customerOrderQuotations = quotations);

      if (changes.quotation && this.quotation.id && instanceOfQuotation(this.quotation))
        this.orderingSearchResultService.getCustomerOrderForQuotation(this.quotation).subscribe(quotations => this.quotationCustomerOrders = quotations);

      if (changes.quotation && this.quotation.id && instanceOfCustomerOrder(this.quotation) && this.quotation.isRecurring)
        this.orderingSearchResultService.getCustomerOrderByCustomerOrderParentRecurringId(this.quotation).subscribe(customerOrders => this.customerOrderRecurring = customerOrders);

      if (changes.quotation && this.quotation.id && instanceOfCustomerOrder(this.quotation) && this.quotation.hasCustomerOrderParentRecurring)
        this.orderingSearchResultService.getCustomerOrderParentRecurringByCustomerOrderId(this.quotation).subscribe(customerOrders => this.customerOrderRecurring = customerOrders);

      if (changes.quotation && this.quotation.id && instanceOfCustomerOrder(this.quotation))
        if (this.quotation.isRecurring) {
          this.customerOrderRecurringDisplayedColumns.push({ id: "recurringStartDate", fieldName: "recurringStartDate", label: "Début", valueFonction: formatDateTimeForSortTable } as SortTableColumn<OrderingSearchResult>);
          this.customerOrderRecurringDisplayedColumns.push({ id: "recurringEndDate", fieldName: "recurringEndDate", label: "Fin", valueFonction: formatDateTimeForSortTable } as SortTableColumn<OrderingSearchResult>);
        } else {
          this.customerOrderRecurringDisplayedColumns.push({ id: "recurringPeriodStartDate", fieldName: "recurringPeriodStartDate", label: "Début période", valueFonction: formatDateTimeForSortTable } as SortTableColumn<OrderingSearchResult>);
          this.customerOrderRecurringDisplayedColumns.push({ id: "recurringPeriodEndDate", fieldName: "recurringPeriodEndDate", label: "Fin période", valueFonction: formatDateTimeForSortTable } as SortTableColumn<OrderingSearchResult>);
        }
    }
  }

  ngOnInit() {
    this.orderingCustomerForm.markAllAsTouched();

    this.customerOrderDisplayedColumns = [];
    this.customerOrderDisplayedColumns.push({ id: "customerOrderId", fieldName: "customerOrderId", label: "N° de la commande" } as SortTableColumn<OrderingSearchResult>);
    this.customerOrderDisplayedColumns.push({ id: "customerOrderStatus", fieldName: "customerOrderStatus", label: "Statut" } as SortTableColumn<OrderingSearchResult>);
    this.customerOrderDisplayedColumns.push({ id: "createdDate", fieldName: "createdDate", label: "Date de création", valueFonction: formatDateTimeForSortTable } as SortTableColumn<OrderingSearchResult>);
    this.customerOrderDisplayedColumns.push({ id: "totalPrice", fieldName: "totalPrice", label: "Prix total", valueFonction: formatEurosForSortTable } as SortTableColumn<OrderingSearchResult>);

    this.customerOrderTableActions.push({
      actionIcon: "visibility", actionName: "Voir la commande", actionLinkFunction: (action: SortTableAction<OrderingSearchResult>, element: OrderingSearchResult) => {
        if (element)
          return ['/order', element.customerOrderId];
        return undefined;
      }, display: true,
    } as SortTableAction<OrderingSearchResult>);

    this.quotationDisplayedColumns = [];
    this.quotationDisplayedColumns.push({ id: "quotationId", fieldName: "quotationId", label: "N° du devis" } as SortTableColumn<QuotationSearchResult>);
    this.quotationDisplayedColumns.push({ id: "quotationStatus", fieldName: "quotationStatus", label: "Statut" } as SortTableColumn<QuotationSearchResult>);
    this.quotationDisplayedColumns.push({ id: "createdDate", fieldName: "createdDate", label: "Date de création", valueFonction: formatDateTimeForSortTable } as SortTableColumn<QuotationSearchResult>);
    this.quotationDisplayedColumns.push({ id: "totalPrice", fieldName: "totalPrice", label: "Prix total", valueFonction: formatEurosForSortTable } as SortTableColumn<QuotationSearchResult>);

    this.quotationTableActions.push({
      actionIcon: "visibility", actionName: "Voir le devis", actionLinkFunction: (action: SortTableAction<QuotationSearchResult>, element: QuotationSearchResult) => {
        if (element)
          return ['/quotation', element.quotationId];
        return undefined;
      }, display: true,
    } as SortTableAction<QuotationSearchResult>);

    this.customerOrderRecurringDisplayedColumns = [];
    this.customerOrderRecurringDisplayedColumns.push({ id: "customerOrderId", fieldName: "customerOrderId", label: "N° de la commande" } as SortTableColumn<OrderingSearchResult>);
    this.customerOrderRecurringDisplayedColumns.push({ id: "customerOrderStatus", fieldName: "customerOrderStatus", label: "Statut" } as SortTableColumn<OrderingSearchResult>);
    this.customerOrderRecurringDisplayedColumns.push({ id: "createdDate", fieldName: "createdDate", label: "Date de création", valueFonction: formatDateTimeForSortTable } as SortTableColumn<OrderingSearchResult>);
    this.customerOrderRecurringDisplayedColumns.push({ id: "totalPrice", fieldName: "totalPrice", label: "Prix total", valueFonction: formatEurosForSortTable } as SortTableColumn<OrderingSearchResult>);


    this.customerOrderRecurringTableActions.push({
      actionIcon: "visibility", actionName: "Voir la commande", actionLinkFunction: (action: SortTableAction<OrderingSearchResult>, element: OrderingSearchResult) => {
        if (element)
          return ['/order', element.customerOrderId];
        return undefined;
      }, display: true,
    } as SortTableAction<OrderingSearchResult>);
  }

  orderingCustomerForm = this.formBuilder.group({
  });

  getCustomerOrderForIQuotation = getCustomerOrderForIQuotation;

  fillTiers(tiers: IndexEntity) {
    this.tiersService.getTiers(tiers.entityId).subscribe(response => {
      this.quotation.tiers = response;
      this.quotation.responsable = undefined;
      this.quotation.responsable = undefined;
      if (this.quotation.tiers) {
        this.quotation.observations = this.quotation.tiers.observations;
        this.quotation.instructions = this.quotation.tiers.instructions;
      }
      this.setDocument();
    })
  }

  fillConfrere(confrere: Confrere) {
    this.quotation.confrere = confrere;
    this.quotation.tiers = undefined;
    this.quotation.responsable = undefined;
    if (this.quotation.confrere) {
      this.quotation.observations = this.quotation.confrere.observations;
    }
    this.setDocument();
  }

  fillResponsable(responsable: IndexEntity) {
    this.responsableService.getResponsable(responsable.entityId).subscribe(response => {
      this.quotation.responsable = response;
      this.setDocument();
    });
    this.tiersService.getTiersByResponsable(responsable.entityId).subscribe(response => {
      if (this.quotation.responsable != null) {
        this.quotation.responsable.tiers = response;
        this.quotation.observations = this.quotation.responsable.tiers.observations;
        this.quotation.instructions = this.quotation.responsable.tiers.instructions;
        this.setDocument();
      }
    })
    this.quotation.tiers = undefined;
  }

  getFormStatus(): boolean {
    this.orderingCustomerForm.markAllAsTouched();
    return this.orderingCustomerForm.valid && (this.quotation.responsable != undefined || this.quotation.tiers != undefined || this.quotation.confrere != undefined);
  }

  instanceOfQuotation = instanceOfQuotation;
  instanceOfCustomerOrder = instanceOfCustomerOrder;

  setDocument() {
    this.updateDocuments.emit();
  }

  openRoute(event: any, link: string) {
    this.appService.openRoute(event, link, null);
  }

  updateAssignedToForCustomerOrder(employee: Employee) {
    if (this.editMode)
      return;
    if (instanceOfCustomerOrder(this.quotation))
      this.customerOrderService.updateAssignedToForCustomerOrder(this.quotation, employee).subscribe(response => {
      });
    if (instanceOfQuotation(this.quotation))
      this.customerOrderService.updateAssignedToForQuotation(this.quotation, employee).subscribe(response => {
      });
  }

  selectCustomerOrderOnQuotation(customerOrder: IndexEntity) {
    this.quotationService.associateCustomerOrderToQuotation(customerOrder.entityId, this.quotation.id).subscribe(response => {
      this.appService.openRoute(null, '/quotation/' + this.quotation.id, null);
    })
  }
}
