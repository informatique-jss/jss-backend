import { AfterContentChecked, ChangeDetectorRef, Component, Input, OnInit, SimpleChanges, ViewChild } from '@angular/core';
import { SortTableAction } from 'src/app/modules/miscellaneous/model/SortTableAction';
import { SortTableColumn } from 'src/app/modules/miscellaneous/model/SortTableColumn';
import { Tiers } from '../../model/Tiers';
import { UntypedFormBuilder } from '@angular/forms';
import { RffSearch } from '../../model/RffSearch';
import { RffService } from '../../services/rff.service';
import { Rff } from '../../model/Rff';
import { IndexEntity } from 'src/app/routing/search/IndexEntity';
import { Responsable } from '../../model/Responsable';
import { ConstantService } from 'src/app/modules/miscellaneous/services/constant.service';
import { TiersService } from '../../services/tiers.service';
import { formatEurosForSortTable } from 'src/app/libs/FormatHelper';

@Component({
  selector: 'prepa-visite-tiers-info',
  templateUrl: './prepa-visite-tiers-info.component.html',
  styleUrls: ['./prepa-visite-tiers-info.component.css']
})

export class PrepaVisiteTiersComponent implements OnInit, AfterContentChecked {

  @Input() tiers: Tiers = {} as Tiers;
  @Input() rffSearch: RffSearch = {} as RffSearch;
  @Input() editMode: boolean = false;

  rff: Rff[] | undefined;
  availableColumnsTiers: SortTableColumn<Tiers>[] = [];
  displayedColumnsTiers: SortTableColumn<Tiers>[] = [];
  displayedColumnsResponsables: SortTableColumn<Responsable>[] = [];
  displayedColumnsRff: SortTableColumn<Rff>[] = [];

  tiersToDisplay: Tiers[] | undefined;

  selectedResponsableId: number | null = null;

  selectedResponsable: Responsable | null = null;

  constructor(private rffService: RffService,
    private changeDetectorRef: ChangeDetectorRef,
    protected tiersService: TiersService,
  ) { }

  ngAfterContentChecked(): void {
    this.changeDetectorRef.detectChanges();
  }


  ngOnInit() {

    this.tiersToDisplay = [this.tiers];

    this.displayedColumnsTiers = [];
    this.displayedColumnsRff = [];
    this.displayedColumnsResponsables = [];

    this.displayedColumnsTiers.push({ id: "denomination", fieldName: "denomination", label: "Dénomination"  } as SortTableColumn<Tiers>);
    this.displayedColumnsTiers.push({ id: "address", fieldName: "address", label: "address" } as SortTableColumn<Tiers>);
    this.displayedColumnsTiers.push({ id: "mails[0].mail", fieldName: "mails[0].mail", label: "mail" } as SortTableColumn<Tiers>);
    this.displayedColumnsTiers.push({ id: "phones[0].phoneNumber", fieldName: "phones[0].phoneNumber", label: "phone" } as SortTableColumn<Tiers>);
    this.displayedColumnsTiers.push({ id: "rffInsertionRate", fieldName: "rffInsertionRate", label: "rff Insertion Rate" } as SortTableColumn<Tiers>);
    this.displayedColumnsTiers.push({ id: "rffFormaliteRate", fieldName: "rffFormaliteRate", label: "rff Formalite Rate" } as SortTableColumn<Tiers>);
    // this.displayedColumnsRff.push({ id: "rffInsertion", fieldName: "rffInsertion", label: "RFF AL", valueFonction: formatEurosForSortTable } as SortTableColumn<Rff>);
    // this.displayedColumnsRff.push({ id: "rffFormalite", fieldName: "rffFormalite", label: "RFF Formalités", valueFonction: formatEurosForSortTable } as SortTableColumn<Rff>);
    // this.displayedColumnsRff.push({ id: "rffTotal", fieldName: "rffTotal", label: "Total HT", valueFonction: formatEurosForSortTable } as SortTableColumn<Rff>);

    this.displayedColumnsResponsables.push({ id: "id", fieldName: "id", label: "N° du responsable" } as SortTableColumn<Responsable>);
    this.displayedColumnsResponsables.push({ id: "lastname", fieldName: "lastname", label: "Nom" } as SortTableColumn<Responsable>);
    this.displayedColumnsResponsables.push({ id: "firstname", fieldName: "firstname", label: "Prénom" } as SortTableColumn<Responsable>);
    this.displayedColumnsResponsables.push({ id: "function", fieldName: "function", label: "Fonction" } as SortTableColumn<Responsable>);
    this.displayedColumnsResponsables.push({ id: "mails", fieldName: "mails", label: "Mails", valueFonction: (element: Responsable, column: SortTableColumn<Responsable>) => { return ((element.mails) ? element.mails.map((e: { mail: any; }) => e.mail).join(", ") : "") } } as SortTableColumn<Responsable>);
    this.displayedColumnsResponsables.push({ id: "phones", fieldName: "phones", label: "Téléphones", valueFonction: (element: Responsable, column: SortTableColumn<Responsable>) => { return ((element.phones) ? element.phones.map((e: { phoneNumber: any; }) => e.phoneNumber).join(", ") : "") } } as SortTableColumn<Responsable>);


    this.searchRff();
  }

  selectResponsable(responsable: Responsable) {
    let responsableId = responsable.id;
    this.selectResponsableById(responsableId);
  }

  selectResponsableById(responsableId: number) {
    if (this.selectedResponsable == null) {
      this.tiers.responsables.forEach(responsable => {
        if (responsable.id == responsableId) {
          this.selectedResponsable = responsable;

          this.rffSearch = {} as RffSearch;

          setTimeout(() => {
            this.rffSearch = {} as RffSearch;
            this.rffSearch.responsable = { entityId: responsable.id } as IndexEntity;
            this.rffSearch.isHideCancelledRff = false;

            let start = new Date();
            let d = new Date(start.getTime());
            d.setFullYear(d.getFullYear() - 1);
            this.rffSearch.startDate = d;

            let end = new Date();
            let d2 = new Date(end.getTime());
            d2.setFullYear(d2.getFullYear() + 1);
            this.rffSearch.endDate = d2;
          }, 0);

          this.tiersService.setCurrentViewedResponsable(responsable);

        }
      })
    }
  }

  searchRff() {
    this.rffSearch = {} as RffSearch;
    this.rffSearch.tiers = { entityId: this.tiers.id } as IndexEntity;
    this.rffSearch.isHideCancelledRff = false;

    let start = new Date();
    let d = new Date(start.getTime());
    d.setFullYear(d.getFullYear() - 1);
    this.rffSearch.startDate = d;

    let end = new Date();
    let d2 = new Date(end.getTime());
    d2.setFullYear(d2.getFullYear() + 1);
    this.rffSearch.endDate = d2;

    this.rffService.getRffs(this.rffSearch).subscribe(response => {
      this.rff = response;
    })
  }

  setDataTable() {
    this.tiers.responsables.sort(function (a: Responsable, b: Responsable) {
      return (a.lastname + "" + a.firstname).localeCompare(b.lastname + "" + a.firstname);
    });
  }

}


