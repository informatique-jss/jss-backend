import { Component, Input, OnInit, SimpleChanges, ViewChild } from '@angular/core';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { CustomErrorStateMatcher } from 'src/app/app.component';
import { Dictionnary } from 'src/app/libs/Dictionnary';
import { Audit } from 'src/app/modules/miscellaneous/model/Audit';
import { EntityType } from 'src/app/routing/search/EntityType';
import { HistoryAction } from '../../model/HistoryAction';
import { AuditService } from '../../services/audit.service';

@Component({
  selector: 'history',
  templateUrl: './history.component.html',
  styleUrls: ['./history.component.css']
})
export class HistoryComponent implements OnInit {


  matcher: CustomErrorStateMatcher = new CustomErrorStateMatcher();
  @Input() entity: any = {};
  @Input() entityType: EntityType = {} as EntityType;
  @ViewChild(MatSort) sort!: MatSort;
  @Input() displayOnlyFields: string[] | null = null;
  @Input() historyActions: HistoryAction[] = [] as Array<HistoryAction>;
  internalHistoryActions: HistoryAction[] = [] as Array<HistoryAction>;

  audits: Audit[] = [] as Array<Audit>;

  displayedColumns: string[] = ['createdBy', 'creationDate', 'fieldName', 'oldValue', 'newValue'];

  auditDataSource: MatTableDataSource<Audit> = new MatTableDataSource<Audit>();

  filterValue: string = "";

  dictionnary = new Map<string, string>(Object.entries(Dictionnary));

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
    if (this.internalHistoryActions.length > 0)
      this.displayedColumns.push("actions");
  }

  historyActionTrigger(historyAction: HistoryAction, element: Audit) {
    historyAction.actionClick(element);
  }

  setData() {
    this.auditService.getAuditForEntity(this.entity.id, this.entityType).subscribe(response => {
      if (this.displayOnlyFields != null && this.displayOnlyFields != undefined && response) {
        response.forEach(audit => {
          if (this.displayOnlyFields!.indexOf(audit.fieldName) >= 0)
            this.audits.push(audit);
        })
      } else {
        this.audits = response;
      }
      if (this.audits != undefined && this.audits != null && this.audits.length > 0) {
        this.audits.sort(function (a: Audit, b: Audit) {
          return new Date(b.datetime).getTime() - new Date(a.datetime).getTime();
        });
      }

      this.auditDataSource = new MatTableDataSource(this.audits);
      setTimeout(() => {
        this.auditDataSource.sort = this.sort;
        this.auditDataSource.sortingDataAccessor = (item: Audit, property) => {
          switch (property) {
            case 'fieldName': return item.fieldName;
            case 'oldValue': return item.oldValue;
            case 'newValue': return item.newValue;
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

  applyFilter(filterValue: any) {
    let filterValueCast = (filterValue as HTMLInputElement);
    filterValue = filterValueCast.value.trim();
    filterValue = filterValue.toLowerCase();
    this.auditDataSource.filter = filterValue;
  }

  getFieldLabel(fieldName: string) {
    if (this.dictionnary.get(fieldName))
      return this.dictionnary.get(fieldName);
    return fieldName;
  }
}
