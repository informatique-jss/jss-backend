import { AfterContentChecked, ChangeDetectorRef, Component, Input, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { formatDateTimeForSortTable, formatEurosForSortTable, toIsoString } from 'src/app/libs/FormatHelper';
import { SortTableAction } from 'src/app/modules/miscellaneous/model/SortTableAction';
import { SortTableColumn } from 'src/app/modules/miscellaneous/model/SortTableColumn';
import { HabilitationsService } from '../../../../services/habilitations.service';
import { BankTransfertService } from '../../../quotation/services/bank.transfert.service';
import { BankTransfertSearch } from '../../model/BankTransfertSearch';
import { BankTransfertSearchResult } from '../../model/BankTransfertSearchResult';
import { BankTransfertSearchResultService } from '../../services/bank.transfert.search.result.service';

@Component({
  selector: 'bank-transfer-list',
  templateUrl: './bank-transfert-list.component.html',
  styleUrls: ['./bank-transfert-list.component.css']
})
export class BankTransfertListComponent implements OnInit, AfterContentChecked {

  @Input() transfertSearch: BankTransfertSearch = {} as BankTransfertSearch;
  transfers: BankTransfertSearchResult[] | undefined;
  availableColumns: SortTableColumn[] = [];
  displayedColumns: SortTableColumn[] = [];
  tableAction: SortTableAction[] = [];

  constructor(
    private bankTransfertSearchResultService: BankTransfertSearchResultService,
    private changeDetectorRef: ChangeDetectorRef,
    private formBuilder: FormBuilder,
    private bankTransfertService: BankTransfertService,
    private habilitationService: HabilitationsService,
  ) { }

  ngAfterContentChecked(): void {
    this.changeDetectorRef.detectChanges();
  }

  ngOnInit() {
    this.availableColumns = [];
    this.availableColumns.push({ id: "id", fieldName: "id", label: "N° du virement" } as SortTableColumn);
    this.availableColumns.push({ id: "transfertDate", fieldName: "transfertDate", label: "Date", valueFonction: formatDateTimeForSortTable } as SortTableColumn);
    this.availableColumns.push({ id: "transfertAmount", fieldName: "transfertAmount", label: "Montant", valueFonction: formatEurosForSortTable } as SortTableColumn);
    this.availableColumns.push({ id: "transfertLabel", fieldName: "transfertLabel", label: "Libellé" } as SortTableColumn);
    this.availableColumns.push({ id: "affaireLabel", fieldName: "affaireLabel", label: "Affaire" } as SortTableColumn);
    this.availableColumns.push({ id: "isAlreadyExported", fieldName: "isAlreadyExported", label: "A été exporté", valueFonction: (element: any) => { return (element.isAlreadyExported) ? "Oui" : "Non" } } as SortTableColumn);
    this.availableColumns.push({ id: "isSelectedForExport", fieldName: "isSelectedForExport", label: "Est sélectionné pour l'export", valueFonction: (element: any) => { return (element.isSelectedForExport) ? "Oui" : "Non" } } as SortTableColumn);
    this.availableColumns.push({ id: "competentAuthorityLabel", fieldName: "competentAuthorityLabel", label: "Autorité compétente" } as SortTableColumn);
    this.availableColumns.push({ id: "invoiceBillingLabel", fieldName: "invoiceBillingLabel", label: "Libellé de la facture" } as SortTableColumn);

    this.setColumns();

    this.transfertSearch.isHideExportedBankTransfert = true;

    this.tableAction.push({
      actionIcon: 'check_box', actionName: 'Sélectionner ce virement pour l\'export', actionClick: (action: SortTableAction, element: any) => {
        this.bankTransfertService.selectBankTransfertForExport(element).subscribe(response => this.searchTransferts());
      }, display: true,
    } as SortTableAction);

    this.tableAction.push({
      actionIcon: 'check_box_outline_blank', actionName: 'Désélectionner ce virement pour l\'export', actionClick: (action: SortTableAction, element: any) => {
        this.bankTransfertService.unselectBankTransfertForExport(element).subscribe(response => this.searchTransferts());
      }, display: true,
    } as SortTableAction);

    if (this.habilitationService.canCancelBankTransfert())
      this.tableAction.push({
        actionIcon: 'delete', actionName: 'Supprimer ce virement', actionClick: (action: SortTableAction, element: any) => {
          this.bankTransfertService.cancelBankTransfert(element).subscribe(response => this.searchTransferts());
        }, display: true,
      } as SortTableAction);
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
      this.bankTransfertSearchResultService.getTransferts(this.transfertSearch).subscribe(response => {
        this.transfers = response;
      })
    }
  }

  exportTransferts() {
    this.bankTransfertSearchResultService.exportTransferts(this.transfertSearch);
  }
}
