import { Component, OnInit } from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';
import { Affaire } from '../../model/Affaire';
import { CustomerOrderStatus } from '../../model/CustomerOrderStatus';
import { OrderingSearch } from '../../model/OrderingSearch';
import { OrderingSearchResult } from '../../model/OrderingSearchResult';
import { AddAffaireDialogComponent } from '../add-affaire-dialog/add-affaire-dialog.component';

@Component({
  selector: 'order-similarities-dialog',
  templateUrl: './order-similarities-dialog.component.html',
  styleUrls: ['./order-similarities-dialog.component.css']
})
export class OrderSimilaritiesDialogComponent implements OnInit {

  orders: OrderingSearchResult[] | undefined;
  affaire: Affaire | undefined;
  customerOrderStatusList: CustomerOrderStatus[] = [] as Array<CustomerOrderStatus>;
  orderingSearch: OrderingSearch | undefined;

  constructor(
    private dialogRef: MatDialogRef<AddAffaireDialogComponent>,
  ) { }

  ngOnInit() {
  }

  closeDialog() {
    this.dialogRef.close(null);
  }

  accept() {
    this.dialogRef.close(true);
  }

}
