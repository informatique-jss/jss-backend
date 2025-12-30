import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Router } from '@angular/router';
import { NgbOffcanvas } from '@ng-bootstrap/ng-bootstrap';
import { Row } from '@tanstack/angular-table';
import { Observable, Subject } from 'rxjs';
import { formatDateFrance } from '../../../../libs/FormatHelper';
import { GenericListComponent } from '../../../../libs/generic-list/generic-list.component';
import { GenericForm } from '../../../../libs/generic-list/GenericForm';
import { GenericSearchForm } from '../../../../libs/generic-list/GenericSearchForm';
import { GenericSearchTab } from '../../../../libs/generic-list/GenericSearchTab';
import { GenericTableAction } from '../../../../libs/generic-list/GenericTableAction';
import { GenericTableColumn } from '../../../../libs/generic-list/GenericTableColumn';
import { KpiCrmService } from '../../../crm/services/kpi.crm.service';
import { AppService } from '../../../main/services/app.service';
import { RestUserPreferenceService } from '../../../main/services/rest.user.preference.service';
import { PaymentDto } from '../../model/PaymentDto';
import { PaymentSearch } from '../../model/PaymentSearch';
import { PaymentService } from '../../services/payment.service';

@Component({
  selector: 'payment-list',
  templateUrl: './../../../../libs/generic-list/generic-list.component.html',
  styleUrls: ['./../../../../libs/generic-list/generic-list.component.css'],
})
export class PaymentListComponent extends GenericListComponent<PaymentDto, PaymentSearch> implements OnInit {
  eventOnClickOpenAction = new Subject<Row<PaymentDto>[]>();
  eventOnClickOpenQuotation = new Subject<Row<PaymentDto>>();

  override pageTitle = "Liste des paiements";

  constructor(private offcanvasService2: NgbOffcanvas,
    private formBuilder2: FormBuilder,
    private appService2: AppService,
    private restUserPreferenceService2: RestUserPreferenceService,
    private router: Router,
    private kpiCrmService: KpiCrmService,
    private paymentService: PaymentService,
  ) {
    super(offcanvasService2, formBuilder2, appService2, restUserPreferenceService2);

  }

  override ngOnInit(): void {
    super.ngOnInit();
    this.breadcrumbPaths = [
      { label: this.pageTitle, route: "/invoicing/payments" },
    ]
  }

  override generateActions(): GenericTableAction<PaymentDto>[] {
    let actions = [] as GenericTableAction<PaymentDto>[];
    actions.push({
      label: 'Voir le paiement',
      eventOnClick: this.eventOnClickOpenAction,
      maxNumberOfElementsRequiredToDisplay: 1,
      minNumberOfElementsRequiredToDisplay: 1
    })

    this.eventOnClickOpenAction.subscribe((row: Row<PaymentDto>[]) => {
      this.paymentService.setSelectedPaymentUnique(row[0].original);
      this.router.navigate(['payment/view/' + row[0].original.id]);
    });

    this.eventOnClickOpenQuotation.subscribe((row: Row<PaymentDto>) => {
      this.paymentService.setSelectedPaymentUnique(row.original);
      this.router.navigate(['payment/view/' + row.original.id]);
    });

    return actions;
  }

  override getListCode(): string {
    return 'PAYMENT_LIST';
  }

