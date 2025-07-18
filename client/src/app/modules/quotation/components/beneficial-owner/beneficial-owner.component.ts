import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { Subject } from 'rxjs';
import { formatDateForSortTable } from 'src/app/libs/FormatHelper';
import { SortTableAction } from 'src/app/modules/miscellaneous/model/SortTableAction';
import { SortTableColumn } from 'src/app/modules/miscellaneous/model/SortTableColumn';
import { Affaire } from '../../model/Affaire';
import { BeneficialOwner } from '../../model/beneficial-owner/BeneficialOwner';
import { BeneficialOwnerService } from '../../services/beneficial.owner.service';

@Component({
  selector: 'beneficial-owner',
  templateUrl: './beneficial-owner.component.html',
  styleUrls: ['./beneficial-owner.component.css']
})
export class BeneficialOwnerComponent implements OnInit {

  @Input() editMode: boolean = false;
  @Input() affaire: Affaire | undefined;
  displayedColumns: SortTableColumn<BeneficialOwner>[] = [] as Array<SortTableColumn<BeneficialOwner>>;
  beneficialOwners: BeneficialOwner[] = [] as Array<BeneficialOwner>;
  tableActions: SortTableAction<BeneficialOwner>[] = [] as Array<SortTableAction<BeneficialOwner>>;
  refreshBeneficialOwnersTable: Subject<void> = new Subject<void>();
  index: number = 0;
  @Output() editBeneficialOwner = new EventEmitter<BeneficialOwner>();

  constructor(private beneficiaOwnerService: BeneficialOwnerService) { }

  ngOnInit() {
    if (this.affaire)
      this.beneficiaOwnerService.getBeneficialOwners(this.affaire).subscribe(response => { this.beneficialOwners = response });
    this.displayedColumns = [];
    this.displayedColumns.push({ id: "id", fieldName: "id", label: "identifiant BE" } as SortTableColumn<BeneficialOwner>);
    this.displayedColumns.push({ id: "usedName", fieldName: "usedName", label: "Nom d'usage" } as SortTableColumn<BeneficialOwner>);
    this.displayedColumns.push({ id: "firstNames", fieldName: "firstNames", label: "Prénoms" } as SortTableColumn<BeneficialOwner>);
    this.displayedColumns.push({ id: "creationDate", fieldName: "creationDate", label: "Date de création", valueFonction: formatDateForSortTable } as SortTableColumn<BeneficialOwner>);

    this.tableActions.push({
      actionIcon: 'delete', actionName: "Supprimer le BE", actionClick: (column: SortTableAction<BeneficialOwner>, element: BeneficialOwner, event: any) => {
        if (this.editMode && element)
          this.beneficiaOwnerService.deleteBeneficialOwner(element).subscribe(response => {
            if (this.affaire)
              this.beneficiaOwnerService.getBeneficialOwners(this.affaire).subscribe(response => { this.beneficialOwners = response });
          });

      }, display: true,
    } as SortTableAction<BeneficialOwner>);

    this.tableActions.push({
      actionIcon: 'edit',
      actionName: "Modifier le BE",
      actionClick: (column, element, event) => {
        if (this.editMode && element) {
          this.editBeneficialOwner.emit(element);
        }
      },
      display: true,
    } as SortTableAction<BeneficialOwner>);

  }

}
