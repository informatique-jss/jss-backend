import { Directive, EventEmitter, Input, OnInit, Output, ViewChild } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { Observable, Subscription } from 'rxjs';
import { IReferential } from '../../../model/IReferential';

@Directive()
export abstract class GenericReferentialComponent<T extends IReferential> implements OnInit {

  selectedEntity: T | undefined;
  @Input() editMode: boolean = false;
  @Input() saveEvent: Observable<void> | undefined;
  saveEventSubscription: Subscription | undefined;
  @Input() addEvent: Observable<void> | undefined;
  addEventSubscription: Subscription | undefined;
  @Output() selectedEntityChange: EventEmitter<T> = new EventEmitter<T>();
  entities: T[] = [] as Array<T>;
  displayedColumns: string[] = ['id', 'code', 'label'];
  @ViewChild(MatSort) sort!: MatSort;

  entityDataSource: MatTableDataSource<T> = new MatTableDataSource<T>();

  constructor(
    private formBuilder: FormBuilder) { }

  entityForm = this.formBuilder.group({
    departments: [''],
    noticeTypeFamily: [''],
    provisionFamilyType: [''],
    billingType: [''],
    department: [''],
    country: [''],
    billingItems: [''],
    vat: [''],
    accountingAccountClass: [''],
    competentAuthorityType: [''],
    postalCode: [''],
    regions: [''],
    manager: [''],
    accountingAccountProvider: [''],
    accountingAccount: [''],
    accountingAccountCustomer: [''],
    city: [''],
    journalTypes: [''],
    weekDays: [''],
    preference: [''],
    regie: [''],
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

  selectEntity(element: T) {
    this.selectedEntity = element;
    this.selectedEntityChange.emit(this.selectedEntity);
  }

  getElementCode(element: T) {
    return element.code;
  }

  getElementLabel(element: T) {
    return element.label;
  }

  abstract getAddOrUpdateObservable(): Observable<T>;
  abstract getGetObservable(): Observable<T[]>;

  saveEntity() {
    if (this.selectedEntity)
      this.getAddOrUpdateObservable().subscribe(response => {
        this.setDataTable();
      });
  }

  addEntity() {
    this.selectedEntity = {} as T;
  }

  setDataTable() {
    this.getGetObservable().subscribe(response => {
      this.entities = response;
      this.mapEntities();
      this.entityDataSource = new MatTableDataSource(this.entities);
      setTimeout(() => {
        this.entityDataSource.sort = this.sort;
        this.entityDataSource.sortingDataAccessor = (item: T, property) => {
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

  mapEntities() {
    return;
  }

  applyFilter(filterValue: any) {
    let filterValueCast = (filterValue as HTMLInputElement);
    filterValue = filterValueCast.value.trim();
    filterValue = filterValue.toLowerCase();
    this.entityDataSource.filter = filterValue;
  }
}
