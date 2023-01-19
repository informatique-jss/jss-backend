import { ChangeDetectorRef, Component, EventEmitter, Input, OnInit, Output, SimpleChanges } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { formatDateTimeForSortTable } from 'src/app/libs/FormatHelper';
import { instanceOfCustomerOrder, instanceOfQuotation } from 'src/app/libs/TypeHelper';
import { getCustomerOrderForIQuotation } from 'src/app/modules/invoicing/components/invoice-tools';
import { SortTableColumn } from 'src/app/modules/miscellaneous/model/SortTableColumn';
import { SpecialOffer } from 'src/app/modules/miscellaneous/model/SpecialOffer';
import { DocumentTypeService } from 'src/app/modules/miscellaneous/services/document.type.service';
import { TiersService } from 'src/app/modules/tiers/services/tiers.service';
import { IndexEntityService } from 'src/app/routing/search/index.entity.service';
import { IndexEntity } from '../../../../routing/search/IndexEntity';
import { AppService } from '../../../../services/app.service';
import { Document } from '../../../miscellaneous/model/Document';
import { DocumentType } from '../../../miscellaneous/model/DocumentType';
import { SortTableAction } from '../../../miscellaneous/model/SortTableAction';
import { ResponsableService } from '../../../tiers/services/responsable.service';
import { Confrere } from '../../model/Confrere';
import { IQuotation } from '../../model/IQuotation';
import { QuotationComponent } from '../quotation/quotation.component';

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

  customerOrderTableActions: SortTableAction[] = [] as Array<SortTableAction>;
  customerOrderDisplayedColumns: SortTableColumn[] = [] as Array<SortTableColumn>;

  quotationTableActions: SortTableAction[] = [] as Array<SortTableAction>;
  quotationDisplayedColumns: SortTableColumn[] = [] as Array<SortTableColumn>;

  constructor(private formBuilder: UntypedFormBuilder,
    private tiersService: TiersService,
    private appService: AppService,
    protected cd: ChangeDetectorRef,
    private responsableService: ResponsableService,
    private indexEntityService: IndexEntityService,
    protected documentTypeService: DocumentTypeService,
    public specialOfferDialog: MatDialog) { }

  ngOnChanges(changes: SimpleChanges) {
    if (changes.quotation) {
      if (!this.quotation.overrideSpecialOffer) {
        this.quotation.overrideSpecialOffer = false;
        this.initSpecialOffers();
      }

      if (this.quotation.responsable && this.quotation.responsable.id && !this.searchedResponsable) {
        this.indexEntityService.getResponsableByKeyword(this.quotation.responsable.id + "").subscribe(response => this.searchedResponsable = response[0]);
      }
      if (this.quotation.tiers && this.quotation.tiers.id && !this.searchedTiers) {
        this.indexEntityService.getIndividualTiersByKeyword(this.quotation.tiers.id + "").subscribe(response => this.searchedTiers = response[0]);
      }
      this.orderingCustomerForm.markAllAsTouched();
    }
  }

  ngOnInit() {
    this.orderingCustomerForm.markAllAsTouched();

    this.customerOrderDisplayedColumns = [];
    this.customerOrderDisplayedColumns.push({ id: "id", fieldName: "id", label: "N° de la commande" } as SortTableColumn);
    this.customerOrderDisplayedColumns.push({ id: "quotationStatus", fieldName: "customerOrderStatus.label", label: "Statut" } as SortTableColumn);
    this.customerOrderDisplayedColumns.push({ id: "createdDate", fieldName: "createdDate", label: "Date de création", valueFonction: formatDateTimeForSortTable } as SortTableColumn);
    this.customerOrderDisplayedColumns.push({ id: "totalPrice", fieldName: "totalPrice", label: "Prix total", valueFonction: (element: any, elements: any[], column: SortTableColumn, columns: SortTableColumn[]): string => { return QuotationComponent.computePriceTotal(element) + " €"; } } as SortTableColumn);

    this.customerOrderTableActions.push({
      actionIcon: "visibility", actionName: "Voir la commande", actionLinkFunction: (action: SortTableAction, element: any) => {
        if (element)
          return ['/order', element.id];
        return undefined;
      }, display: true,
    } as SortTableAction);

    this.quotationDisplayedColumns = [];
    this.quotationDisplayedColumns.push({ id: "id", fieldName: "id", label: "N° du devis" } as SortTableColumn);
    this.quotationDisplayedColumns.push({ id: "quotationStatus", fieldName: "quotationStatus.label", label: "Statut" } as SortTableColumn);
    this.quotationDisplayedColumns.push({ id: "createdDate", fieldName: "createdDate", label: "Date de création", valueFonction: formatDateTimeForSortTable } as SortTableColumn);
    this.quotationDisplayedColumns.push({ id: "totalPrice", fieldName: "totalPrice", label: "Prix total", valueFonction: (element: any, elements: any[], column: SortTableColumn, columns: SortTableColumn[]): string => { return QuotationComponent.computePriceTotal(element) + " €"; } } as SortTableColumn);

    this.quotationTableActions.push({
      actionIcon: "visibility", actionName: "Voir le devis", actionLinkFunction: (action: SortTableAction, element: any) => {
        if (element)
          return ['/quotation', element.id];
        return undefined;
      }, display: true,
    } as SortTableAction);

  }

  orderingCustomerForm = this.formBuilder.group({
  });

  getCustomerOrderForIQuotation = getCustomerOrderForIQuotation;

  displayOverrideSpecialOffers() {
    this.quotation.overrideSpecialOffer = true;
  }

  initSpecialOffers() {
    this.quotation.specialOffers = [] as Array<SpecialOffer>;
    if (this.quotation.tiers && this.quotation.tiers.specialOffers)
      this.quotation.specialOffers.push(...this.quotation.tiers.specialOffers);

    if (this.quotation.confrere && this.quotation.confrere.specialOffers)
      this.quotation.specialOffers.push(...this.quotation.confrere.specialOffers);

    if (this.quotation.responsable && this.quotation.responsable.tiers && this.quotation.responsable.tiers.specialOffers)
      this.quotation.specialOffers.push(...this.quotation.responsable.tiers.specialOffers);
  }


  fillTiers(tiers: IndexEntity) {
    this.tiersService.getTiers(tiers.entityId).subscribe(response => {
      this.quotation.tiers = response;
      this.quotation.responsable = undefined;
      this.quotation.responsable = undefined;
      if (this.quotation.tiers) {
        this.quotation.observations = this.quotation.tiers.observations;
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

}
