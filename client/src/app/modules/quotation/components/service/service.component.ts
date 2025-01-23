import { Component, Input, OnInit, SimpleChanges } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { SERVICE_FIELD_TYPE_DATE, SERVICE_FIELD_TYPE_INTEGER, SERVICE_FIELD_TYPE_SELECT, SERVICE_FIELD_TYPE_TEXT, SERVICE_FIELD_TYPE_TEXTAREA, VALIDATED_BY_CUSTOMER } from 'src/app/libs/Constants';
import { instanceOfQuotation } from 'src/app/libs/TypeHelper';
import { ConfirmDialogComponent } from 'src/app/modules/miscellaneous/components/confirm-dialog/confirm-dialog.component';
import { EditCommentDialogComponent } from 'src/app/modules/miscellaneous/components/edit-comment-dialog.component/edit-comment-dialog-component.component';
import { Attachment } from 'src/app/modules/miscellaneous/model/Attachment';
import { ConstantService } from 'src/app/modules/miscellaneous/services/constant.service';
import { UploadAttachmentService } from 'src/app/modules/miscellaneous/services/upload.attachment.service';
import { ASSO_SERVICE_DOCUMENT_ENTITY_TYPE } from 'src/app/routing/search/search.component';
import { AppService } from 'src/app/services/app.service';
import { formatBytes } from '../../../../libs/FormatHelper';
import { AssoServiceDocument } from '../../model/AssoServiceDocument';
import { AssoServiceFieldType } from '../../model/AssoServiceFieldType';
import { IQuotation } from '../../model/IQuotation';
import { Service } from '../../model/Service';
import { ServiceType } from '../../model/ServiceType';
import { TypeDocument } from '../../model/guichet-unique/referentials/TypeDocument';
import { SelectDocumentTypeDialogComponent } from '../select-document-type-dialog/select-document-type-dialog.component';
import { AssoServiceFieldType } from '../../model/AssoServiceFieldType';
import { ServiceFieldType } from '../../model/ServiceFieldType';

@Component({
  selector: 'service',
  templateUrl: './service.component.html',
  styleUrls: ['./service.component.css']
})
export class ServiceComponent implements OnInit {
  [x: string]: any;

  @Input() service: Service | undefined;
  @Input() quotation: IQuotation | undefined;
  @Input() editMode: boolean = false;
  searchText: string = "";
  SERVICE_FIELD_TYPE_TEXT = SERVICE_FIELD_TYPE_TEXT;
  SERVICE_FIELD_TYPE_INTEGER = SERVICE_FIELD_TYPE_INTEGER;
  SERVICE_FIELD_TYPE_DATE = SERVICE_FIELD_TYPE_DATE;
  SERVICE_FIELD_TYPE_TEXTAREA = SERVICE_FIELD_TYPE_TEXTAREA;
  SERVICE_FIELD_TYPE_SELECT = SERVICE_FIELD_TYPE_SELECT;

  constructor(
    private formBuilder: FormBuilder,
    public editCommentDialog: MatDialog,
    public selectedDocumentTypeDialog: MatDialog,
    public confirmationDialog: MatDialog,
    public selectServiceTypeDialog: MatDialog,
    private constantService: ConstantService,
    private appService: AppService,
    private uploadAttachmentService: UploadAttachmentService
  ) { }

  serviceForm = this.formBuilder.group({});
  otherServiceType: ServiceType = this.constantService.getServiceTypeOther();
  ASSO_SERVICE_DOCUMENT_ENTITY_TYPE = ASSO_SERVICE_DOCUMENT_ENTITY_TYPE;
  formatBytes = formatBytes;

