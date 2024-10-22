import { AfterContentChecked, ChangeDetectorRef, Component, Input, OnInit } from '@angular/core';
import { OutboundCheckSearch } from '../../model/OutboundCheckSearch';
import { OutboundCheckSearchResult } from '../../model/OutboundCheckSearchResult';
import { SortTableColumn } from 'src/app/modules/miscellaneous/model/SortTableColumn';
import { SortTableAction } from 'src/app/modules/miscellaneous/model/SortTableAction';
import { formatDateTimeForSortTable, formatEurosForSortTable, toIsoString } from 'src/app/libs/FormatHelper';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { AppService } from 'src/app/services/app.service';
import { UserPreferenceService } from 'src/app/services/user.preference.service';
import { OutboundCheckSearchResultService } from '../../services/outbound.check.search.service';
import { Payment } from '../../model/Payment';
import { PaymentDetailsDialogService } from '../../services/payment.details.dialog.service';

@Component({
  selector: 'outbound-check-list',
  templateUrl: './outbound-check-list.component.html',
  styleUrls: ['./outbound-check-list.component.css']
})
export class OutboundCheckListComponent implements OnInit, AfterContentChecked {
  @Input() outboundCheckSearch: OutboundCheckSearch = {} as OutboundCheckSearch;
  @Input() isForDashboard: boolean = false;
  outboundChecks: OutboundCheckSearchResult[] | undefined;
  availableColumns: SortTableColumn<OutboundCheckSearchResult>[] = [];
  displayedColumns: SortTableColumn<OutboundCheckSearchResult>[] = [];
  columnToDisplayOnDashboard: string[] = ["outboundCheckDate", "outboundCheckAmount", "outboundCheckLabel"];
  tableAction: SortTableAction<OutboundCheckSearchResult>[] = [];
  bookmark: OutboundCheckSearch | undefined;

  constructor(private changeDetectorRef: ChangeDetectorRef,
    private formBuilder: FormBuilder,
    private activatedRoute: ActivatedRoute,
    private appService: AppService,
    private userPreferenceService: UserPreferenceService,
    private outboundCheckSearchResultService: OutboundCheckSearchResultService,
    private paymentDetailsDialogService: PaymentDetailsDialogService
  ) {
  }
  ngAfterContentChecked(): void {
    this.changeDetectorRef.detectChanges();
  }

  ngOnInit() {
    this.availableColumns = [];
    this.availableColumns.push({ id: "outboundCheckNumber", fieldName: "outboundCheckNumber", label: "N° de chèque" } as SortTableColumn<OutboundCheckSearchResult>);
    this.availableColumns.push({ id: "paymentNumber", fieldName: "paymentNumber", label: "N° de paiement", actionFunction: (element: OutboundCheckSearchResult) => this.paymentDetailsDialogService.displayPaymentDetailsDialog({ id: element.paymentNumber } as Payment), actionIcon: "visibility", actionTooltip: "Voir le détail du paiement" } as SortTableColumn<OutboundCheckSearchResult>);
    this.availableColumns.push({ id: "outboundCheckDate", fieldName: "outboundCheckDate", label: "Date", valueFonction: formatDateTimeForSortTable } as SortTableColumn<OutboundCheckSearchResult>);
    this.availableColumns.push({ id: "outboundCheckAmount", fieldName: "outboundCheckAmount", label: "Montant", valueFonction: formatEurosForSortTable } as SortTableColumn<OutboundCheckSearchResult>);
    this.availableColumns.push({ id: "outboundCheckLabel", fieldName: "outboundCheckLabel", label: "Libellé" } as SortTableColumn<OutboundCheckSearchResult>);
    this.availableColumns.push({ id: "invoiceAssociated", fieldName: "invoiceAssociated", label: "Facture associée" } as SortTableColumn<OutboundCheckSearchResult>);
    this.availableColumns.push({ id: "isMatched", fieldName: "isMatched", label: "Est rapproché", valueFonction: (element: OutboundCheckSearchResult, column: SortTableColumn<OutboundCheckSearchResult>) => { return (element.isMatched) ? "Oui" : "Non" } } as SortTableColumn<OutboundCheckSearchResult>);

    this.setColumns();

    this.outboundCheckSearch.isHideMatchedOutboundChecks = true;

    if (this.isForDashboard && !this.outboundChecks && this.outboundCheckSearch) {
      this.searchOutboundChecks();
    }

    let idOutboundCheck = this.activatedRoute.snapshot.params.id;
    if (idOutboundCheck) {
      this.outboundCheckSearch.idOutboundCheck = idOutboundCheck;
      this.outboundCheckSearch.isHideMatchedOutboundChecks = false;
      this.appService.changeHeaderTitle("Chèques sortants");
      this.searchOutboundChecks();
    } else {
      this.bookmark = this.userPreferenceService.getUserSearchBookmark("outboundChecks") as OutboundCheckSearch;
      if (this.bookmark) {
        this.outboundCheckSearch = this.bookmark;
        if (this.outboundCheckSearch.startDate)
          this.outboundCheckSearch.startDate = new Date(this.outboundCheckSearch.startDate);
        if (this.outboundCheckSearch.endDate)
          this.outboundCheckSearch.endDate = new Date(this.outboundCheckSearch.endDate);
        this.searchOutboundChecks();
      }
    }
  }

  outboundCheckForm = this.formBuilder.group({
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

  searchOutboundChecks() {
    if (this.outboundCheckForm.valid) {
      if (this.outboundCheckSearch.startDate)
        this.outboundCheckSearch.startDate = new Date(toIsoString(this.outboundCheckSearch.startDate));
      if (this.outboundCheckSearch.endDate)
        this.outboundCheckSearch.endDate = new Date(toIsoString(this.outboundCheckSearch.endDate));
      if (!this.outboundCheckSearch.idOutboundCheck)
        this.userPreferenceService.setUserSearchBookmark(this.outboundCheckSearch, "outboundChecks");
      this.outboundCheckSearchResultService.getOutboundChecks(this.outboundCheckSearch).subscribe(response => {
        this.outboundChecks = response;
      })
    }
  }
}
