import { Component, Input, OnInit, SimpleChanges, ViewChild } from '@angular/core';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { CustomErrorStateMatcher } from 'src/app/app.component';
import { ITiers } from '../../model/ITiers';
import { Tiers } from '../../model/Tiers';
import { TiersAttachment } from '../../model/TiersAttachment';
import { UploadTiersAttachmentService } from '../../services/upload.tiers.attachment.service';
import { UploadTiersAttachementDialogComponent } from '../upload-tiers-attachement-dialog/upload-tiers-attachement-dialog.component';

@Component({
  selector: 'tiers-attachments',
  templateUrl: './tiers-attachments.component.html',
  styleUrls: ['./tiers-attachments.component.css']
})
export class TiersAttachmentsComponent implements OnInit {

  matcher: CustomErrorStateMatcher = new CustomErrorStateMatcher();
  @Input() tiers: ITiers = {} as ITiers;
  @Input() editMode: boolean = false;
  @ViewChild(MatSort) sort!: MatSort;

  displayedColumns: string[] = ['name', 'attachementType', 'createdBy', 'creationDate', 'action'];

  tiersAttachmentDataSource: MatTableDataSource<TiersAttachment> = new MatTableDataSource<TiersAttachment>();

  filterValue: string = "";

  uploadTiersAttachementDialogRef: MatDialogRef<UploadTiersAttachementDialogComponent> | undefined;

  constructor(
    protected uploadTiersAttachementDialog: MatDialog,
    protected uploadTiersAttachmentService: UploadTiersAttachmentService
  ) { }

  ngOnChanges(changes: SimpleChanges) {
    if (changes.tiers != undefined && this.tiers.tiersAttachments != undefined) {
      this.setDataTable();
    }
  }

  ngOnInit() {
  }

  setDataTable() {
    this.tiers.tiersAttachments.sort(function (a: TiersAttachment, b: TiersAttachment) {
      return new Date(b.uploadedFile.creationDate).getTime() - new Date(a.uploadedFile.creationDate).getTime();
    });

    this.tiersAttachmentDataSource = new MatTableDataSource(this.tiers.tiersAttachments);
    setTimeout(() => {
      this.tiersAttachmentDataSource.sort = this.sort;
      this.tiersAttachmentDataSource.sortingDataAccessor = (item: TiersAttachment, property) => {
        switch (property) {
          case 'name': return item.uploadedFile.filename;
          case 'attachementType': return item.attachmentType.label;
          case 'createdBy': return item.uploadedFile.createdBy;
          case 'creationDate': return new Date(item.uploadedFile.creationDate).getTime() + "";
          default: return item.uploadedFile.filename;
        }
      };

      this.tiersAttachmentDataSource.filterPredicate = (data: any, filter) => {
        const dataStr = JSON.stringify(data).toLowerCase();
        return dataStr.indexOf(filter) != -1;
      }
    });
  }

  applyFilter(filterValue: any) {
    let filterValueCast = (filterValue as HTMLInputElement);
    filterValue = filterValueCast.value.trim();
    filterValue = filterValue.toLowerCase();
    this.tiersAttachmentDataSource.filter = filterValue;
  }

  uploadFile() {
    this.uploadTiersAttachementDialogRef = this.uploadTiersAttachementDialog.open(UploadTiersAttachementDialogComponent, {
    });
    this.uploadTiersAttachementDialogRef.componentInstance.tiers = this.tiers;
    this.uploadTiersAttachementDialogRef.afterClosed().subscribe(response => {
      if (response && response != null) {
        this.tiers.tiersAttachments = response;
        this.setDataTable();
      }
    });
  }

  previewFile(tiersAttachment: TiersAttachment) {
    this.uploadTiersAttachmentService.previewAttachment(tiersAttachment);
  }

  downloadFile(tiersAttachment: TiersAttachment) {
    this.uploadTiersAttachmentService.downloadAttachment(tiersAttachment);
  }

}


