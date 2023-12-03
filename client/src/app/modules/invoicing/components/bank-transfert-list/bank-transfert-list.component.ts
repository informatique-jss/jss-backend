import { AfterContentChecked, ChangeDetectorRef, Component, Input, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute } from '@angular/router';
import { formatDateTimeForSortTable, formatEurosForSortTable, toIsoString } from 'src/app/libs/FormatHelper';
import { EditCommentDialogComponent } from 'src/app/modules/miscellaneous/components/edit-comment-dialog.component/edit-comment-dialog-component.component';
import { SortTableAction } from 'src/app/modules/miscellaneous/model/SortTableAction';
import { SortTableColumn } from 'src/app/modules/miscellaneous/model/SortTableColumn';
import { AppService } from 'src/app/services/app.service';
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
    public editCommentDialog: MatDialog,
    private activatedRoute: ActivatedRoute,
    private appService: AppService,
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
    this.availableColumns.push({ id: "comment", fieldName: "comment", label: "Commentaire" } as SortTableColumn);


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

    this.tableAction.push({
      actionIcon: 'mode_comment', actionName: 'Modifier le commentaire', actionClick: (action: SortTableAction, element: any) => {
        let dialogRef = this.editCommentDialog.open(EditCommentDialogComponent);
        dialogRef.componentInstance.comment = element.comment;

        dialogRef.afterClosed().subscribe(newComment => {
          if (newComment) {
            this.bankTransfertService.addOrUpdateTransfertComment(newComment, element.id).subscribe(response => { this.searchTransferts() });
          }
        });
      }, display: true,
    } as SortTableAction);

    let idBankTransfert = this.activatedRoute.snapshot.params.id;
    if (idBankTransfert) {
      this.transfertSearch.idBankTransfert = idBankTransfert;
      this.transfertSearch.isHideExportedBankTransfert = false;
      this.appService.changeHeaderTitle("Virements");
      this.searchTransferts();
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
      this.bankTransfertSearchResultService.getTransferts(this.transfertSearch).subscribe(response => {
        this.transfers = response;
      })
    }
  }

  exportTransferts() {
    this.bankTransfertSearchResultService.exportTransferts(this.transfertSearch);
  }
}
