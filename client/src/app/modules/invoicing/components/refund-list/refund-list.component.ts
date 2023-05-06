import { AfterContentChecked, ChangeDetectorRef, Component, Input, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { formatDateTimeForSortTable, formatEurosForSortTable, toIsoString } from 'src/app/libs/FormatHelper';
import { SortTableAction } from 'src/app/modules/miscellaneous/model/SortTableAction';
import { SortTableColumn } from 'src/app/modules/miscellaneous/model/SortTableColumn';
import { RefundSearch } from '../../model/RefundSearch';
import { RefundSearchResult } from '../../model/RefundSearchResult';
import { RefundSearchResultService } from '../../services/refund.search.result.service';

@Component({
  selector: 'refund-list',
  templateUrl: './refund-list.component.html',
  styleUrls: ['./refund-list.component.css']
})
export class RefundListComponent implements OnInit, AfterContentChecked {

  @Input() refundSearch: RefundSearch = {} as RefundSearch;
  @Input() isForDashboard: boolean = false;
  refunds: RefundSearchResult[] | undefined;
  availableColumns: SortTableColumn[] = [];
  displayedColumns: SortTableColumn[] = [];
  columnToDisplayOnDashboard: string[] = ["refundDate", "refundAmount", "refundLabel"];
  tableAction: SortTableAction[] = [];

  constructor(
    private refundSearchResultService: RefundSearchResultService,
    private changeDetectorRef: ChangeDetectorRef,
    private formBuilder: FormBuilder,
  ) { }

  ngAfterContentChecked(): void {
    this.changeDetectorRef.detectChanges();
  }

  ngOnInit() {
    this.availableColumns = [];
    this.availableColumns.push({ id: "id", fieldName: "id", label: "N° du remboursement" } as SortTableColumn);
    this.availableColumns.push({ id: "refundDate", fieldName: "refundDate", label: "Date", valueFonction: formatDateTimeForSortTable } as SortTableColumn);
    this.availableColumns.push({ id: "refundAmount", fieldName: "refundAmount", label: "Montant", valueFonction: formatEurosForSortTable } as SortTableColumn);
    this.availableColumns.push({ id: "refundTiersLabel", fieldName: "refundTiersLabel", label: "Tiers remboursé" } as SortTableColumn);
    this.availableColumns.push({ id: "refundLabel", fieldName: "refundLabel", label: "Libellé" } as SortTableColumn);
    this.availableColumns.push({ id: "affaireLabel", fieldName: "affaireLabel", label: "Affaire" } as SortTableColumn);
    this.availableColumns.push({ id: "isMatched", fieldName: "isMatched", label: "Est rapproché", valueFonction: (element: any) => { return (element.isMatched) ? "Oui" : "Non" } } as SortTableColumn);
    this.availableColumns.push({ id: "isAlreadyExported", fieldName: "isAlreadyExported", label: "A été exporté", valueFonction: (element: any) => { return (element.isAlreadyExported) ? "Oui" : "Non" } } as SortTableColumn);

    this.setColumns();

    this.refundSearch.isHideExportedRefunds = true;
    this.refundSearch.isHideMatchedRefunds = true;

    if (this.isForDashboard && !this.refunds && this.refundSearch) {
      this.searchRefunds();
    }
  }

  refundForm = this.formBuilder.group({
  });

  setColumns() {
    this.displayedColumns = [];
    if (this.availableColumns && this.columnToDisplayOnDashboard && this.isForDashboard) {
      for (let availableColumn of this.availableColumns)
        for (let columnToDisplay of this.columnToDisplayOnDashboard)
          if (availableColumn.id == columnToDisplay)
            this.displayedColumns.push(availableColumn);
    }
    else
      this.displayedColumns.push(...this.availableColumns);
  }

  searchRefunds() {
    if (this.refundForm.valid) {
      if (this.refundSearch.startDate)
        this.refundSearch.startDate = new Date(toIsoString(this.refundSearch.startDate));
      if (this.refundSearch.endDate)
        this.refundSearch.endDate = new Date(toIsoString(this.refundSearch.endDate));
      this.refundSearchResultService.getRefunds(this.refundSearch).subscribe(response => {
        this.refunds = response;
      })
    }
  }

  exportRefunds() {
    this.refundSearchResultService.exportRefunds(this.refundSearch);
  }
}
