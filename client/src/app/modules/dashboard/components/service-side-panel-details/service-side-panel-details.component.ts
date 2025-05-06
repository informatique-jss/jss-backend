import { Component, Input, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { SERVICE_FIELD_TYPE_DATE, SERVICE_FIELD_TYPE_INTEGER, SERVICE_FIELD_TYPE_SELECT, SERVICE_FIELD_TYPE_TEXT, SERVICE_FIELD_TYPE_TEXTAREA } from 'src/app/libs/Constants';
import { Attachment } from 'src/app/modules/miscellaneous/model/Attachment';
import { IAttachment } from 'src/app/modules/miscellaneous/model/IAttachment';
import { Service } from 'src/app/modules/quotation/model/Service';

@Component({
  selector: 'service-side-panel-details',
  templateUrl: './service-side-panel-details.component.html',
  styleUrls: ['./service-side-panel-details.component.css']
})
export class ServiceSidePanelDetailsComponent implements OnInit {

  @Input() service: Service | undefined;

  serviceAttachments: IAttachment = { id: 1, attachments: [] as Attachment[] } as IAttachment;

  SERVICE_FIELD_TYPE_TEXT = SERVICE_FIELD_TYPE_TEXT;
  SERVICE_FIELD_TYPE_INTEGER = SERVICE_FIELD_TYPE_INTEGER;
  SERVICE_FIELD_TYPE_DATE = SERVICE_FIELD_TYPE_DATE;
  SERVICE_FIELD_TYPE_TEXTAREA = SERVICE_FIELD_TYPE_TEXTAREA;
  SERVICE_FIELD_TYPE_SELECT = SERVICE_FIELD_TYPE_SELECT;

  constructor(private formBuilder: FormBuilder) { }

  serviceForm = this.formBuilder.group({});

  ngOnInit() {
    this.serviceAttachments = { id: 1, attachments: [] as Attachment[] } as IAttachment;
    if (this.service)
      if (this.service.assoServiceDocuments)
        for (let doc of this.service.assoServiceDocuments)
          this.serviceAttachments.attachments.push(...doc.attachments);
  }

}
