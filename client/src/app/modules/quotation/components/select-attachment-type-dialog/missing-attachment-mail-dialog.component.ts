import { Component, OnInit, SimpleChanges, ViewChild } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { Subject } from 'rxjs';
import { SERVICE_FIELD_TYPE_DATE, SERVICE_FIELD_TYPE_INTEGER, SERVICE_FIELD_TYPE_SELECT, SERVICE_FIELD_TYPE_TEXT, SERVICE_FIELD_TYPE_TEXTAREA } from 'src/app/libs/Constants';
import { formatBytes } from 'src/app/libs/FormatHelper';
import { MultipleUploadComponent } from 'src/app/modules/miscellaneous/components/multiple-upload/multiple-upload.component';
import { IAttachment } from 'src/app/modules/miscellaneous/model/IAttachment';
import { SortTableColumn } from 'src/app/modules/miscellaneous/model/SortTableColumn';
import { ConstantService } from 'src/app/modules/miscellaneous/services/constant.service';
import { UploadAttachmentService } from 'src/app/modules/miscellaneous/services/upload.attachment.service';
import { MISSING_ATTACHMENT_QUERY_ENTITY_TYPE } from '../../../../routing/search/search.component';
import { AssoServiceDocument } from '../../model/AssoServiceDocument';
import { AssoServiceFieldType } from '../../model/AssoServiceFieldType';
import { MissingAttachmentQuery } from '../../model/MissingAttachmentQuery';
import { Service } from '../../model/Service';
import { MissingAttachmentQueryService } from '../../services/missing-attachment-query.service';
import { ServiceService } from '../../services/service.service';
import { SelectAttachmentsDialogComponent } from '../select-attachments-dialog/select-attachment-dialog.component';

@Component({
  selector: 'app-select-attachment-type-dialog',
  templateUrl: './missing-attachment-mail-dialog.component.html',
  styleUrls: ['./missing-attachment-mail-dialog.component.css']
})
export class MissingAttachmentMailDialogComponent implements OnInit {
  entity: IAttachment = { id: undefined } as IAttachment;
  service: Service | undefined;
  displayedColumns: SortTableColumn<AssoServiceDocument>[] = [];
  displayedFieldTypes: SortTableColumn<AssoServiceFieldType>[] = [];
  selectedAssoServiceDocument: AssoServiceDocument[] = [];
  selectedAssoServiceFieldType: AssoServiceFieldType[] = [];
  tableAssoServiceDocuments: AssoServiceDocument[] = [];
  tableAssoServiceFieldTypes: AssoServiceFieldType[] = [];
  missingAttachmentQuery: MissingAttachmentQuery = {} as MissingAttachmentQuery;
  editMode: boolean = true;
  isWaitingForAttachmentToUpload: boolean = false;
  SERVICE_FIELD_TYPE_TEXT = SERVICE_FIELD_TYPE_TEXT;
  SERVICE_FIELD_TYPE_INTEGER = SERVICE_FIELD_TYPE_INTEGER;
  SERVICE_FIELD_TYPE_DATE = SERVICE_FIELD_TYPE_DATE;
  SERVICE_FIELD_TYPE_TEXTAREA = SERVICE_FIELD_TYPE_TEXTAREA;
  SERVICE_FIELD_TYPE_SELECT = SERVICE_FIELD_TYPE_SELECT;
  MISSING_ATTACHMENT_QUERY_ENTITY_TYPE = MISSING_ATTACHMENT_QUERY_ENTITY_TYPE;

  @ViewChild(MultipleUploadComponent) multipleUploadComponent: MultipleUploadComponent | undefined;
  formatBytes = formatBytes;

  constructor(private formBuilder: FormBuilder,
    public confirmationDialog: MatDialog,
    private uploadAttachmentService: UploadAttachmentService,
    public dialogRef: MatDialogRef<SelectAttachmentsDialogComponent>,
    private missingAttachmentQueryService: MissingAttachmentQueryService,
    private serviceService: ServiceService,
    private constantService: ConstantService,
  ) { }

