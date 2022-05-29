import { Component, Input, OnInit, SimpleChanges, ViewChild } from '@angular/core';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { CustomErrorStateMatcher } from 'src/app/app.component';
import { Audit } from 'src/app/modules/miscellaneous/model/Audit';
import { EntityType } from 'src/app/routing/search/EntityType';
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

  audits: Audit[] = [] as Array<Audit>;

  displayedColumns: string[] = ['createdBy', 'creationDate', 'fieldName', 'oldValue', 'newValue'];

  auditDataSource: MatTableDataSource<Audit> = new MatTableDataSource<Audit>();

  filterValue: string = "";

  constructor(
    protected auditService: AuditService
  ) { }

  ngOnChanges(changes: SimpleChanges) {
    if (changes.entity != undefined && this.entity.id != undefined) {
      this.setData();
    }
  }

  ngOnInit() {
  }

  setData() {
    this.auditService.getAuditForEntity(this.entity.id, this.entityType).subscribe(response => {
      this.audits = response;
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
            case 'createdBy': return item.author.firstname + item.author.lastname;
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
}
