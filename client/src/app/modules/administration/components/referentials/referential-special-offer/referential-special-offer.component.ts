import { Component, EventEmitter, Input, OnInit, Output, ViewChild } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatAccordion } from '@angular/material/expansion';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { Observable, Subscription } from 'rxjs';
import { AssoSpecialOfferBillingType } from 'src/app/modules/miscellaneous/model/AssoSpecialOfferBillingType';
import { SpecialOffer } from 'src/app/modules/miscellaneous/model/SpecialOffer';
import { SpecialOfferService } from 'src/app/modules/miscellaneous/services/special.offer.service';

@Component({
  selector: 'referential-special-offer',
  templateUrl: 'referential-special-offer.component.html',
  styleUrls: ['referential-special-offer.component.css']
})
export class ReferentialSpecialOfferComponent implements OnInit {

  selectedEntity: SpecialOffer | undefined;
  @Input() editMode: boolean = false;
  @Input() saveEvent: Observable<void> | undefined;
  saveEventSubscription: Subscription | undefined;
  @Input() addEvent: Observable<void> | undefined;
  addEventSubscription: Subscription | undefined;
  @Input() cloneEvent: Observable<void> | undefined;
  cloneEventSubscription: Subscription | undefined;
  @Output() selectedEntityChange: EventEmitter<SpecialOffer> = new EventEmitter<SpecialOffer>();
  entities: SpecialOffer[] = [] as Array<SpecialOffer>;
  displayedColumns: string[] = ['id', 'code', 'label'];
  @ViewChild(MatSort) sort!: MatSort;
  @ViewChild(MatAccordion) accordion: MatAccordion | undefined;

  assoSpecialOfferBillingItems: AssoSpecialOfferBillingType[] | undefined;

  entityDataSource: MatTableDataSource<SpecialOffer> = new MatTableDataSource<SpecialOffer>();

  constructor(
    private formBuilder: FormBuilder,
    private specialOfferService: SpecialOfferService,
  ) { }

  entityForm = this.formBuilder.group({
  });

  cloneEntity() {
    this.selectedEntity = structuredClone(this.selectedEntity);
    this.selectedEntity!.id = undefined;
    this.entities.push(this.selectedEntity!);
    this.setDataTable();
  }

  ngOnInit() {
    this.setDataTable();
    if (this.saveEvent)
      this.saveEventSubscription = this.saveEvent.subscribe(() => this.saveEntity());
    if (this.addEvent)
      this.addEventSubscription = this.addEvent.subscribe(() => this.addEntity());
    if (this.cloneEvent)
      this.cloneEventSubscription = this.cloneEvent.subscribe(() => this.cloneEntity());
  }

  ngOnDestroy() {
    if (this.saveEventSubscription)
      this.saveEventSubscription.unsubscribe();
    if (this.addEventSubscription)
      this.addEventSubscription.unsubscribe();
    if (this.cloneEventSubscription)
      this.cloneEventSubscription.unsubscribe();
  }

  selectEntity(element: SpecialOffer) {
    this.selectedEntity = element;
    this.setBillingItems();
    this.selectedEntityChange.emit(this.selectedEntity);
  }

  getElementCode(element: SpecialOffer) {
    return element.code;
  }

  getElementLabel(element: SpecialOffer) {
    return element.label;
  }

  saveEntity() {
    if (this.selectedEntity) {
      this.selectedEntity.assoSpecialOfferBillingTypes = [] as Array<AssoSpecialOfferBillingType>;
      if (this.assoSpecialOfferBillingItems)
        this.selectedEntity.assoSpecialOfferBillingTypes.push(...this.assoSpecialOfferBillingItems);
      this.specialOfferService.addOrUpdateSpecialOffer(this.selectedEntity).subscribe(response => {
        this.setDataTable();
      });
    }
  }

  addEntity() {
    this.selectedEntity = {} as SpecialOffer;
    this.setBillingItems();
  }

  setDataTable() {
    this.specialOfferService.getSpecialOffers().subscribe(response => {
      this.entities = response;
      this.entityDataSource = new MatTableDataSource(this.entities);
      setTimeout(() => {
        this.entityDataSource.sort = this.sort;
        this.entityDataSource.sortingDataAccessor = (item: SpecialOffer, property) => {
          switch (property) {
            case 'id': return item.id!;
            case 'code': return this.getElementCode(item);
            case 'label': return this.getElementLabel(item);
            default: return this.getElementLabel(item);
          }
        };

        this.entityDataSource.filterPredicate = (data: any, filter) => {
          const dataStr = JSON.stringify(data).toLowerCase();
          return dataStr.indexOf(filter) != -1;
        }
      });
    })
  }

  setBillingItems() {
    this.assoSpecialOfferBillingItems = [] as Array<AssoSpecialOfferBillingType>;
    if (this.selectedEntity && this.selectedEntity.assoSpecialOfferBillingTypes)
      this.assoSpecialOfferBillingItems?.push(...this.selectedEntity.assoSpecialOfferBillingTypes);
  }

  addAsso() {
    if (this.selectedEntity) {
      if (this.assoSpecialOfferBillingItems == undefined)
        this.assoSpecialOfferBillingItems = [] as Array<AssoSpecialOfferBillingType>;
      this.assoSpecialOfferBillingItems.push({} as AssoSpecialOfferBillingType);
    }
  }

  applyFilter(filterValue: any) {
    let filterValueCast = (filterValue as HTMLInputElement);
    filterValue = filterValueCast.value.trim();
    filterValue = filterValue.toLowerCase();
    this.entityDataSource.filter = filterValue;
  }
}
