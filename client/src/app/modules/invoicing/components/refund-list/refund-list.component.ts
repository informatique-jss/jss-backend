import { AfterContentChecked, ChangeDetectorRef, Component, Input, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { formatDateTimeForSortTable, formatEurosForSortTable, toIsoString } from 'src/app/libs/FormatHelper';
import { SortTableAction } from 'src/app/modules/miscellaneous/model/SortTableAction';
import { SortTableColumn } from 'src/app/modules/miscellaneous/model/SortTableColumn';
import { AppService } from 'src/app/services/app.service';
import { UserPreferenceService } from 'src/app/services/user.preference.service';
import { RefundSearch } from '../../model/RefundSearch';
import { RefundSearchResult } from '../../model/RefundSearchResult';
import { RefundSearchResultService } from '../../services/refund.search.result.service';
import { MatDialog } from '@angular/material/dialog';
import { EditRefundLabelDialogComponent } from 'src/app/modules/miscellaneous/components/edit-refund-label-dialog/edit-refund-label-dialog.component';
import { RefundService } from '../../services/refund.service';

@Component({
  selector: 'refund-list',
  templateUrl: './refund-list.component.html',
  styleUrls: ['./refund-list.component.css']
})
export class RefundListComponent implements OnInit, AfterContentChecked {

  @Input() refundSearch: RefundSearch = {} as RefundSearch;
  @Input() isForDashboard: boolean = false;
  refunds: RefundSearchResult[] | undefined;
  availableColumns: SortTableColumn<RefundSearchResult>[] = [];
  displayedColumns: SortTableColumn<RefundSearchResult>[] = [];
  columnToDisplayOnDashboard: string[] = ["refundDate", "refundAmount", "refundLabel"];
  tableAction: SortTableAction<RefundSearchResult>[] = [];
  bookmark: RefundSearch | undefined;

  constructor(
    private refundSearchResultService: RefundSearchResultService,
    private changeDetectorRef: ChangeDetectorRef,
    private refundService: RefundService,
    private formBuilder: FormBuilder,
    private activatedRoute: ActivatedRoute,
    private appService: AppService,
    private userPreferenceService: UserPreferenceService,
    public editRefundLabelDialog: MatDialog,
  ) { }

  ngAfterContentChecked(): void {
    this.changeDetectorRef.detectChanges();
  }

  ngOnInit() {
    this.availableColumns = [];
    this.availableColumns.push({ id: "id", fieldName: "id", label: "N° du remboursement" } as SortTableColumn<RefundSearchResult>);
    this.availableColumns.push({ id: "refundDate", fieldName: "refundDate", label: "Date", valueFonction: formatDateTimeForSortTable } as SortTableColumn<RefundSearchResult>);
    this.availableColumns.push({ id: "refundAmount", fieldName: "refundAmount", label: "Montant", valueFonction: formatEurosForSortTable } as SortTableColumn<RefundSearchResult>);
    this.availableColumns.push({ id: "refundTiersLabel", fieldName: "refundTiersLabel", label: "Tiers remboursé" } as SortTableColumn<RefundSearchResult>);
    this.availableColumns.push({ id: "refundLabel", fieldName: "refundLabel", label: "Libellé" } as SortTableColumn<RefundSearchResult>);
    this.availableColumns.push({ id: "refundIban", fieldName: "refundIban", label: "IBAN" } as SortTableColumn<RefundSearchResult>);
    this.availableColumns.push({ id: "affaireLabel", fieldName: "affaireLabel", label: "Affaire" } as SortTableColumn<RefundSearchResult>);
    this.availableColumns.push({ id: "isMatched", fieldName: "isMatched", label: "Est rapproché", valueFonction: (element: RefundSearchResult, column: SortTableColumn<RefundSearchResult>) => { return (element.isMatched) ? "Oui" : "Non" } } as SortTableColumn<RefundSearchResult>);
    this.availableColumns.push({ id: "isAlreadyExported", fieldName: "isAlreadyExported", label: "A été exporté", valueFonction: (element: RefundSearchResult, column: SortTableColumn<RefundSearchResult>) => { return (element.isAlreadyExported) ? "Oui" : "Non" } } as SortTableColumn<RefundSearchResult>);
    this.tableAction.push({
      actionIcon: 'edit', actionName: "Modifier le libellé", actionClick: (column: SortTableAction<RefundSearchResult>, element: RefundSearchResult, event: any) => {
        if (element) {
          const dialogRef = this.editRefundLabelDialog.open(EditRefundLabelDialogComponent, { maxWidth: "400px" });
          dialogRef.componentInstance.refundLabel = element.refundLabel;

          dialogRef.afterClosed().subscribe(dialogResult => {
            if (dialogResult) {
              this.refundService.getRefund(element.id).subscribe(response => {
                response.label = dialogResult;
                this.refundService.addOrUpdateRefund(response).subscribe(done => {
                  this.searchRefunds();
                });
              });
            }
          });
        }
      }, display: true,
    } as SortTableAction<RefundSearchResult>);
    this.setColumns();

    this.refundSearch.isHideExportedRefunds = true;
    this.refundSearch.isHideMatchedRefunds = true;

    if (this.isForDashboard && !this.refunds && this.refundSearch) {
      this.searchRefunds();
    }

    let idRefund = this.activatedRoute.snapshot.params.id;
    if (idRefund) {
      this.refundSearch.idRefund = idRefund;
      this.refundSearch.isHideExportedRefunds = false;
      this.refundSearch.isHideMatchedRefunds = false;
      this.appService.changeHeaderTitle("Remboursements");
      this.searchRefunds();
    } else {
      this.bookmark = this.userPreferenceService.getUserSearchBookmark("refunds") as RefundSearch;
      if (this.bookmark) {
        this.refundSearch = this.bookmark;
        if (this.refundSearch.startDate)
          this.refundSearch.startDate = new Date(this.refundSearch.startDate);
        if (this.refundSearch.endDate)
          this.refundSearch.endDate = new Date(this.refundSearch.endDate);
        this.searchRefunds();
      }
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
      if (!this.refundSearch.idRefund)
        this.userPreferenceService.setUserSearchBookmark(this.refundSearch, "refunds");
      this.refundSearchResultService.getRefunds(this.refundSearch).subscribe(response => {
        this.refunds = response;
      })
    }
  }

  exportRefunds() {
    this.refundSearchResultService.exportRefunds(this.refundSearch);
  }
}
