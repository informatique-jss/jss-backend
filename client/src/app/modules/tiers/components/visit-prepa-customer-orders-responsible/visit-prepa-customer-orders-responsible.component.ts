import { AfterContentChecked, ChangeDetectorRef, Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import { Responsable } from '../../model/Responsable';
import { CustomerOrder } from '../../../quotation/model/CustomerOrder';
import { InvoiceSearch } from 'src/app/modules/invoicing/model/InvoiceSearch';
import { Tiers } from '../../model/Tiers';
import { SortTableColumn } from 'src/app/modules/miscellaneous/model/SortTableColumn';
import { ITiers } from '../../model/ITiers';
import { OrderingSearchResult } from 'src/app/modules/quotation/model/OrderingSearchResult';
import { OrderingSearchResultService } from 'src/app/modules/quotation/services/ordering.search.result.service';
import { OrderingSearch } from 'src/app/modules/quotation/model/OrderingSearch';
import { QuotationSearch } from 'src/app/modules/quotation/model/QuotationSearch';
import { AffaireSearch } from 'src/app/modules/quotation/model/AffaireSearch';
import { InvoiceSearchResultService } from 'src/app/modules/invoicing/services/invoice.search.result.service';
import { InvoiceSearchResult } from 'src/app/modules/invoicing/model/InvoiceSearchResult';
import { QuotationSearchResult } from 'src/app/modules/quotation/model/QuotationSearchResult';
import { CustomerOrderFiller } from '../../model/CustomerOrderFiller';
import { formatDateTimeForSortTable, formatEurosForSortTable } from 'src/app/libs/FormatHelper';
import { SortTableAction } from 'src/app/modules/miscellaneous/model/SortTableAction';
import { Employee } from 'src/app/modules/profile/model/Employee';
import { AssoAffaireOrderSearchResult } from 'src/app/modules/quotation/model/AssoAffaireOrderSearchResult';
import { Subscription } from 'rxjs';
import { ConstantService } from 'src/app/modules/miscellaneous/services/constant.service';

@Component({
  selector: 'visit-prepa-customer-orders-responsible',
  templateUrl: './visit-prepa-customer-orders-responsible.component.html',
  styleUrls: ['./visit-prepa-customer-orders-responsible.component.css']
})

export class VisitPrepaCustomerOrdersResponsibleComponent implements OnInit, AfterContentChecked {
  @Input() quotationSearch: QuotationSearch = {} as QuotationSearch;
  @Input() orderingSearch: OrderingSearch = {} as OrderingSearch;
  @Input() responsable: Responsable = {} as Responsable;
  @Input() tiers: Tiers = {} as Tiers;
  @Input() overrideIconAction: string = "";
  @Input() affaireSearch: AffaireSearch | undefined;
  @Input() isForDashboard: boolean = false;
  @Input() isForTiersIntegration: boolean = false;

  searchText: string | undefined;
  customerOrders: CustomerOrder[] | undefined;
  invoiceSearch: InvoiceSearch = {} as InvoiceSearch;
  orders: OrderingSearchResult[] | undefined;
  invoices: InvoiceSearchResult[] | undefined;
  quotations: QuotationSearchResult[] | undefined;
  customerOrderFiller: CustomerOrderFiller[] | undefined;
  customerOrder: CustomerOrder = {} as CustomerOrder;

  displayedColumnsSalesRecord: SortTableColumn<CustomerOrderFiller>[] = [];
  salesRecordTableActions: SortTableAction<CustomerOrderFiller>[] = [] as Array<SortTableAction<CustomerOrderFiller>>;
  tableAction: SortTableAction<CustomerOrderFiller>[] = [];

  constructor(private orderingSearchResultService: OrderingSearchResultService,
    private invoiceSearchResultService: InvoiceSearchResultService,
    private changeDetectorRef: ChangeDetectorRef,
    private constantService: ConstantService,
    ) {
    }

  ngAfterContentChecked(): void {
      this.changeDetectorRef.detectChanges();
  }

  ngOnInit() {
    this.displayedColumnsSalesRecord = [];
    this.quotations = [{} as QuotationSearchResult];
    this.invoices = [{} as InvoiceSearchResult];
    this.customerOrderFiller = [{} as CustomerOrderFiller];

    this.displayedColumnsSalesRecord.push({ id: "responsableLabel", fieldName: "responsableLabel", label: "Donneur d'ordre"  } as SortTableColumn<CustomerOrderFiller>);
    this.displayedColumnsSalesRecord.push({ id: "customerOrderId", fieldName: "customerOrderId", label: "Nº commande"  } as SortTableColumn<CustomerOrderFiller>);
    this.displayedColumnsSalesRecord.push({ id: "createdDateCO", fieldName: "createdDateCO", label: "Date de création", valueFonction: formatDateTimeForSortTable } as SortTableColumn<CustomerOrderFiller>);
    this.displayedColumnsSalesRecord.push({ id: "affaireLabel", fieldName: "affaireLabel", label: "Affaire"  } as SortTableColumn<CustomerOrderFiller>);
    this.displayedColumnsSalesRecord.push({ id: "customerOrderStatus", fieldName: "customerOrderStatus", label: "Statut de la commande"  } as SortTableColumn<CustomerOrderFiller>);
    this.displayedColumnsSalesRecord.push({ id: "invoiceId", fieldName: "invoiceId", label: "facture"  } as SortTableColumn<CustomerOrderFiller>);
    this.displayedColumnsSalesRecord.push({ id: "createdDateInvoice", fieldName: "createdDateInvoice", label: "Date de la Facture", valueFonction: formatDateTimeForSortTable  } as SortTableColumn<CustomerOrderFiller>);
    this.displayedColumnsSalesRecord.push({ id: "totalPriceInvoice", fieldName: "totalPriceInvoice", label: "Montant de la Facture", valueFonction: formatEurosForSortTable  } as SortTableColumn<CustomerOrderFiller>);
    this.displayedColumnsSalesRecord.push({ id: "remainingToPayInvoice", fieldName: "remainingToPayInvoice", label: "Reste à payer", valueFonction: formatEurosForSortTable  } as SortTableColumn<CustomerOrderFiller>);
    this.displayedColumnsSalesRecord.push({ id: "depositTotalAmount", fieldName: "depositTotalAmount", label: "Acompte versé", valueFonction: formatEurosForSortTable  } as SortTableColumn<CustomerOrderFiller>);
    this.displayedColumnsSalesRecord.push({ id: "nbrCommandes", fieldName: "nbrCommandes", label: "Nbr commandes"  } as SortTableColumn<CustomerOrderFiller>);

    if (this.overrideIconAction == "") {

    this.salesRecordTableActions.push({
      actionIcon: "visibility", actionName: "Voir la commande", actionLinkFunction: (action: SortTableAction<CustomerOrderFiller>, element: CustomerOrderFiller) => {
        if (element)
          return ['/order', element.customerOrderId];
        return undefined;
      }, display: true,
    } as SortTableAction<CustomerOrderFiller>);

      this.salesRecordTableActions.push({
        actionIcon: "point_of_sale", actionName: "Voir le détail de la facture / associer", actionLinkFunction: (action: SortTableAction<CustomerOrderFiller>, element: CustomerOrderFiller) => {
          if (element)
            return ['/invoicing/view', element.invoiceId];
          return undefined;
        }, display: true,
      } as SortTableAction<CustomerOrderFiller>);

    }
    this.loadCustomerOrderFilter();
    this.loadInvoiceFilter();
  }

  applyFilter(filterValue: any) {
    let filterValueCast = (filterValue as HTMLInputElement);
    filterValue = filterValueCast.value.trim();
    this.searchText = filterValue.toLowerCase();
  }

 loadCustomerOrderFilter() {
    this.orderingSearch.customerOrders = [this.tiers];
    this.orderingSearchResultService.getOrders(this.orderingSearch).subscribe(response => {
      this.orders = response;
      this.fillCustomerOrderFiller();
    })
  }

 loadInvoiceFilter(){
  this.invoiceSearch.customerOrders = [this.tiers];
  this.invoiceSearchResultService.getInvoicesList(this.invoiceSearch).subscribe(response => {
    this.invoices = response;
  })
  }

  fillCustomerOrderFiller() {
    this.customerOrderFiller = [];
    const fillerMap = new Map<number, CustomerOrderFiller>();
    const responsibleLabelCount = new Map<string, number>();

    if (this.orders !== undefined && this.orders.length > 0) {
      for (let i = 0; i < this.orders.length; i++) {
        if (this.orders[i].customerOrderId !== undefined) {
          const fillerItem: CustomerOrderFiller = {
            customerOrderId: this.orders[i].customerOrderId,
            customerOrderStatus: this.orders[i].customerOrderStatus || '',
            affaireLabel: this.orders[i].affaireLabel || '',
            depositTotalAmount: this.orders[i].depositTotalAmount || 0,
            createdDateCO: this.orders[i].createdDate || '',
            responsableLabel: this.orders[i].customerOrderLabel || '',
            invoiceId: 0,
            createdDateInvoice: null,
            totalPriceInvoice: 0,
            remainingToPayInvoice: 0,
            dateFacture: null,
            nbrCommandes: 0,
          };

          fillerMap.set(this.orders[i].customerOrderId, fillerItem);
        }
      }
    }

    if (this.invoices !== undefined && this.invoices.length > 0) {
      for (let i = 0; i < this.invoices.length; i++) {
        const customerOrderId = this.invoices[i].customerOrderId;

        if (fillerMap.has(customerOrderId)) {
          const fillerItem = fillerMap.get(customerOrderId)!;
          fillerItem.invoiceId = this.invoices[i].invoiceId || 0;
          fillerItem.createdDateInvoice = this.invoices[i].createdDate || null;
          fillerItem.totalPriceInvoice = this.invoices[i].totalPrice || 0;

          if (this.invoices[i].invoiceStatus !== this.constantService.getInvoiceStatusCancelled().label) {
            fillerItem.remainingToPayInvoice = this.invoices[i].remainingToPay || 0;
          }
        }
      }
    }
    for (const fillerItem of Array.from(fillerMap.values())) {
      const count = responsibleLabelCount.get(fillerItem.responsableLabel) || 0;
      responsibleLabelCount.set(fillerItem.responsableLabel, count + 1);
    }
    for (const [key, value] of responsibleLabelCount.entries()) {
      const correspondingOrder = Array.from(fillerMap.values()).find(item => item.responsableLabel === key);
      if (correspondingOrder) {
        correspondingOrder.nbrCommandes = value;
      }
    }
    this.customerOrderFiller = Array.from(fillerMap.values());
  }
}
