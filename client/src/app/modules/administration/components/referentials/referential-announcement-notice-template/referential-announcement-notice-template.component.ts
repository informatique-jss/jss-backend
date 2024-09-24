import { Component, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { ChangeEvent } from '@ckeditor/ckeditor5-angular';
import { Alignment, Bold, ClassicEditor, Clipboard, Essentials, Font, GeneralHtmlSupport, Indent, IndentBlock, Italic, Link, List, Mention, Paragraph, RemoveFormat, Underline, Undo } from 'ckeditor5';
import { Observable } from 'rxjs';
import { AnnouncementNoticeTemplate } from 'src/app/modules/quotation/model/AnnouncementNoticeTemplate';
import { AnnouncementNoticeTemplateService } from 'src/app/modules/quotation/services/announcement.notice.template.service';
import { AppService } from 'src/app/services/app.service';
import { GenericReferentialComponent } from '../generic-referential/generic-referential-component';

@Component({
  selector: 'referential-announcement-notice-template',
  templateUrl: 'referential-announcement-notice-template.component.html',
  styleUrls: ['referential-announcement-notice-template.component.css']
})
export class ReferentialAnnouncementNoticeTemplateComponent extends GenericReferentialComponent<AnnouncementNoticeTemplate> implements OnInit {
  constructor(private announcementNoticeTemplateService: AnnouncementNoticeTemplateService,
    private formBuilder2: FormBuilder,
    private appService2: AppService,) {
    super(formBuilder2, appService2);
  }

  initialNoticeValue: string = '';

  entityForm2 = this.formBuilder2.group({
    notice: ['', Validators.required]
  })

  ngOnInit() {
    this.setDataTable();
    if (this.saveEvent)
      this.saveEventSubscription = this.saveEvent.subscribe(() => this.saveEntity());
    if (this.addEvent)
      this.addEventSubscription = this.addEvent.subscribe(() => this.addEntity());
    if (this.cloneEvent)
      this.cloneEventSubscription = this.cloneEvent.subscribe(() => this.cloneEntity());
  }

  selectEntity(element: AnnouncementNoticeTemplate) {
    this.selectedEntity = element;
    this.initialNoticeValue = this.selectedEntity.text
    this.selectedEntityChange.emit(this.selectedEntity);
  }

  getAddOrUpdateObservable(): Observable<AnnouncementNoticeTemplate> {
    return this.announcementNoticeTemplateService.addOrUpdateAnnouncementNoticeTemplate(this.selectedEntity!);
  }
  getGetObservable(): Observable<AnnouncementNoticeTemplate[]> {
    return this.announcementNoticeTemplateService.getAnnouncementNoticeTemplates();
  }

  ckEditorNotice = ClassicEditor;
  config = {
    toolbar: ['undo', 'redo', '|', 'fontFamily', 'fontSize', 'bold', 'italic', 'underline', 'fontColor', 'fontBackgroundColor', '|',
      'alignment:left', 'alignment:right', 'alignment:center', 'alignment:justify', '|', 'link', 'bulletedList', 'numberedList', 'outdent', 'indent', 'removeformat'
    ],
    plugins: [
      Bold, Essentials, Italic, Mention, Paragraph, Undo, Font, Alignment, Link, List, Indent, IndentBlock, RemoveFormat, Clipboard, GeneralHtmlSupport, Underline
    ],
    htmlSupport: {
      allow: [
        {
          name: /.*/,
          attributes: true,
          classes: true,
          styles: true
        }
      ]
    }
  } as any;

  onNoticeChange(event: ChangeEvent) {
    if (this.selectedEntity)
      this.selectedEntity.text = event.editor.getData();
  }
}
