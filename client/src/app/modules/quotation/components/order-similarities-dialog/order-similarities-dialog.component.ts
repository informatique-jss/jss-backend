import { Component, OnInit } from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';
import { formatDateForSortTable, formatEurosForSortTable } from 'src/app/libs/FormatHelper';
import { SortTableColumn } from 'src/app/modules/miscellaneous/model/SortTableColumn';
import { Affaire } from '../../model/Affaire';
import { CustomerOrderStatus } from '../../model/CustomerOrderStatus';
import { OrderingSearch } from '../../model/OrderingSearch';
import { OrderingSearchResult } from '../../model/OrderingSearchResult';
import { CustomerOrderStatusService } from '../../services/customer.order.status.service';
import { AddAffaireDialogComponent } from '../add-affaire-dialog/add-affaire-dialog.component';

@Component({
  selector: 'order-similarities-dialog',
  templateUrl: './order-similarities-dialog.component.html',
  styleUrls: ['./order-similarities-dialog.component.css']
})
export class OrderSimilaritiesDialogComponent implements OnInit {

  displayedColumns: SortTableColumn[] = [];
  orders: OrderingSearchResult[] | undefined;
  affaire: Affaire | undefined;
  customerOrderStatusList: CustomerOrderStatus[] = [] as Array<CustomerOrderStatus>;
  orderingSearch: OrderingSearch | undefined;
  loaded: boolean = false;

  constructor(
    private dialogRef: MatDialogRef<AddAffaireDialogComponent>,
    private customerOrderStatusService: CustomerOrderStatusService,
  ) { }

  ngOnInit() {
    this.displayedColumns = [];
    this.displayedColumns.push({ id: "id", fieldName: "customerOrderId", label: "N° de la commande" } as SortTableColumn);
    this.displayedColumns.push({ id: "createdDate", fieldName: "createdDate", label: "Date de création", valueFonction: formatDateForSortTable } as SortTableColumn);
    this.displayedColumns.push({ id: "affaireLabel", fieldName: "affaireLabel", label: "Affaire(s)", isShrinkColumn: false } as SortTableColumn);
    this.displayedColumns.push({ id: "customerOrderStatus", fieldName: "customerOrderStatus", label: "Statut" } as SortTableColumn);
    this.displayedColumns.push({ id: "customerOrderDescription", fieldName: "customerOrderDescription", label: "Description", isShrinkColumn: true } as SortTableColumn);
    this.displayedColumns.push({ id: "tiersLabel", fieldName: "tiersLabel", label: "Tiers", actionLinkFunction: this.getColumnLink, actionIcon: "visibility", actionTooltip: "Voir la fiche du tiers" } as SortTableColumn);
    this.displayedColumns.push({ id: "customerOrderLabel", fieldName: "customerOrderLabel", label: "Donneur d'ordre", actionLinkFunction: this.getColumnLink, actionIcon: "visibility", actionTooltip: "Voir la fiche du donneur d'ordre" } as SortTableColumn);
    this.displayedColumns.push({ id: "totalPrice", fieldName: "totalPrice", label: "Prix TTC", valueFonction: formatEurosForSortTable } as SortTableColumn);
  }

  getColumnLink(column: SortTableColumn, element: any) {
    if (element && column.id == "tiersLabel") {
      return ['/tiers', element.tiersId];
    }
    if (element && column.id == "customerOrderLabel") {
      if (element.responsableId)
        return ['/tiers/responsable', element.responsableId];
      if (element.tiersId)
        return ['/tiers', element.tiersId];
      if (element.confrereId)
        return ['/confrere', element.confrereId];
    }
    return ['/tiers'];
  }

  closeDialog() {
    this.dialogRef.close(null);
  }

  accept() {
    this.dialogRef.close(true);
  }

}
