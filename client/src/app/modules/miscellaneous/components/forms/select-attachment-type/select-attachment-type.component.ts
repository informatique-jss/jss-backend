import { Component, Input, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { AttachmentType } from '../../../model/AttachmentType';
import { AttachmentTypeService } from '../../../services/attachment.type.service';
import { GenericSelectComponent } from '../generic-select/generic-select.component';
import { AppService } from 'src/app/services/app.service';
import { A } from '@angular/cdk/keycodes';

@Component({
  selector: 'select-attachment-type',
  templateUrl: './select-attachment-type.component.html',
  styleUrls: ['./select-attachment-type.component.css']
})
export class SelectAttachmentTypeComponent extends GenericSelectComponent<AttachmentType> implements OnInit {

  types: AttachmentType[] = [] as Array<AttachmentType>;
  @Input() byPassAttachmentHiddenFilter: boolean = false;

  constructor(private formBuild: UntypedFormBuilder, private attachmentTypeService: AttachmentTypeService, private appService3: AppService) {
    super(formBuild, appService3)
  }

  initTypes(): void {
    this.attachmentTypeService.getAttachmentTypes().subscribe(response => {
      if (this.byPassAttachmentHiddenFilter)
        this.types = response.sort((a, b) => a.label.localeCompare(b.label));
      else
        this.types = response.filter(attachmentType => !attachmentType.isHiddenFromUser).sort((a, b) => a.label.localeCompare(b.label));
    })
  }
}