  refreshTable: Subject<void> = new Subject<void>();
  getAttachmentTypeAutomaticMail = this.constantService.getAttachmentTypeAutomaticMail;

  getServiceLabel(service: Service) {
    return this.serviceService.getServiceLabel(service, false, this.constantService.getServiceTypeOther());
  }

  ngOnInit() {
    this.displayedColumns = [];
    this.displayedColumns.push({ id: "typeDocument", fieldName: "typeDocument.customLabel", label: "Type de document" } as SortTableColumn<AssoServiceDocument>);
    this.displayedColumns.push({ id: "typeAttachment", fieldName: "typeDocument.attachmentType.label", label: "Type de PJ" } as SortTableColumn<AssoServiceDocument>);
    this.displayedColumns.push({ id: "isMandatory", fieldName: "isMandatory", label: "Obligatoire ?", valueFonction: (element: AssoServiceDocument, column: SortTableColumn<AssoServiceDocument>) => { return element.isMandatory ? "Oui" : "Non" } } as SortTableColumn<AssoServiceDocument>);
    this.displayedColumns.push({ id: "existingFiles", fieldName: "existingFiles", label: "Fichiers", valueFonction: (element: AssoServiceDocument, column: SortTableColumn<AssoServiceDocument>) => { return element.attachments ? element.attachments.map(attachment => attachment.uploadedFile.filename).join(" / ") : "" } } as SortTableColumn<AssoServiceDocument>);

    this.displayedFieldTypes.push({ id: "serviceFieldTypeLabel", fieldName: "serviceFieldType.label", label: "Type de champ" } as SortTableColumn<AssoServiceFieldType>);
    this.displayedFieldTypes.push({ id: "serviceFieldTypeDataType", fieldName: "serviceFieldType.dataType", label: "Type de donnée" } as SortTableColumn<AssoServiceFieldType>);
    this.displayedFieldTypes.push({ id: "isMandatory", fieldName: "isMandatory", label: "Obligatoire ?", valueFonction: (element: AssoServiceFieldType, column: SortTableColumn<AssoServiceFieldType>) => { return element.isMandatory ? "Oui" : "Non" } } as SortTableColumn<AssoServiceFieldType>);
    this.setTableValues();
  }
  ngOnChanges(changes: SimpleChanges) {
    if (changes.service)
      this.setTableValues();
  }

  setTableValues() {
    // load last one
    if (this.service && this.selectedAssoServiceDocument.length == 0 && this.service.missingAttachmentQueries && this.service.missingAttachmentQueries.length > 0) {
      this.service.missingAttachmentQueries.sort((a: MissingAttachmentQuery, b: MissingAttachmentQuery) => {
        return new Date(b.createdDateTime!).getTime() - new Date(a.createdDateTime!).getTime();
      })
      this.missingAttachmentQuery = this.service.missingAttachmentQueries[0];
      if (this.editMode)
        this.missingAttachmentQuery.id = undefined;
      this.selectedAssoServiceDocument = this.missingAttachmentQuery.assoServiceDocument;
      this.selectedAssoServiceFieldType = this.missingAttachmentQuery.assoServiceFieldType;
      this.missingAttachmentQuery.attachments = [];
    }

    // display specific one
    if (this.missingAttachmentQuery && this.missingAttachmentQuery.id) {
      this.selectedAssoServiceDocument = this.missingAttachmentQuery.assoServiceDocument;
      this.selectedAssoServiceFieldType = this.missingAttachmentQuery.assoServiceFieldType;
      if (this.editMode)
        this.missingAttachmentQuery.id = undefined;
    }

    this.tableAssoServiceFieldTypes = this.getAssoServiceFieldType();
    this.tableAssoServiceDocuments = this.getAssoServiceDocument();
    this.refreshTable.next();
  }

  attachmentTypeForm = this.formBuilder.group({
  });

