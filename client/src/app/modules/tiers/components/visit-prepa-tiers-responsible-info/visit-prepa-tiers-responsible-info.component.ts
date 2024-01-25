import { AfterContentChecked, ChangeDetectorRef, Component, Input, OnInit } from '@angular/core';
import { SortTableColumn } from 'src/app/modules/miscellaneous/model/SortTableColumn';
import { Tiers } from '../../model/Tiers';
import { RffSearch } from '../../model/RffSearch';
import { RffService } from '../../services/rff.service';
import { Rff } from '../../model/Rff';
import { IndexEntity } from 'src/app/routing/search/IndexEntity';
import { Responsable } from '../../model/Responsable';
import { TiersService } from '../../services/tiers.service';
import { formatEurosForSortTable } from 'src/app/libs/FormatHelper';
import { TiersRff } from '../../model/TiersRff';
import { ResponsablesRff } from '../../model/ResponsablesRff';
import { TiersSearch } from '../../model/TiersSearch';
import { ResponsableSearchResultService } from '../../services/responsable.search.result.service';
import { ResponsableSearchResult } from '../../model/ResponsableSearchResult';
import { OrderingSearch } from 'src/app/modules/quotation/model/OrderingSearch';
import { QuotationSearch } from 'src/app/modules/quotation/model/QuotationSearch';
import { AffaireSearch } from 'src/app/modules/quotation/model/AffaireSearch';
import { InvoiceSearch } from 'src/app/modules/invoicing/model/InvoiceSearch';
import { TiersSearchResultService } from '../../services/tiers.search.result.service';
import { TiersSearchResult } from '../../model/TiersSearchResult';
import { LegalForm } from '../../../miscellaneous/model/LegalForm';

@Component({
  selector: 'visit-prepa-tiers-responsible-info',
  templateUrl: './visit-prepa-tiers-responsible-info.component.html',
  styleUrls: ['./visit-prepa-tiers-responsible-info.component.css']
})

export class VisitPrepaTiersResponsibleInfoComponent implements OnInit, AfterContentChecked {

  @Input() tiers: Tiers = {} as Tiers;
  @Input() editMode: boolean = false;

  rffSearch: RffSearch = {} as RffSearch;

  rff: Rff[] | undefined;
  rffList: Rff[] = [];

  tiersRff: TiersRff[] | undefined;
  responsablesRff: ResponsablesRff[] | undefined;
  responsableDummy: ResponsablesRff = {} as ResponsablesRff;

  tiersList: Tiers[] = [];
  rffSearchList: RffSearch[] = [];
  responsablesList: Responsable[] = [];

  responsableSearch: TiersSearch = {} as TiersSearch;
  responsableSearchResult: ResponsableSearchResult[] = [];
  tiersSearchResult: TiersSearchResult[] = [];

  onlyResponsable: Responsable | undefined;

  orderingSearch: OrderingSearch = {} as OrderingSearch;
  quotationSearch: QuotationSearch = {} as QuotationSearch;
  provisionSearch: AffaireSearch = {} as AffaireSearch;
  invoiceSearch: InvoiceSearch = {} as InvoiceSearch;
  responsableAccountSearch: Tiers | undefined;

  displayedColumnsTiersRff: SortTableColumn<TiersRff>[] = [];
  displayedColumnsResponsablesRff: SortTableColumn<ResponsablesRff>[] = [];
  availableColumnsTiersRff: SortTableColumn<TiersRff>[] = [];
  availableColumnsResponsablesRff: SortTableColumn<ResponsablesRff>[] = [];

  constructor(private rffService: RffService,
    private changeDetectorRef: ChangeDetectorRef,
    protected tiersService: TiersService,
    protected responsableSearchResultService :ResponsableSearchResultService,
    protected tiersSearchResultService :TiersSearchResultService,
  ) { }

  ngAfterContentChecked(): void {
    this.changeDetectorRef.detectChanges();
  }


