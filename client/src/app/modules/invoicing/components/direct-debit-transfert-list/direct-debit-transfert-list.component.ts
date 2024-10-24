import { AfterContentChecked, ChangeDetectorRef, Component, Input, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { formatDateTimeForSortTable, formatEurosForSortTable, toIsoString } from 'src/app/libs/FormatHelper';
import { SortTableAction } from 'src/app/modules/miscellaneous/model/SortTableAction';
import { SortTableColumn } from 'src/app/modules/miscellaneous/model/SortTableColumn';
import { UserPreferenceService } from 'src/app/services/user.preference.service';
import { AppService } from '../../../../services/app.service';
import { DirectDebitTransfertService } from '../../../quotation/services/direct.debit.transfert.service';
import { DirectDebitTransfertSearch } from '../../model/DirectDebitTransfertSearch';
import { DirectDebitTransfertSearchResult } from '../../model/DirectDebitTransfertSearchResult';
import { DirectDebitTransfertSearchResultService } from '../../services/direct.debit.transfert.search.result.service';

@Component({
  selector: 'direct-debit-transfer-list',
  templateUrl: './direct-debit-transfert-list.component.html',
  styleUrls: ['./direct-debit-transfert-list.component.css']
})
export class DirectDebitTransfertListComponent implements OnInit, AfterContentChecked {

  @Input() transfertSearch: DirectDebitTransfertSearch = {} as DirectDebitTransfertSearch;
  transfers: DirectDebitTransfertSearchResult[] | undefined;
  availableColumns: SortTableColumn<DirectDebitTransfertSearchResult>[] = [];
  displayedColumns: SortTableColumn<DirectDebitTransfertSearchResult>[] = [];
  tableAction: SortTableAction<DirectDebitTransfertSearchResult>[] = [];
  bookmark: DirectDebitTransfertSearch | undefined;

  constructor(
    private directDebitTransfertSearchResultService: DirectDebitTransfertSearchResultService,
    private changeDetectorRef: ChangeDetectorRef,
    private formBuilder: FormBuilder,
    private directDebitTransfertService: DirectDebitTransfertService,
    private activatedRoute: ActivatedRoute,
    private appService: AppService,
    private userPreferenceService: UserPreferenceService
  ) { }

  ngAfterContentChecked(): void {
    this.changeDetectorRef.detectChanges();
  }

  ngOnInit() {
    this.availableColumns = [];
    this.availableColumns.push({ id: "id", fieldName: "id", label: "N° du prélèvement" } as SortTableColumn<DirectDebitTransfertSearchResult>);
    this.availableColumns.push({ id: "customerOrderLabel", fieldName: "customerOrderLabel", label: "Payeur" } as SortTableColumn<DirectDebitTransfertSearchResult>);
    this.availableColumns.push({ id: "transfertDate", fieldName: "transfertDate", label: "Date", valueFonction: formatDateTimeForSortTable } as SortTableColumn<DirectDebitTransfertSearchResult>);
    this.availableColumns.push({ id: "transfertAmount", fieldName: "transfertAmount", label: "Montant", valueFonction: formatEurosForSortTable } as SortTableColumn<DirectDebitTransfertSearchResult>);
    this.availableColumns.push({ id: "transfertLabel", fieldName: "transfertLabel", label: "Libellé" } as SortTableColumn<DirectDebitTransfertSearchResult>);
    this.availableColumns.push({ id: "isAlreadyExported", fieldName: "isAlreadyExported", label: "A été exporté", valueFonction: (element: DirectDebitTransfertSearchResult, column: SortTableColumn<DirectDebitTransfertSearchResult>) => { return (element.isAlreadyExported) ? "Oui" : "Non" } } as SortTableColumn<DirectDebitTransfertSearchResult>);
    this.availableColumns.push({ id: "isMatched", fieldName: "isMatched", label: "A été rapproché", valueFonction: (element: DirectDebitTransfertSearchResult, column: SortTableColumn<DirectDebitTransfertSearchResult>) => { return (element.isMatched) ? "Oui" : "Non" } } as SortTableColumn<DirectDebitTransfertSearchResult>);

    this.setColumns();

    this.tableAction.push({
      actionIcon: 'delete', actionName: 'Supprimer ce prélèvement', actionClick: (column: SortTableAction<DirectDebitTransfertSearchResult>, element: DirectDebitTransfertSearchResult, event: any) => {
        this.directDebitTransfertService.cancelDirectDebitTransfert(element as any).subscribe(response => this.searchTransferts());
      }, display: true,
    } as SortTableAction<DirectDebitTransfertSearchResult>);


    this.transfertSearch.isHideExportedDirectDebitTransfert = true;

    let idDirectDebitTransfert = this.activatedRoute.snapshot.params.id;
    if (idDirectDebitTransfert) {
      this.transfertSearch.idDirectDebitTransfert = idDirectDebitTransfert;
      this.transfertSearch.isHideExportedDirectDebitTransfert = false;
      this.appService.changeHeaderTitle("Prélèvements");
      this.searchTransferts();
    } else {
      this.bookmark = this.userPreferenceService.getUserSearchBookmark("direct-debit-transfert") as DirectDebitTransfertSearch;
      if (this.bookmark) {
        this.transfertSearch = this.bookmark;
        if (this.transfertSearch.startDate)
          this.transfertSearch.startDate = new Date(this.transfertSearch.startDate);
        if (this.transfertSearch.endDate)
          this.transfertSearch.endDate = new Date(this.transfertSearch.endDate);
        this.searchTransferts();
      }
    }
  }

  transfertForm = this.formBuilder.group({
  });

  setColumns() {
    this.displayedColumns.push(...this.availableColumns);
  }

  searchTransferts() {
    if (this.transfertForm.valid) {
      if (this.transfertSearch.startDate)
        this.transfertSearch.startDate = new Date(toIsoString(this.transfertSearch.startDate));
      if (this.transfertSearch.endDate)
        this.transfertSearch.endDate = new Date(toIsoString(this.transfertSearch.endDate));
      if (!this.transfertSearch.idDirectDebitTransfert)
        this.userPreferenceService.setUserSearchBookmark(this.transfertSearch, "direct-debit-transfert");
      this.directDebitTransfertSearchResultService.getTransferts(this.transfertSearch).subscribe(response => {
        this.transfers = response;
      })
    }
  }

  exportTransferts() {
    this.directDebitTransfertSearchResultService.exportTransferts(this.transfertSearch);
  }
}