  override generateSearchTabs(): GenericSearchTab<PaymentSearch>[] {
    let searchTabs = [] as GenericSearchTab<PaymentSearch>[];
    searchTabs.push({
      label: "Généraux",
      icon: 'tablerFilter',
      forms: [
        {
          accessorKey: "startDate",
          form: {
            label: 'Date de début',
            type: 'input',
            inputType: 'date',
            validators: [],
          } as GenericForm<PaymentSearch>
        } as GenericSearchForm<PaymentSearch>,
        {
          accessorKey: "endDate",
          form: {
            label: 'Date de fin',
            type: 'input',
            inputType: 'date',
            validators: [],
          } as GenericForm<PaymentSearch>
        } as GenericSearchForm<PaymentSearch>,
        {
          accessorKey: "minAmount",
          form: {
            label: 'Montant minimum',
            type: 'input',
            inputType: 'number',
            validators: [],
          } as GenericForm<PaymentSearch>
        } as GenericSearchForm<PaymentSearch>,
        {
          accessorKey: "maxAmount",
          form: {
            label: 'Montant maximum',
            type: 'input',
            inputType: 'number',
            validators: [],
          } as GenericForm<PaymentSearch>
        } as GenericSearchForm<PaymentSearch>,
        {
          accessorKey: "labelContent",
          form: {
            label: 'Contenu du libellé',
            type: 'input',
            inputType: 'text',
            validators: [],
          } as GenericForm<PaymentSearch>
        } as GenericSearchForm<PaymentSearch>,
        // TODO : add autocomplete 
        // {
        //   accessorKey: "tiersId",
        //   form: {
        //     label: 'Tiers',
        //     type: 'input',
        //     autocompleteType: 'text',
        //     validators: [],
        //   } as GenericForm<PaymentSearch>
        // } as GenericSearchForm<PaymentSearch>,
      ]
    });

    return searchTabs;
  }

  override generateColumns() {
    let columns = [] as GenericTableColumn<PaymentDto>[];
    columns.push({
      accessorKey: 'id', header: 'N°', enableSorting: true, cell: info => info.getValue(), meta: {
      }
    })
    columns.push({
      accessorFn: (originalRow: PaymentDto, index: number) => { return formatDateFrance(originalRow.paymentDate) }, header: 'Date de paiment', enableSorting: true, cell: info => info.getValue(), meta: {
        eventOnDoubleClick: this.eventOnClickOpenQuotation
      }
    })
    // columns.push({
    //   accessorKey: 'origin', header: 'Origine', enableSorting: true, cell: info => info.getValue(), meta: {
    //     eventOnDoubleClick: this.eventOnClickOpenQuotation
    //   }
    // })
    // columns.push({
    //   accessorKey: 'affaires', header: 'Affaire(s)', enableSorting: true, cell: info => info.getValue(), meta: {
    //     eventOnDoubleClick: this.eventOnClickOpenQuotation
    //   }
    // })
    // columns.push({
    //   accessorKey: 'services', header: 'Service(s)', enableSorting: true, cell: info => info.getValue(), meta: {
    //     eventOnDoubleClick: this.eventOnClickOpenQuotation
    //   }
    // })
    // columns.push({
    //   accessorKey: 'status', header: 'Status', enableSorting: true, cell: info => info.getValue(), meta: {
    //     eventOnDoubleClick: this.eventOnClickOpenQuotation
    //   }
    // })
    // columns.push({
    //   accessorKey: 'description', header: 'Description', enableSorting: true, cell: info => info.getValue(), meta: {
    //     eventOnDoubleClick: this.eventOnClickOpenQuotation
    //   }
    // })
    // columns.push({
    //   accessorKey: 'tiers', header: 'Tiers', enableSorting: true, cell: info => info.getValue(), meta: {
    //     eventOnDoubleClick: this.eventOnClickOpenQuotation
    //   }
    // })
    // columns.push({
    //   accessorKey: 'responsablesIds', header: 'Donneur d\'ordre', enableSorting: true, cell: info => info.getValue(), meta: {
    //     eventOnDoubleClick: this.eventOnClickOpenQuotation
    //   }
    // })
    // columns.push({
    //   accessorKey: 'totalPrice', header: 'Prix', enableSorting: true, cell: info => info.getValue(), meta: {
    //     eventOnDoubleClick: this.eventOnClickOpenQuotation
    //   }
    // })
    // columns.push({
    //   accessorKey: 'salesEmployee', header: 'Commercial', enableSorting: true, cell: info => info.getValue(), meta: {
    //     eventOnDoubleClick: this.eventOnClickOpenQuotation
    //   }
    // })
    return columns;
  }

  override parseBookmarkModel(model: PaymentSearch) {
    return model;
  }

  getSearchFunction(searchModel: PaymentSearch): Observable<PaymentDto[]> {
    return this.paymentService.searchPayment(searchModel);
  }

  override initSearchModel() {
    let model = {} as PaymentSearch;
    return model;
  }


}
