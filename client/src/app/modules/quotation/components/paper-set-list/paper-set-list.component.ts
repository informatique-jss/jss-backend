import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { ConfirmDialogComponent } from 'src/app/modules/miscellaneous/components/confirm-dialog/confirm-dialog.component';
import { SortTableAction } from 'src/app/modules/miscellaneous/model/SortTableAction';
import { SortTableColumn } from 'src/app/modules/miscellaneous/model/SortTableColumn';
import { AppService } from 'src/app/services/app.service';
import { PaperSetResult } from '../../model/PaperSetResult';
import { PaperSetResultService } from '../../services/paper.set.result.service';
import { PaperSetService } from '../../services/paper.set.service';

@Component({
  selector: 'app-paper-set-list',
  templateUrl: './paper-set-list.component.html',
  styleUrls: ['./paper-set-list.component.css']
})
export class PaperSetListComponent implements OnInit {

  textSearch: string = "";
  filteredPaperSetResults: PaperSetResult[] | undefined;
  paperSetResults: PaperSetResult[] | undefined;
  displayedColumns: SortTableColumn<PaperSetResult>[] = [];
  tableAction: SortTableAction<PaperSetResult>[] = [];

  constructor(
    private appService: AppService,
    public confirmationDialog: MatDialog,
    private formBuilder: FormBuilder,
    private paperSetResultService: PaperSetResultService,
    private paperSetService: PaperSetService
  ) { }

  ngOnInit() {
    this.appService.changeHeaderTitle("Gestion des documents")
    this.displayedColumns = [];
    this.displayedColumns.push({ id: "locationNumber", fieldName: "locationNumber", label: "Emplacement" } as SortTableColumn<PaperSetResult>);
    this.displayedColumns.push({ id: "paperSetTypeLabel", fieldName: "paperSetTypeLabel", label: "Action à réaliser" } as SortTableColumn<PaperSetResult>);
    this.displayedColumns.push({ id: "customerOrderId", fieldName: "customerOrderId", label: "N° commande" } as SortTableColumn<PaperSetResult>);
    this.displayedColumns.push({ id: "customerOrderStatus", fieldName: "customerOrderStatus", label: "Statut commande" } as SortTableColumn<PaperSetResult>);
    this.displayedColumns.push({ id: "tiersLabel", fieldName: "tiersLabel", label: "Tiers" } as SortTableColumn<PaperSetResult>);
    this.displayedColumns.push({ id: "responsableLabel", fieldName: "responsableLabel", label: "Responsable" } as SortTableColumn<PaperSetResult>);
    this.displayedColumns.push({ id: "affaireLabel", fieldName: "affaireLabel", label: "Affaire(s)" } as SortTableColumn<PaperSetResult>);
    this.displayedColumns.push({ id: "servicesLabel", fieldName: "servicesLabel", label: "Service(s)" } as SortTableColumn<PaperSetResult>);

    this.tableAction.push({
      actionIcon: "shopping_cart", actionName: "Voir la commande", actionLinkFunction: (action: SortTableAction<PaperSetResult>, element: PaperSetResult) => {
        if (element && element.customerOrderId)
          return ['/order', element.customerOrderId];
        return undefined;
      }, display: true,
    } as SortTableAction<PaperSetResult>);

    this.tableAction.push({
      actionIcon: "group", actionName: "Voir le tiers", actionLinkFunction: (action: SortTableAction<PaperSetResult>, element: any) => {
        if (element)
          return ['/tiers', element.tiersId];
        return undefined;
      }, display: true,
    } as SortTableAction<PaperSetResult>);
    this.tableAction.push({
      actionIcon: "group", actionName: "Voir le responsable", actionLinkFunction: (action: SortTableAction<PaperSetResult>, element: any) => {
        if (element)
          return ['/tiers/responsable', element.responsableId];
        return undefined;
      }, display: true,
    } as SortTableAction<PaperSetResult>);

    this.tableAction.push({
      actionIcon: "delete", actionName: "Supprimer cette action", actionClick: (action: SortTableAction<PaperSetResult>, element: PaperSetResult, event: any) => {
        if (element) {
          const dialogRef = this.confirmationDialog.open(ConfirmDialogComponent, {
            maxWidth: "400px",
            data: {
              title: "Supprimer l'action",
              content: "Êtes-vous sûr de vouloir supprimer cette action et de libérer l'emplacement associé ?",
              closeActionText: "Annuler",
              validationActionText: "Supprimer"
            }
          });

          dialogRef.afterClosed().subscribe(dialogResult => {
            if (dialogResult) {
              this.paperSetService.cancelPaperSet(element.id).subscribe(response => this.searchPaperSets());
            }
          });
        }
        return undefined;
      }, display: true,
    } as SortTableAction<PaperSetResult>);

    this.searchPaperSets();
  }

  paperSetForm = this.formBuilder.group({
  });

  searchPaperSets() {
    this.paperSetResultService.searchPaperSets().subscribe(response => {
      this.paperSetResults = response;
      this.filterPaperSets();
    }
    );
  }

  filterPaperSets() {
    if (this.paperSetResults && this.textSearch && this.textSearch.trim().length > 0) {
      this.filteredPaperSetResults = this.paperSetResults.filter(result => JSON.stringify(result).toLocaleLowerCase().indexOf(this.textSearch.toLocaleLowerCase()) >= 0);
    } else {
      this.filteredPaperSetResults = this.paperSetResults;
    }
  }
}
