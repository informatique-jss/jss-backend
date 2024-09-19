import { Component, Input, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { Subject } from 'rxjs';
import { SortTableColumn } from 'src/app/modules/miscellaneous/model/SortTableColumn';
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
  refreshPaperSetTable: Subject<void> = new Subject<void>();
  displayedPaperSets: PaperSet[] | undefined;

  constructor(
    public selectPaperSetTypeDialogComponent: MatDialog,
    private paperSetService: PaperSetService,
  ) { }

  ngOnInit() {
    this.displayedColumns = [];
    this.displayedColumns.push({ id: "paperSetType", fieldName: "paperSetType.label", label: "Action à réaliser" } as SortTableColumn<PaperSet>);
    this.displayedColumns.push({ id: "locationNumber", fieldName: "locationNumber", label: "Emplacement" } as SortTableColumn<PaperSet>);
    this.displayedColumns.push({ id: "creationComment", fieldName: "creationComment", label: "Commentaire" } as SortTableColumn<PaperSet>);
    this.displayedColumns.push({ id: "validationComment", fieldName: "validationComment", label: "Commentaire de validation/annulation" } as SortTableColumn<PaperSet>);
    this.displayedColumns.push({ id: "isCancelled", fieldName: "isCancelled", label: "Annulé ?", valueFonction: (element: PaperSet | PaperSet) => { if (element.isCancelled) return "Oui"; return "Non"; } } as unknown as SortTableColumn<PaperSet | PaperSet>);
    this.displayedColumns.push({ id: "isValidated", fieldName: "isValidated", label: "Validé ?", valueFonction: (element: PaperSet | PaperSet) => { if (element.isValidated) return "Oui"; return "Non"; } } as unknown as SortTableColumn<PaperSet | PaperSet>);
    this.displayedColumns.push({ id: "comment", fieldName: "comment", label: "Commentaire" } as SortTableColumn<PaperSet>);
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
      if (this.customerOrder.paperSets)
        dialogRef.componentInstance.excludedPaperSetTypes = this.customerOrder.paperSets.filter(paperSet => !paperSet.isCancelled).map(paperSet => paperSet.paperSetType);

      dialogRef.afterClosed().subscribe(dialogResult => {
        if (dialogResult != null && this.customerOrder) {
          let paperSet = {} as PaperSet;
          paperSet.customerOrder = this.customerOrder;
          paperSet.paperSetType = dialogResult.paperSetType;
          paperSet.creationComment = dialogResult.creationComment;
          this.paperSetService.addOrUpdatePaperSet(paperSet).subscribe(newPaperSet => {
            this.refreshPaperSets();
          })
        }
      });
    }
  }

}