  ngOnInit() {

    this.tiersList = [this.tiers];
    this.responsablesList = this.tiers.responsables;
    this.rffSearchList = [{} as RffSearch];
    this.rff = [{} as Rff];
    this.rffSearch = {} as RffSearch;
    this.responsableDummy = {} as ResponsablesRff;

    this.displayedColumnsTiersRff = [];
    this.displayedColumnsResponsablesRff = [];

    this.displayedColumnsTiersRff.push({ id: "denomination", fieldName: "denomination", label: "Dénomination"  } as SortTableColumn<TiersRff>);
    this.displayedColumnsTiersRff.push({ id: "address", fieldName: "address", label: "address" } as SortTableColumn<TiersRff>);
    this.displayedColumnsTiersRff.push({ id: "mails", fieldName: "mails", label: "Mails", valueFonction: (element: TiersRff, column: SortTableColumn<TiersRff>) => { return ((element.mails) ? element.mails.map((e: { mail: any; }) => e.mail).join(", ") : "") } } as SortTableColumn<TiersRff>);
    this.displayedColumnsTiersRff.push({ id: "phones", fieldName: "phones", label: "Téléphones", valueFonction: (element: TiersRff, column: SortTableColumn<TiersRff>) => { return ((element.phones) ? element.phones.map((e: { phoneNumber: any; }) => e.phoneNumber).join(", ") : "") } } as SortTableColumn<TiersRff>);
    this.displayedColumnsTiersRff.push({ id: "rffInsertion", fieldName: "rffInsertion", label: "RFF AL" } as SortTableColumn<TiersRff>);
    this.displayedColumnsTiersRff.push({ id: "rffFormalite", fieldName: "rffFormalite", label: "RFF Formalités" } as SortTableColumn<TiersRff>);
    this.displayedColumnsTiersRff.push({ id: "rffTotal", fieldName: "rffTotal", label: "Total HT", valueFonction: formatEurosForSortTable } as SortTableColumn<TiersRff>);

    this.displayedColumnsResponsablesRff.push({ id: "id", fieldName: "id", label: "N° du responsable" } as SortTableColumn<ResponsablesRff>);
    this.displayedColumnsResponsablesRff.push({ id: "lastName", fieldName: "lastName", label: "Nom" } as SortTableColumn<ResponsablesRff>);
    this.displayedColumnsResponsablesRff.push({ id: "phones", fieldName: "phones", label: "Téléphones", valueFonction: (element: ResponsablesRff, column: SortTableColumn<ResponsablesRff>) => { return ((element.phones) ? element.phones.map((e: { phoneNumber: any; }) => e.phoneNumber).join(", ") : "") } } as SortTableColumn<ResponsablesRff>);
    this.displayedColumnsResponsablesRff.push({ id: "function", fieldName: "function", label: "Function" } as SortTableColumn<ResponsablesRff>);
    this.displayedColumnsResponsablesRff.push({ id: "mails", fieldName: "mails", label: "Mails", valueFonction: (element: ResponsablesRff, column: SortTableColumn<ResponsablesRff>) => { return ((element.mails) ? element.mails.map((e: { mail: any; }) => e.mail).join(", ") : "") } } as SortTableColumn<ResponsablesRff>);
    this.displayedColumnsResponsablesRff.push({ id: "turnoverAmountWithTax", fieldName: "turnoverAmountWithTax", label: "Chiffre d'affaire", valueFonction: formatEurosForSortTable} as SortTableColumn<ResponsablesRff>);
    this.displayedColumnsResponsablesRff.push({ id: "announcementNbr", fieldName: "announcementNbr", label: "Nbre AL"} as SortTableColumn<ResponsablesRff>);
    this.displayedColumnsResponsablesRff.push({ id: "formalityNbr", fieldName: "formalityNbr", label: "Nbre Form"} as SortTableColumn<ResponsablesRff>);
    this.displayedColumnsResponsablesRff.push({ id: "rffInsertion", fieldName: "rffInsertion", label: "RFF AL" } as SortTableColumn<ResponsablesRff>);
    this.displayedColumnsResponsablesRff.push({ id: "rffFormalite", fieldName: "rffFormalite", label: "RFF Formalités" } as SortTableColumn<ResponsablesRff>);
    this.displayedColumnsResponsablesRff.push({ id: "rffTotal", fieldName: "rffTotal", label: "Total HT", valueFonction: formatEurosForSortTable } as SortTableColumn<ResponsablesRff>);

    this.searchRff();
  }

  searchRff() {

    if(this.tiers.responsables.length>1){
      this.tiers.responsables.forEach(responsable => {
        this.setRffSearch(responsable);
      })
    }else{
        this.onlyResponsable = this.tiers.responsables[0];
        this.setRffSearch(this.onlyResponsable);
    }

    if(this.rffSearchList.length>1){
      this.rffSearchList.forEach(rffSearch => {
          this.rffService.getRffs(rffSearch).subscribe(response => {
            this.rff = response;
          });
      });
    }

      this.rffService.getRffs(this.rffSearchList[0]).subscribe(response => {
        this.rff =response;
      });

      if(this.rff && this.rff?.length>1){
        this.rff?.forEach( (rff: Rff) => {
            if(rff.responsableId!=null)
              this.rffList.push(rff)
        });
    }else if(this.rff && this.rff?.length == 1){
      this.rffList.push(this.rff[0]);
    }
    this.setTiersAndResponsableRffData(this.rffList);
  }

