import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { AppService } from 'src/app/services/app.service';
import { Attachment } from '../../../model/Attachment';
import { UploadAttachmentService } from '../../../services/upload.attachment.service';
import { GenericSelectComponent } from '../generic-select/generic-select.component';

@Component({
  selector: 'select-attachment',
  templateUrl: './select-attachment.component.html',
  styleUrls: ['./select-attachment.component.css']
})
export class SelectAttachmentComponent extends GenericSelectComponent<Attachment> implements OnInit {

  @Input() types: Attachment[] = [] as Array<Attachment>;
  @Output() onUploadFile: EventEmitter<Attachment> = new EventEmitter();

  constructor(private formBuild: UntypedFormBuilder, private appService3: AppService, private uploadAttachmentService: UploadAttachmentService) {
    super(formBuild, appService3)
  }

  initTypes(): void {
  }

  previewFile(tiersAttachment: Attachment) {
    this.uploadAttachmentService.previewAttachment(tiersAttachment);
  }

  downloadFile(tiersAttachment: Attachment) {
    this.uploadAttachmentService.downloadAttachment(tiersAttachment);
  }

  uploadFile() {
    if (this.model)
      this.onUploadFile.emit(this.model);
  }

  displayLabel(object: any): string {
    if (object && object.uploadedFile && object.uploadedFile.filename)
      return object.uploadedFile.filename;
    return "";
  }
}
