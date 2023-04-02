import { AfterContentChecked, ChangeDetectorRef, Component, Input, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { formatDateTimeForSortTable, formatEurosForSortTable, toIsoString } from 'src/app/libs/FormatHelper';
import { SortTableAction } from 'src/app/modules/miscellaneous/model/SortTableAction';
import { SortTableColumn } from 'src/app/modules/miscellaneous/model/SortTableColumn';
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
  availableColumns: SortTableColumn[] = [];
  displayedColumns: SortTableColumn[] = [];
  tableAction: SortTableAction[] = [];

  constructor(
    private directDebitTransfertSearchResultService: DirectDebitTransfertSearchResultService,
    private changeDetectorRef: ChangeDetectorRef,
    private formBuilder: FormBuilder,
    private directDebitTransfertService: DirectDebitTransfertService
  ) { }

  ngAfterContentChecked(): void {
    this.changeDetectorRef.detectChanges();
  }

  ngOnInit() {
    this.availableColumns = [];
    this.availableColumns.push({ id: "id", fieldName: "id", label: "N° du prélèvement" } as SortTableColumn);
    this.availableColumns.push({ id: "transfertDate", fieldName: "transfertDate", label: "Date", valueFonction: formatDateTimeForSortTable } as SortTableColumn);
    this.availableColumns.push({ id: "transfertAmount", fieldName: "transfertAmount", label: "Montant", valueFonction: formatEurosForSortTable } as SortTableColumn);
    this.availableColumns.push({ id: "transfertLabel", fieldName: "transfertLabel", label: "Libellé" } as SortTableColumn);
    this.availableColumns.push({ id: "isAlreadyExported", fieldName: "isAlreadyExported", label: "A été exporté", valueFonction: (element: any) => { return (element.isAlreadyExported) ? "Oui" : "Non" } } as SortTableColumn);

    this.setColumns();

    this.tableAction.push({
      actionIcon: 'delete', actionName: 'Supprimer ce prélèvement', actionClick: (action: SortTableAction, element: any) => {
        this.directDebitTransfertService.cancelDirectDebitTransfert(element).subscribe(response => this.searchTransferts());
      }, display: true,
    } as SortTableAction);


    this.transfertSearch.isHideExportedDirectDebitTransfert = true;
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
      this.directDebitTransfertSearchResultService.getTransferts(this.transfertSearch).subscribe(response => {
        this.transfers = response;
      })
    }
  }

  exportTransferts() {
    this.directDebitTransfertSearchResultService.exportTransferts(this.transfertSearch);
  }
}