  selectAssoServiceDocument(assoServiceDocument: AssoServiceDocument) {
    if (!this.editMode)
      return;

    if (this.selectedAssoServiceDocument)
      for (let att of this.selectedAssoServiceDocument)
        if (att.id == assoServiceDocument.id)
          return;
    this.selectedAssoServiceDocument.push(assoServiceDocument);
    this.setTableValues();
  }

  selectAssoServiceFieldType(assoServiceFieldType: AssoServiceFieldType) {
    if (!this.editMode)
      return;

    if (this.selectedAssoServiceFieldType)
      for (let att of this.selectedAssoServiceFieldType)
        if (att.id == assoServiceFieldType.id)
          return;
    this.selectedAssoServiceFieldType.push(assoServiceFieldType);
    this.setTableValues();
  }

  deleteAssoServiceDocument(index: number) {
    this.selectedAssoServiceDocument.splice(index, 1);
    this.setTableValues();
  }

  deleteAssoServiceFieldType(index: number) {
    this.selectedAssoServiceFieldType.splice(index, 1);
    this.setTableValues();
  }

  getAssoServiceDocument() {
    if (this.service) {
      let assos = [];
      if (!this.selectedAssoServiceDocument)
        return this.service.assoServiceDocuments;
      else if (this.service.assoServiceDocuments) {
        for (let serviceAsso of this.service.assoServiceDocuments) {
          let found = false;
          for (let selectedAsso of this.selectedAssoServiceDocument) {
            if (selectedAsso.typeDocument.code == serviceAsso.typeDocument.code) {
              found = true;
              break;
            }
          }
          if (!found)
            assos.push(serviceAsso);
        }
        return assos;
      }
    }
    return [];
  }

  getAssoServiceFieldType() {
    if (this.service) {
      let assos = [];
      if (!this.selectedAssoServiceFieldType)
        return this.service.assoServiceFieldTypes;
      else if (this.service.assoServiceFieldTypes) {
        for (let serviceAssoFieldType of this.service.assoServiceFieldTypes) {
          let found = false;
          for (let selectedAssoFieldType of this.selectedAssoServiceFieldType) {
            if (selectedAssoFieldType.serviceFieldType.code == serviceAssoFieldType.serviceFieldType.code) {
              found = true;
              break;
            }
          }
          if (!found)
            assos.push(serviceAssoFieldType);
        }
        return assos;
      }
    }
    return [];
  }

  getFormStatus(): boolean {
    return this.attachmentTypeForm.valid;
  }

  generateMail() {
    if (this.attachmentTypeForm.valid && (this.selectedAssoServiceDocument.length > 0 || this.selectedAssoServiceFieldType.length > 0) && this.editMode) {
      if (this.selectedAssoServiceDocument.length > 0)
        this.missingAttachmentQuery.assoServiceDocument = this.selectedAssoServiceDocument;
      if (this.selectedAssoServiceFieldType.length > 0)
        this.missingAttachmentQuery.assoServiceFieldType = this.selectedAssoServiceFieldType;
      if (this.multipleUploadComponent && this.multipleUploadComponent.files && this.multipleUploadComponent.files.length > 0)
        this.isWaitingForAttachmentToUpload = true;
      this.dialogRef.close(this.missingAttachmentQueryService.generateMissingAttachmentMail(this.missingAttachmentQuery, this.isWaitingForAttachmentToUpload).subscribe(response => {
        if (response && this.multipleUploadComponent && this.multipleUploadComponent.files && this.multipleUploadComponent.files.length > 0) {
          this.missingAttachmentQuery = response;
          this.entity.id = response.id;
          this.multipleUploadComponent.uploadFiles();
          this.missingAttachmentQueryService.sendMissingAttachmentQueryWithUploadedFiles(this.missingAttachmentQuery).subscribe();
        }
        if (response)
          this.closeDialog();
      }));
    }
  }

  closeDialog() {
    this.dialogRef.close(null);
  }

  sendMailQuery($event: any) {
    // if ($event)
    //  this.missingAttachmentQueryService.sendMissingAttachmentQueryImmediatly(this.missingAttachmentQuery).subscribe();
  }
}
