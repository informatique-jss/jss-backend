import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { UserNoteService } from 'src/app/services/user.notes.service';
import { AttachmentType } from '../../../model/AttachmentType';
import { AttachmentTypeService } from '../../../services/attachment.type.service';
import { GenericSelectComponent } from '../generic-select/generic-select.component';

@Component({
  selector: 'select-attachment-type',
  templateUrl: './select-attachment-type.component.html',
  styleUrls: ['./select-attachment-type.component.css']
})
export class SelectAttachmentTypeComponent extends GenericSelectComponent<AttachmentType> implements OnInit {

  types: AttachmentType[] = [] as Array<AttachmentType>;

  constructor(private formBuild: UntypedFormBuilder, private attachmentTypeService: AttachmentTypeService, private userNoteService2: UserNoteService,) {
    super(formBuild, userNoteService2)
  }

  initTypes(): void {
    this.attachmentTypeService.getAttachmentTypes().subscribe(response => {
      this.types = response.filter(attachmentType => !attachmentType.isHiddenFromUser).sort((a, b) => a.label.localeCompare(b.label));
    })
  }
}
