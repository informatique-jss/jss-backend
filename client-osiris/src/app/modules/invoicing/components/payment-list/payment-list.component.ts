import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Router } from '@angular/router';
import { NgbNavModule, NgbOffcanvas } from '@ng-bootstrap/ng-bootstrap';
import { NgIcon } from '@ng-icons/core';
import { Row } from '@tanstack/angular-table';
import { Observable, Subject } from 'rxjs';
import { SimplebarAngularModule } from 'simplebar-angular';
import { sortableDateFormat } from '../../../../libs/DateHelper';
import { formatBoolean, formatCurrency } from '../../../../libs/FormatHelper';
import { GenericListComponent } from '../../../../libs/generic-list/generic-list.component';
import { GenericForm } from '../../../../libs/generic-list/GenericForm';
import { GenericSearchForm } from '../../../../libs/generic-list/GenericSearchForm';
import { GenericSearchTab } from '../../../../libs/generic-list/GenericSearchTab';
import { GenericTableAction } from '../../../../libs/generic-list/GenericTableAction';
import { GenericTableColumn } from '../../../../libs/generic-list/GenericTableColumn';
import { SHARED_IMPORTS } from '../../../../libs/SharedImports';
import { TanstackTableComponent } from '../../../../libs/tanstack-table/tanstack-table.component';
import { PageTitleComponent } from '../../../main/components/page-title/page-title.component';
import { AppService } from '../../../main/services/app.service';
import { RestUserPreferenceService } from '../../../main/services/rest.user.preference.service';
import { AutocompleteComponent } from '../../../miscellaneous/forms/components/autocomplete/autocomplete.component';
import { GenericFormComponent } from '../../../miscellaneous/forms/components/generic-form/generic-form.component';
import { PaymentDto } from '../../model/PaymentDto';
import { PaymentSearch } from '../../model/PaymentSearch';
import { PaymentService } from '../../services/payment.service';

@Component({
  selector: 'payment-list',
  standalone: true,
  templateUrl: './../../../../libs/generic-list/generic-list.component.html',
  styleUrls: ['./../../../../libs/generic-list/generic-list.component.css'],
  imports: [...SHARED_IMPORTS,
    TanstackTableComponent,
    PageTitleComponent,
    NgIcon,
    SimplebarAngularModule,
    NgbNavModule,
    GenericFormComponent,
    AutocompleteComponent]
})
export class PaymentListComponent extends GenericListComponent<PaymentDto, PaymentSearch> implements OnInit {

  eventOnClickOpenAction = new Subject<Row<PaymentDto>[]>();
  eventOnClickOpenQuotation = new Subject<Row<PaymentDto>>();

  override pageTitle = "Liste des paiements";
  override pageRoute = "/invoicing/payments";

  constructor(private offcanvasService2: NgbOffcanvas,
    private formBuilder2: FormBuilder,
    private appService2: AppService,
    private restUserPreferenceService2: RestUserPreferenceService,
    private router: Router,
    private paymentService: PaymentService,
  ) {
    super(offcanvasService2, formBuilder2, appService2, restUserPreferenceService2);

  }

  override ngOnInit(): void {
    super.ngOnInit();
  }

