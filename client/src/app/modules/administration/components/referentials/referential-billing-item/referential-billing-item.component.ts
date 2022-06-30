import { Component, EventEmitter, Input, OnInit, Output, ViewChild } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatAccordion } from '@angular/material/expansion';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { Observable, Subscription } from 'rxjs';
import { formatDate } from 'src/app/libs/FormatHelper';
import { AccountingAccount } from 'src/app/modules/accounting/model/AccountingAccount';
import { BillingItem } from 'src/app/modules/miscellaneous/model/BillingItem';
import { BillingItemService } from 'src/app/modules/miscellaneous/services/billing.item.service';

@Component({
  selector: 'referential-billing-item',
  templateUrl: 'referential-billing-item.component.html',
  styleUrls: ['referential-billing-item.component.css']
})
export class ReferentialBillingItemComponent implements OnInit {

  selectedEntity: BillingItem | undefined;
  @Input() editMode: boolean = false;
  @Input() saveEvent: Observable<void> | undefined;
  saveEventSubscription: Subscription | undefined;
  @Input() addEvent: Observable<void> | undefined;
  addEventSubscription: Subscription | undefined;
  @Output() selectedEntityChange: EventEmitter<BillingItem> = new EventEmitter<BillingItem>();
  entities: BillingItem[] = [] as Array<BillingItem>;
  displayedColumns: string[] = ['id', 'code', 'label'];
  @ViewChild(MatSort) sort!: MatSort;
  @ViewChild(MatAccordion) accordion: MatAccordion | undefined;

  accountingAccountCharge: AccountingAccount | undefined;
  accountingAccountProducts: AccountingAccount[] | undefined;

  entityDataSource: MatTableDataSource<BillingItem> = new MatTableDataSource<BillingItem>();

  constructor(
    private formBuilder: FormBuilder,
    private billingItemService: BillingItemService,
  ) { }

  entityForm = this.formBuilder.group({
  });

  ngOnInit() {
    this.setDataTable();
    if (this.saveEvent)
      this.saveEventSubscription = this.saveEvent.subscribe(() => this.saveEntity());
    if (this.addEvent)
      this.addEventSubscription = this.addEvent.subscribe(() => this.addEntity());
  }

  ngOnDestroy() {
    if (this.saveEventSubscription)
      this.saveEventSubscription.unsubscribe();
  }

  selectEntity(element: BillingItem) {
    this.selectedEntity = element;
    if (this.selectedEntity.startDate)
      this.selectedEntity.startDate = new Date(this.selectedEntity.startDate);
    this.setCharge();
    this.setProducts();
    this.selectedEntityChange.emit(this.selectedEntity);
  }

  getElementCode(element: BillingItem) {
    return element.billingType.code;
  }

  getElementLabel(element: BillingItem) {
    return element.billingType.label + " - " + formatDate(new Date(element.startDate));
  }

  saveEntity() {
    if (this.selectedEntity) {
      this.selectedEntity.accountingAccounts = [] as Array<AccountingAccount>;
      if (this.accountingAccountCharge)
        this.selectedEntity.accountingAccounts.push(this.accountingAccountCharge);
      if (this.accountingAccountProducts)
        for (let accountingAccountProduct of this.accountingAccountProducts)
          this.selectedEntity.accountingAccounts.push(accountingAccountProduct);
      console.log(this.selectedEntity);
      this.billingItemService.addOrUpdateBillingItem(this.selectedEntity).subscribe(response => {
        this.setDataTable();
      });
    }
  }

  addEntity() {
    this.selectedEntity = {} as BillingItem;
    this.setCharge();
    this.setProducts();
  }

  setDataTable() {
    this.billingItemService.getBillingItems().subscribe(response => {
      this.entities = response;
      this.entityDataSource = new MatTableDataSource(this.entities);
      setTimeout(() => {
        this.entityDataSource.sort = this.sort;
        this.entityDataSource.sortingDataAccessor = (item: BillingItem, property) => {
          switch (property) {
            case 'id': return item.id;
            case 'code': return this.getElementCode(item);
            case 'label': return this.getElementLabel(item);
            default: return item.id;
          }
        };

        this.entityDataSource.filterPredicate = (data: any, filter) => {
          const dataStr = JSON.stringify(data).toLowerCase();
          return dataStr.indexOf(filter) != -1;
        }
      });
    })
  }

  setCharge() {
    this.accountingAccountCharge = undefined;
    if (this.selectedEntity && this.selectedEntity.accountingAccounts)
      for (let entity of this.selectedEntity.accountingAccounts)
        if (entity && entity.accountingAccountNumber.substring(0, 3) == "606")
          this.accountingAccountCharge = entity;
  }

  setProducts() {
    this.accountingAccountProducts = [] as Array<AccountingAccount>;
    if (this.selectedEntity && this.selectedEntity.accountingAccounts)
      for (let entity of this.selectedEntity.accountingAccounts)
        if (entity && entity.accountingAccountNumber.substring(0, 3) == "706") {
          this.accountingAccountProducts?.push(entity);
        }
  }

  addCharge() {
    if (this.selectedEntity) {
      if (this.accountingAccountCharge == undefined)
        this.accountingAccountCharge = { accountingAccountNumber: "606" } as AccountingAccount;
    }
  }

  addProduct() {
    if (this.selectedEntity) {
      if (this.accountingAccountProducts == undefined)
        this.accountingAccountProducts = [] as Array<AccountingAccount>;
      this.accountingAccountProducts.push({ accountingAccountNumber: "706" } as AccountingAccount);
    }
  }

  applyFilter(filterValue: any) {
    let filterValueCast = (filterValue as HTMLInputElement);
    filterValue = filterValueCast.value.trim();
    filterValue = filterValue.toLowerCase();
    this.entityDataSource.filter = filterValue;
  }
}
