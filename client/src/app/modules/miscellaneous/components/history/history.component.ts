import { Component, Input, OnInit, SimpleChanges, ViewChild } from '@angular/core';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { Dictionnary } from 'src/app/libs/Dictionnary';
import { formatDateTimeForSortTable } from 'src/app/libs/FormatHelper';
import { IReferential } from 'src/app/modules/administration/model/IReferential';
import { Audit } from 'src/app/modules/miscellaneous/model/Audit';
import { EntityType } from 'src/app/routing/search/EntityType';
import { SortTableAction } from '../../model/SortTableAction';
import { SortTableColumn } from '../../model/SortTableColumn';
import { AuditService } from '../../services/audit.service';

@Component({
  selector: 'history',
  templateUrl: './history.component.html',
  styleUrls: ['./history.component.css']
})
export class HistoryComponent implements OnInit {



  @Input() entity: any = {};
  @Input() entityType: EntityType = {} as EntityType;
  @ViewChild(MatSort) sort!: MatSort;
  @Input() displayOnlyFields: string[] | null = null;
  @Input() historyActions: SortTableAction[] = [] as Array<SortTableAction>;
  internalHistoryActions: SortTableAction[] = [] as Array<SortTableAction>;
  @Input() parseTypeList: IReferential[] | undefined;

  audits: Audit[] = [] as Array<Audit>;

  ddisplayedColumns: string[] = ['', '', '', '', ''];

  auditDataSource: MatTableDataSource<Audit> = new MatTableDataSource<Audit>();

  dictionnary = new Map<string, string>(Object.entries(Dictionnary));

  displayedColumns: SortTableColumn[] = [];
  searchText: string | undefined;

  constructor(
    protected auditService: AuditService
  ) { }

  ngOnChanges(changes: SimpleChanges) {
    if (changes.entity != undefined && this.entity.id != undefined) {
      this.setData();
    }
  }

  ngOnInit() {
    this.internalHistoryActions = this.historyActions;
    this.displayedColumns = [];
    this.displayedColumns.push({ id: "fieldName", fieldName: "fieldName", label: "Champ" } as SortTableColumn);
    this.displayedColumns.push({ id: "oldValue", fieldName: "oldValue", label: "Ancienne valeur" } as SortTableColumn);
    this.displayedColumns.push({ id: "newValue", fieldName: "newValue", label: "Nouvelle valeur" } as SortTableColumn);
    this.displayedColumns.push({ id: "createdBy", fieldName: "username", label: "Auteur" } as SortTableColumn);
    this.displayedColumns.push({ id: "creationDate", fieldName: "datetime", label: "Modifié le", valueFonction: formatDateTimeForSortTable } as SortTableColumn);
  }

  historyActionTrigger(historyAction: SortTableAction, element: Audit) {
    historyAction.actionClick(element);
  }

  setData() {
    this.auditService.getAuditForEntity(this.entity.id, this.entityType).subscribe(response => {
      if (this.displayOnlyFields != null && this.displayOnlyFields != undefined && response) {
        for (let audit of response) {
          if (this.displayOnlyFields!.indexOf(audit.fieldName) >= 0)
            this.audits.push(audit);
        }
      } else {
        this.audits = response;
      }
      if (this.audits != undefined && this.audits != null && this.audits.length > 0) {
        this.audits.sort(function (a: Audit, b: Audit) {
          return new Date(b.datetime).getTime() - new Date(a.datetime).getTime();
        });
      }

      setTimeout(() => {
        this.auditDataSource = new MatTableDataSource(this.audits);
        this.auditDataSource.sort = this.sort;
        this.auditDataSource.sortingDataAccessor = (item: Audit, property) => {
          switch (property) {
            case 'fieldName': return item.fieldName;
            case 'oldValue': return this.parseValues(item.oldValue);
            case 'newValue': return this.parseValues(item.newValue);
            case 'createdBy': return item.username;
            case 'creationDate': return new Date(item.datetime).getTime() + "";
            default: return item.fieldName;
          }
        };

        this.auditDataSource.filterPredicate = (data: any, filter) => {
          const dataStr = JSON.stringify(data).toLowerCase();
          return dataStr.indexOf(filter) != -1;
        }
      });
    })
  }

  parseValues(value: string): string {
    if (this.parseTypeList)
      for (let type of this.parseTypeList)
        if (type.code == value)
          return type.label;

    return value;
  }

  applyFilter(filterValue: any) {
    let filterValueCast = (filterValue as HTMLInputElement);
    filterValue = filterValueCast.value.trim();
    this.searchText = filterValue.toLowerCase();
  }

  getFieldLabel(fieldName: string) {
    if (this.dictionnary.get(fieldName))
      return this.dictionnary.get(fieldName);
    return fieldName;
  }
}