  setRffSearch(responsable: Responsable) {

        this.rffSearch.tiers = { entityId: this.tiers.id } as IndexEntity;
        this.rffSearch.responsable = { entityId: responsable.id } as IndexEntity;
        this.rffSearch.isHideCancelledRff = false;

        let start = new Date();
        let d = new Date(start.getTime());
        d.setFullYear(d.getFullYear() - 2);
        d.setMonth(10);
        d.setDate(30);
        this.rffSearch.startDate = d;

        let end = new Date();
        let d2 = new Date(end.getTime());
        d2.setFullYear(d2.getFullYear() - 1);
        d2.setMonth(11);
        d2.setDate(1);
        this.rffSearch.endDate = d2;

        this.rffSearchList.push({ ...this.rffSearch });

  }

  setTiersAndResponsableRffData(rffList: Rff[]) {

    this.tiersRff = [{} as TiersRff];
    this.responsablesRff = [{} as ResponsablesRff];
    this.responsableDummy = {} as ResponsablesRff;

    this.tiersRff[0].rffInsertion = rffList[0]?.rffInsertion;
    this.tiersRff[0].rffFormalite = rffList[0]?.rffFormalite;
    this.tiersRff[0].rffTotal = rffList[0]?.rffTotal;

    rffList.forEach(rffResponsable => {

      if(rffResponsable.responsableId && this.responsableDummy && this.responsablesRff){
        this.responsableDummy.id = rffResponsable.responsableId;
        this.responsableDummy.rffInsertion = rffResponsable.rffInsertion;
        this.responsableDummy.rffFormalite = rffResponsable.rffFormalite;
        this.responsableDummy.rffTotal = rffResponsable.rffTotal;
        this.responsablesRff.push(this.responsableDummy);
      }
    })
    this.setTiersAndResponsableData(this.tiersRff, this.responsablesRff);
  }

  setTiersAndResponsableData(tiersRff: TiersRff[] , responsablesRff: ResponsablesRff[] ){


    tiersRff[0].id = this.tiers.id;
    if(tiersRff[0].denomination == null){
      if(!this.tiers.isIndividual)
        tiersRff[0].denomination = this.tiers.denomination;
      else
        tiersRff[0].denomination = this.tiers.firstname + " " + this.tiers.lastname;
      tiersRff[0].address = this.tiers.address + " - " + this.tiers.postalCode
      + " "+ this.tiers.city.label + " "+ this.tiers.country.label;
      tiersRff[0].mails = this.tiers.mails;
      tiersRff[0].phones = this.tiers.phones;
    }

    this.tiers.responsables.forEach(responsable => {
      if(responsablesRff.length == 0){
        this.responsableDummy.id = responsable.id;
        this.responsableDummy.lastName = responsable.lastname;
        this.responsableDummy.firstName = responsable.firstname;
        this.responsableDummy.function = responsable.function;
        this.responsableDummy.mails = responsable.mails;
        this.responsableDummy.phones = responsable.phones;
        this.responsablesRff?.push(this.responsableDummy);
      }
    })
     this.setResponsableSearchResultData(this.tiersRff, this.responsablesRff);
  }


  setResponsableSearchResultData(tiersRff: TiersRff[] | undefined , responsablesRff: ResponsablesRff[] | undefined) {

    this.responsableSearch = {} as TiersSearch

    this.responsableSearch.tiers = { entityId: this.tiers.id } as IndexEntity;

    this.tiers.responsables.forEach(responsable => {
      this.responsableSearch.responsable = { entityId: responsable.id } as IndexEntity;

      this.responsableSearchResultService.getResponsableSearch(this.responsableSearch).subscribe(response => {
        this.responsableSearchResult = response;
        if(response){
          this.responsableSearchResult.forEach(responsableSearchResult => {
            responsablesRff?.forEach(responsablesRff => {
              if(responsablesRff.id == responsableSearchResult.responsableId){
                responsablesRff.turnoverAmountWithTax = responsableSearchResult.turnoverAmountWithTax;
                responsablesRff.announcementNbr = responsableSearchResult.announcementNbr;
                responsablesRff.formalityNbr = responsableSearchResult.formalityNbr;
              }
            })
          })
        }
      })
    })
  }

  loadQuotationFilter() {
    this.orderingSearch.customerOrders = [this.tiers];
    this.invoiceSearch.customerOrders = [this.tiers];
    this.quotationSearch.customerOrders = [this.tiers];
    this.provisionSearch.customerOrders = [this.tiers];

    if (this.tiers.responsables) {
      this.orderingSearch.customerOrders.push(...this.tiers.responsables);
      this.invoiceSearch.customerOrders.push(...this.tiers.responsables);
      this.quotationSearch.customerOrders.push(...this.tiers.responsables);
      this.provisionSearch.customerOrders.push(...this.tiers.responsables);
    }

    this.responsableAccountSearch = this.tiers;
  }

}