  ngOnInit() {
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes.service && this.service) {
      if (this.service.assoServiceDocuments && this.service.assoServiceDocuments.length > 0)
        this.service.assoServiceDocuments.sort((a: AssoServiceDocument, b: AssoServiceDocument) => {
          let aLabel = ((a.isMandatory) ? "0" : "1") + a.typeDocument.customLabel;
          let bLabel = ((b.isMandatory) ? "0" : "1") + b.typeDocument.customLabel;
          return aLabel.localeCompare(bLabel)
        });
    }
  }

  isValidatedStatusForUploadFile() {
    if (this.quotation && instanceOfQuotation(this.quotation) && this.quotation.quotationStatus && this.quotation.quotationStatus.code == VALIDATED_BY_CUSTOMER)
      return false;
    else
      return true;
  }

  commentDocument(document: AssoServiceDocument) {
    let dialogRef = this.editCommentDialog.open(EditCommentDialogComponent, {
      width: "50%",
    });
    dialogRef.componentInstance.comment = document.formalisteComment;
    dialogRef.componentInstance.isMandatory = false;

    dialogRef.afterClosed().subscribe(newComment => {
      if (newComment && newComment.length == 0)
        newComment = null;
      document.formalisteComment = newComment;
    });
  }

  deleteDocument(document: AssoServiceDocument) {
    const dialogRef = this.confirmationDialog.open(ConfirmDialogComponent, {
      maxWidth: "400px",
      data: {
        title: "Supprimer le type de document",
        content: "Êtes-vous sûr de vouloir supprimer le type de document " + document.typeDocument.customLabel + " ? Cela effacera toutes les pièces jointes associées.",
        closeActionText: "Annuler",
        validationActionText: "Supprimer"
      }
    });

    dialogRef.afterClosed().subscribe(dialogResult => {
      if (dialogResult && this.service)
        this.service.assoServiceDocuments.splice(this.service.assoServiceDocuments.indexOf(document), 1);
    });
  }

  completeDocumentAttachment($event: any, document: AssoServiceDocument) {
    document.attachments = $event;
  }

  deleteAttachment(attachment: Attachment) {
    const dialogRef = this.confirmationDialog.open(ConfirmDialogComponent, {
      maxWidth: "400px",
      data: {
        title: "Supprimer le type de document",
        content: "Êtes-vous sûr de vouloir supprimer le fichier " + attachment.uploadedFile.filename + " ?",
        closeActionText: "Annuler",
        validationActionText: "Supprimer"
      }
    });

    dialogRef.afterClosed().subscribe(dialogResult => {
      if (dialogResult && this.service)
        this.uploadAttachmentService.disableAttachment(attachment).subscribe(response => {
          attachment.isDisabled = true;
        })
    });

  }

  previewAttachment(attachment: Attachment) {
    this.uploadAttachmentService.previewAttachment(attachment);
  }

  downloadAttachment(attachment: Attachment) {
    this.uploadAttachmentService.downloadAttachment(attachment);
  }

  downloadAllFiles() {
    if (this.service)
      if (this.service.assoServiceDocuments && this.service.assoServiceDocuments.length > 0)
        for (let assoServiceDocument of this.service.assoServiceDocuments)
          if (assoServiceDocument.attachments)
            for (let attachment of assoServiceDocument.attachments)
              this.uploadAttachmentService.downloadAttachment(attachment);
  }

  addNewDocumentType(service: Service) {
    const dialogRef = this.selectedDocumentTypeDialog.open(SelectDocumentTypeDialogComponent, {
      maxWidth: "400px",
    });

    dialogRef.afterClosed().subscribe((dialogResult: TypeDocument) => {
      if (dialogResult && service) {
        if (!service.assoServiceDocuments)
          service.assoServiceDocuments = [];
        for (let asso of service.assoServiceDocuments) {
          if (asso.typeDocument.code == dialogResult.code) {
            this.appService.displaySnackBar("Type de document déjà présent", true, 10);
            return;
          }
        }
        let asso = {} as AssoServiceDocument;
        asso.isMandatory = false;
        asso.typeDocument = dialogResult;
        service.assoServiceDocuments.push(asso);
      }
    });
  }

  documentContainsSearch(document: AssoServiceDocument) {
    let found = false;
    if (this.searchText && document) {
      found = document.typeDocument.customLabel.toLocaleLowerCase().trim().indexOf(this.searchText.trim().toLocaleLowerCase()) >= 0;
      if (!found && document.attachments && document.attachments.length > 0) {
        for (let attachment of document.attachments)
          if (attachment.uploadedFile.filename.toLocaleLowerCase().trim().indexOf(this.searchText.trim().toLocaleLowerCase()) >= 0) {
            found = true;
            break;
          }
      }
    }
    return found;
  }

  addFurtherInformationField() {
    if (this.service) {
      if (!this.service.assoServiceFieldTypes)
        this.service.assoServiceFieldTypes = [];
      for (let asso of this.service.assoServiceFieldTypes) {
        if (asso.serviceFieldType.code == this.constantService.getFurtherInformationServiceFieldType().code) {
          return;
        }
      }
      let newFurtherInfoAssoServiceFieldType = {} as AssoServiceFieldType;
      newFurtherInfoAssoServiceFieldType.isMandatory = false;
      newFurtherInfoAssoServiceFieldType.serviceFieldType = this.constantService.getFurtherInformationServiceFieldType();;
      this.service.assoServiceFieldTypes.push(newFurtherInfoAssoServiceFieldType);
    }
  }
}
