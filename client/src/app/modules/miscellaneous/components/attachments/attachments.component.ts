import { Component, Input, OnInit, SimpleChanges } from '@angular/core';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { formatBytes, formatDateTimeForSortTable } from 'src/app/libs/FormatHelper';
import { CUSTOMER_ORDER_ENTITY_TYPE, PROVISION_ENTITY_TYPE, QUOTATION_ENTITY_TYPE } from 'src/app/routing/search/search.component';
import { Attachment } from '../../model/Attachment';
import { AttachmentType } from '../../model/AttachmentType';
import { IAttachment } from '../../model/IAttachment';
import { SortTableAction } from '../../model/SortTableAction';
import { SortTableColumn } from '../../model/SortTableColumn';
import { ConstantService } from '../../services/constant.service';
import { UploadAttachmentService } from '../../services/upload.attachment.service';
import { UploadAttachementDialogComponent } from '../upload-attachement-dialog/upload-attachement-dialog.component';

@Component({
  selector: 'attachments',
  templateUrl: './attachments.component.html',
  styleUrls: ['./attachments.component.css']
})
export class AttachmentsComponent implements OnInit {


  @Input() entity: IAttachment = {} as IAttachment;
  @Input() entityType: string = "";
  @Input() editMode: boolean = false;

  displayedColumns: SortTableColumn<Attachment>[] = [];
  tableActions: SortTableAction<Attachment>[] = [] as Array<SortTableAction<Attachment>>;
  searchText: string | undefined;

  filterValue: string = "";

  uploadAttachementDialogRef: MatDialogRef<UploadAttachementDialogComponent> | undefined;

  attachmentTypesToHide: AttachmentType[] = [this.constantService.getAttachmentTypeAutomaticMail()];

  filteredAttachments: Attachment[] = [];

  constructor(
    protected uploadAttachementDialog: MatDialog,
    public confirmationDialog: MatDialog,
    protected uploadAttachmentService: UploadAttachmentService,
    private constantService: ConstantService,
  ) { }

  ngOnChanges(changes: SimpleChanges) {
    if (changes.entity != undefined && this.entity.attachments != undefined) {
      this.setDataTable();
    }
  }

  ngOnInit() {
    this.displayedColumns = [];
    this.displayedColumns.push({ id: "name", fieldName: "uploadedFile.filename", label: "Nom" } as SortTableColumn<Attachment>);
    this.displayedColumns.push({ id: "attachementType", fieldName: "attachmentType.label", label: "Type de document" } as SortTableColumn<Attachment>);
    this.displayedColumns.push({ id: "typeDocument", fieldName: "typeDocument.label", label: "Type INPI" } as SortTableColumn<Attachment>);
    this.displayedColumns.push({ id: "createdBy", fieldName: "uploadedFile.createdBy", label: "Ajouté par" } as SortTableColumn<Attachment>);
    this.displayedColumns.push({ id: "creationDate", fieldName: "uploadedFile.creationDate", label: "Ajouté le", valueFonction: formatDateTimeForSortTable } as SortTableColumn<Attachment>);
    this.displayedColumns.push({ id: "size", fieldName: "uploadedFile.size", label: "Taille", valueFonction: (element: Attachment, column: SortTableColumn<Attachment>) => { return element.uploadedFile.size ? formatBytes(element.uploadedFile.size, 2) : "" } } as SortTableColumn<Attachment>);

    if (this.entityType && (this.entityType == CUSTOMER_ORDER_ENTITY_TYPE.entityType || this.entityType == QUOTATION_ENTITY_TYPE.entityType || this.entityType == PROVISION_ENTITY_TYPE.entityType))
      this.displayedColumns.push({ id: "isAlreadySent", fieldName: "isAlreadySent", label: "Envoyé au client ?", valueFonction: (element: Attachment, column: SortTableColumn<Attachment>) => { return element.isAlreadySent ? "Oui" : "Non" } } as SortTableColumn<Attachment>);

    this.tableActions.push({
      actionIcon: "visibility", actionName: "Prévisualiser le fichier", actionClick: (column: SortTableAction<Attachment>, element: Attachment, event: any): void => {
        this.uploadAttachmentService.previewAttachment(element);
      }, display: true
    } as SortTableAction<Attachment>);
    this.tableActions.push({
      actionIcon: "download", actionName: "Télécharger le fichier", actionClick: (column: SortTableAction<Attachment>, element: Attachment, event: any): void => {
        this.uploadAttachmentService.downloadAttachment(element);
      }, display: true
    } as SortTableAction<Attachment>);
    this.tableActions.push({
      actionIcon: "delete", actionName: "Supprimer le fichier", actionClick: (column: SortTableAction<Attachment>, element: Attachment, event: any): void => {
        this.uploadAttachmentService.disableAttachment(element).subscribe(response => {
          element.isDisabled = true;
          this.setDataTable();
        })
      }, display: true
    } as SortTableAction<Attachment>);
  }

  formatDateTimeForSortTable = formatDateTimeForSortTable;

  downloadAllFiles() {
    if (this.filteredAttachments)
      for (let attachment of this.filteredAttachments)
        this.uploadAttachmentService.downloadAttachment(attachment);
  }

  setDataTable() {
    this.entity.attachments.sort(function (a: Attachment, b: Attachment) {
      return new Date(b.uploadedFile.creationDate).getTime() - new Date(a.uploadedFile.creationDate).getTime();
    });

    this.filteredAttachments = [];
    if (this.entity && this.entity.attachments)
      for (let attachment of this.entity.attachments)
        if (this.attachmentTypesToHide)
          for (let toHideType of this.attachmentTypesToHide)
            if (toHideType.id != attachment.attachmentType.id && !attachment.isDisabled)
              this.filteredAttachments.push(attachment);
  }

  applyFilter(filterValue: any) {
    let filterValueCast = (filterValue as HTMLInputElement);
    filterValue = filterValueCast.value.trim();
    this.searchText = filterValue.toLowerCase();
  }

  uploadFile() {
    this.uploadAttachementDialogRef = this.uploadAttachementDialog.open(UploadAttachementDialogComponent, {
    });
    this.uploadAttachementDialogRef.componentInstance.entity = this.entity;
    this.uploadAttachementDialogRef.componentInstance.entityType = this.entityType;
    this.uploadAttachementDialogRef.afterClosed().subscribe(response => {
      if (response && response != null) {
        this.entity.attachments = response;
        this.setDataTable();
      }
    });
  }
}


