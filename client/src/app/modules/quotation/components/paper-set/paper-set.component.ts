import { Component, Input, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { Subject } from 'rxjs';
import { ConfirmDialogComponent } from 'src/app/modules/miscellaneous/components/confirm-dialog/confirm-dialog.component';
import { EditCommentDialogComponent } from 'src/app/modules/miscellaneous/components/edit-comment-dialog.component/edit-comment-dialog-component.component';
import { SortTableAction } from 'src/app/modules/miscellaneous/model/SortTableAction';
import { SortTableColumn } from 'src/app/modules/miscellaneous/model/SortTableColumn';
import { AppService } from 'src/app/services/app.service';
import { CustomerOrder } from '../../model/CustomerOrder';
import { PaperSet } from '../../model/PaperSet';
import { PaperSetService } from '../../services/paper.set.service';
import { SelectPaperSetTypeDialogComponent } from '../select-paper-set-type-dialog/select-paper-set-type-dialog.component';

@Component({
  selector: 'paper-set',
  templateUrl: './paper-set.component.html',
  styleUrls: ['./paper-set.component.css']
})
export class PaperSetComponent implements OnInit {

  @Input() customerOrder: CustomerOrder | undefined;
  displayedColumns: SortTableColumn<PaperSet>[] = [];
  tableAction: SortTableAction<PaperSet>[] = [];
  refreshPaperSetTable: Subject<void> = new Subject<void>();
  displayedPaperSets: PaperSet[] | undefined;

  constructor(
    public selectPaperSetTypeDialogComponent: MatDialog,
    private paperSetService: PaperSetService,
    private confirmationDialog: MatDialog,
    private appService: AppService,
  ) { }

  ngOnInit() {
    this.displayedColumns = [];
    this.displayedColumns.push({ id: "paperSetType", fieldName: "paperSetType.label", label: "Action à réaliser" } as SortTableColumn<PaperSet>);
    this.displayedColumns.push({ id: "locationNumber", fieldName: "locationNumber", label: "Emplacement" } as SortTableColumn<PaperSet>);
    this.displayedColumns.push({ id: "creationComment", fieldName: "creationComment", label: "Commentaire" } as SortTableColumn<PaperSet>);
    this.displayedColumns.push({ id: "validationComment", fieldName: "validationComment", label: "Commentaire de validation/annulation" } as SortTableColumn<PaperSet>);
    this.displayedColumns.push({ id: "isCancelled", fieldName: "isCancelled", label: "Annulé ?", valueFonction: (element: PaperSet | PaperSet) => { if (element.isCancelled) return "Oui"; return "Non"; } } as unknown as SortTableColumn<PaperSet | PaperSet>);
    this.displayedColumns.push({ id: "isValidated", fieldName: "isValidated", label: "Validé ?", valueFonction: (element: PaperSet | PaperSet) => { if (element.isValidated) return "Oui"; return "Non"; } } as unknown as SortTableColumn<PaperSet | PaperSet>);


    this.tableAction.push({
      actionIcon: "check", actionName: "Valider cette action", actionClick: (action: SortTableAction<PaperSet>, element: PaperSet, event: any) => {
        if (element) {

          const dialogRef = this.confirmationDialog.open(ConfirmDialogComponent, {
            maxWidth: "400px",
            data: {
              title: "Valider l'action",
              content: "Êtes-vous sûr de vouloir valider cette action et de libérer l'emplacement associé ?",
              closeActionText: "Annuler",
              validationActionText: "Confirmer"
            }
          });

          dialogRef.afterClosed().subscribe(dialogResult => {
            if (dialogResult) {
              const dialogRef = this.confirmationDialog.open(EditCommentDialogComponent, {
                width: '40%',
                data: {
                  title: "Nouveau commentaire",
                }
              });
              dialogRef.afterClosed().subscribe(dialogResultComment => {
                this.paperSetService.validatePaperSet(element.id, dialogResultComment).subscribe(response => this.refreshPaperSets());
              });
            }
          });
        }
        return undefined;
      }, display: true,
    } as SortTableAction<PaperSet>);

    this.tableAction.push({
      actionIcon: "cancel", actionName: "Annuler cette action", actionClick: (action: SortTableAction<PaperSet>, element: PaperSet, event: any) => {
        if (element) {
          const dialogRef = this.confirmationDialog.open(ConfirmDialogComponent, {
            maxWidth: "400px",
            data: {
              title: "Annuler l'action",
              content: "Êtes-vous sûr de vouloir annuler cette action et de libérer l'emplacement associé ?",
              closeActionText: "Annuler",
              validationActionText: "Confirmer"
            }
          });

          dialogRef.afterClosed().subscribe(dialogResult => {
            if (dialogResult) {
              const dialogRef = this.confirmationDialog.open(EditCommentDialogComponent, {
                width: '40%',
                data: {
                  title: "Nouveau commentaire",
                }
              });
              dialogRef.afterClosed().subscribe(dialogResult => {
                this.paperSetService.cancelPaperSet(element.id, dialogResult).subscribe(response => this.refreshPaperSets());
              });
            }
          });
        }
        return undefined;
      }, display: true,
    } as SortTableAction<PaperSet>);

    this.refreshPaperSets();
  }

  refreshPaperSets() {
    if (this.customerOrder && this.customerOrder.paperSets && this.customerOrder.paperSets.length > 0) {
      this.displayedPaperSets = this.customerOrder.paperSets;
      this.refreshPaperSetTable.next();
    }
  }

  addPaperSetType() {
    if (this.customerOrder) {
      let dialogRef = this.selectPaperSetTypeDialogComponent.open(SelectPaperSetTypeDialogComponent, {
        width: '50%',
      });

      dialogRef.afterClosed().subscribe(dialogResult => {
        if (dialogResult != null && this.customerOrder) {
          let paperSet = {} as PaperSet;
          paperSet.customerOrder = this.customerOrder;
          paperSet.paperSetType = dialogResult.paperSetType;
          paperSet.creationComment = dialogResult.creationComment;
          this.paperSetService.addOrUpdatePaperSet(paperSet).subscribe(newPaperSet => {
            this.refreshPaperSets();
            if (this.customerOrder)
              this.appService.openRoute(null, '/order/' + this.customerOrder.id, null);
          })
        }
      });
    }
  }


}