  override generateActions(): GenericTableAction<PaymentDto>[] {
    let actions = [] as GenericTableAction<PaymentDto>[];
    actions.push({
      label: 'Voir le paiement',
      eventOnClick: this.eventOnClickOpenAction,
      maxNumberOfElementsRequiredToDisplay: 1,
      minNumberOfElementsRequiredToDisplay: 1
    }, {
      label: 'Voir la facture',
      eventOnClick: this.eventOnClickOpenAction,
      maxNumberOfElementsRequiredToDisplay: 1,
      minNumberOfElementsRequiredToDisplay: 1
    }, {
      label: 'Voir la commande',
      eventOnClick: this.eventOnClickOpenAction,
      maxNumberOfElementsRequiredToDisplay: 1,
      minNumberOfElementsRequiredToDisplay: 1
    })

    this.eventOnClickOpenAction.subscribe((row: Row<PaymentDto>[]) => {
      this.router.navigate(['payment/view/' + row[0].original.id]);
    });

    this.eventOnClickOpenQuotation.subscribe((row: Row<PaymentDto>) => {
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
          accessorKey: "label",
          form: {
            label: 'Contenu du libellé',
            type: 'input',
            inputType: 'text',
            validators: [],
          } as GenericForm<PaymentSearch>
        } as GenericSearchForm<PaymentSearch>,
        {
          accessorKey: "tiers",
          form: {
            label: 'Tiers',
            type: 'autocomplete',
            autocompleteType: 'tiers',
            validators: [],
          } as GenericForm<PaymentSearch>
        } as GenericSearchForm<PaymentSearch>,
        {
          accessorKey: "responsable",
          form: {
            label: 'Responsable',
            type: 'autocomplete',
            autocompleteType: 'responsables',
            validators: [],
          } as GenericForm<PaymentSearch>
        } as GenericSearchForm<PaymentSearch>,
        {
          accessorKey: "isAssociated",
          form: {
            label: 'Est associé ?',
            type: 'switch',
            validators: [],
          } as GenericForm<PaymentSearch>
        } as GenericSearchForm<PaymentSearch>,
        {
          accessorKey: "isCancelled",
          form: {
            label: 'Est annulé ?',
            type: 'switch',
            validators: [],
          } as GenericForm<PaymentSearch>
        } as GenericSearchForm<PaymentSearch>,
        {
          accessorKey: "isAppoint",
          form: {
            label: 'Est un appoint ?',
            type: 'switch',
            validators: [],
          } as GenericForm<PaymentSearch>
        } as GenericSearchForm<PaymentSearch>,
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
      accessorKey: 'paymentDate', header: 'Date', enableSorting: true, cell: info => { return sortableDateFormat(info.getValue()) }, meta: {
        eventOnDoubleClick: this.eventOnClickOpenQuotation
      }
    })
    columns.push({
      accessorKey: 'originPayment', header: 'Paiment d\'origine', enableSorting: true, cell: info => info.getValue(), meta: {
        eventOnDoubleClick: this.eventOnClickOpenQuotation
      }
    })
    columns.push({
      accessorFn: (originalRow: PaymentDto, index: number) => { return formatCurrency(originalRow.paymentAmount) }, header: 'Montant du paiement', enableSorting: true, cell: info => info.getValue(), meta: {
        eventOnDoubleClick: this.eventOnClickOpenQuotation
      }
    })
    columns.push({
      accessorKey: 'paymentType', header: 'Type de paiement', enableSorting: true, cell: info => info.getValue(), meta: {
        eventOnDoubleClick: this.eventOnClickOpenQuotation
      }
    })
    columns.push({
      accessorKey: 'label', header: 'Libellé', enableSorting: true, cell: info => info.getValue(), meta: {
        eventOnDoubleClick: this.eventOnClickOpenQuotation
      }
    })
    columns.push({
      accessorFn: (originalRow: PaymentDto, index: number) => { return formatBoolean(originalRow.isAssociated) }, header: 'Est associé ?', enableSorting: true, cell: info => info.getValue(), meta: {
        eventOnDoubleClick: this.eventOnClickOpenQuotation
      }
    })
    columns.push({
      accessorFn: (originalRow: PaymentDto, index: number) => { return formatBoolean(originalRow.isCancelled) }, header: 'Est annulé ?', enableSorting: true, cell: info => info.getValue(), meta: {
        eventOnDoubleClick: this.eventOnClickOpenQuotation
      }
    })
    columns.push({
      accessorFn: (originalRow: PaymentDto, index: number) => { return formatBoolean(originalRow.isAppoint) }, header: 'Est appoint ?', enableSorting: true, cell: info => info.getValue(), meta: {
        eventOnDoubleClick: this.eventOnClickOpenQuotation
      }
    })
    columns.push({
      accessorKey: 'invoiceId', header: 'Numéro de la facture', enableSorting: true, cell: info => info.getValue(), meta: {
        eventOnDoubleClick: this.eventOnClickOpenQuotation
      }
    })
    columns.push({
      accessorKey: 'customerOrderId', header: 'Numéro de la commande', enableSorting: true, cell: info => info.getValue(), meta: {
        eventOnDoubleClick: this.eventOnClickOpenQuotation
      }
    })
    columns.push({
      accessorKey: 'comment', header: 'Commentaires', enableSorting: true, cell: info => info.getValue(), meta: {
        eventOnDoubleClick: this.eventOnClickOpenQuotation
      }
    })
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
