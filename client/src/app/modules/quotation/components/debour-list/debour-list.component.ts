import { ChangeDetectorRef, Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { formatDateForSortTable, formatEurosForSortTable } from 'src/app/libs/FormatHelper';
import { SortTableAction } from 'src/app/modules/miscellaneous/model/SortTableAction';
import { SortTableColumn } from 'src/app/modules/miscellaneous/model/SortTableColumn';
import { ConstantService } from 'src/app/modules/miscellaneous/services/constant.service';
import { AppService } from 'src/app/services/app.service';
import { DebourSearchResultService } from '../../../invoicing/services/debour.search.result.service';
import { DebourSearch } from '../../model/DebourSearch';
import { DebourSearchResult } from '../../model/DebourSearchResult';

@Component({
  selector: 'debour-list',
  templateUrl: './debour-list.component.html',
  styleUrls: ['./debour-list.component.css']
})
export class DebourListComponent implements OnInit {

  debourSearch: DebourSearch = {} as DebourSearch;
  @Input() isForPaymentAssociation: boolean = false;
  debours: DebourSearchResult[] | undefined;
  displayedColumns: SortTableColumn[] = [];
  tableAction: SortTableAction[] = [];

  @Output() actionBypass: EventEmitter<DebourSearchResult> = new EventEmitter<DebourSearchResult>();
  @Input() overrideIconAction: string = "";
  @Input() overrideTooltipAction: string = "";
  @Input() defaultStatusFilter: string[] | undefined;

  constructor(
    private debourSearchResultService: DebourSearchResultService,
    private constantService: ConstantService,
    private changeDetectorRef: ChangeDetectorRef,
    private appService: AppService,
    private formBuilder: FormBuilder,
  ) { }

  ngAfterContentChecked(): void {
    this.changeDetectorRef.detectChanges();
  }

  ngOnInit() {
    this.displayedColumns = [];
    this.displayedColumns.push({ id: "id", fieldName: "id", label: "N°" } as SortTableColumn);
    this.displayedColumns.push({ id: "billingType", fieldName: "billingTypeLabel", label: "Débour" } as SortTableColumn);
    this.displayedColumns.push({ id: "competentAuthority", fieldName: "competentAuthority.label", label: "Autorité compétente" } as SortTableColumn);
    this.displayedColumns.push({ id: "debourAmount", fieldName: "debourAmount", label: "Montant", valueFonction: formatEurosForSortTable } as SortTableColumn);
    this.displayedColumns.push({ id: "paymentType", fieldName: "paymentTypeLabel", label: "Type de paiement" } as SortTableColumn);
    this.displayedColumns.push({ id: "paymentDateTime", fieldName: "paymentDateTime", label: "Date de paiement", valueFonction: formatDateForSortTable } as SortTableColumn);
    this.displayedColumns.push({ id: "checkNumber", fieldName: "checkNumber", label: "N° de chèque" } as SortTableColumn);
    this.displayedColumns.push({ id: "payment", fieldName: "paymentId", label: "Paiement associé" } as SortTableColumn);
    this.displayedColumns.push({ id: "invoice", fieldName: "invoiceId", label: "Facture associée" } as SortTableColumn);
    this.displayedColumns.push({ id: "customerOrder", fieldName: "customerOrderId", label: "Commande associée" } as SortTableColumn);
    this.displayedColumns.push({ id: "comments", fieldName: "comments", label: "Commentaires", isShrinkColumn: true } as SortTableColumn);

    if (this.isForPaymentAssociation) {
      this.debourSearch.isCompetentAuthorityDirectCharge = true;
      this.debourSearch.isNonAssociated = true;
    }


    this.tableAction.push({
      actionIcon: this.overrideIconAction, actionName: this.overrideTooltipAction, actionClick: (action: SortTableAction, element: any) => {
        this.actionBypass.emit(element);
      }, display: true,
    } as SortTableAction);

  }

  debourForm = this.formBuilder.group({
  });

  searchDebours() {
    if (this.debourForm.valid) {
      this.debourSearchResultService.getDebours(this.debourSearch).subscribe(response => {
        this.debours = response;
      })
    }
  }
}
