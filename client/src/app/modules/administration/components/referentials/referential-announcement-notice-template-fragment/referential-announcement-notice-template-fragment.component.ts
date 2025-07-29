import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { ChangeEvent } from '@ckeditor/ckeditor5-angular';
import { Alignment, Bold, ClassicEditor, Clipboard, Essentials, Font, GeneralHtmlSupport, Indent, IndentBlock, Italic, Link, List, Mention, Paragraph, PasteFromOffice, RemoveFormat, Underline, Undo } from 'ckeditor5';
import { Observable } from 'rxjs';
import { AnnouncementNoticeTemplateFragment } from 'src/app/modules/quotation/model/AnnouncementNoticeTemplateFragment';
import { AnnouncementNoticeTemplateFragmentService } from 'src/app/modules/quotation/services/announcement.notice.template.fragment.service';
import { AppService } from 'src/app/services/app.service';
import { GenericReferentialComponent } from '../generic-referential/generic-referential-component';

@Component({
  selector: 'referential-announcement-notice-template-fragment',
  templateUrl: './referential-announcement-notice-template-fragment.component.html',
  styleUrls: ['./../generic-referential/generic-referential.component.css']
})
export class ReferentialAnnouncementNoticeTemplateFragmentComponent extends GenericReferentialComponent<AnnouncementNoticeTemplateFragment> implements OnInit {
  constructor(private announcementNoticeTemplateFragmentService: AnnouncementNoticeTemplateFragmentService,
    private formBuilder2: FormBuilder,
    private appService2: AppService,) {
    super(formBuilder2, appService2);
  }

  initialNoticeValue: string = '';

  getAddOrUpdateObservable(): Observable<AnnouncementNoticeTemplateFragment> {
    return this.announcementNoticeTemplateFragmentService.addOrUpdateAnnouncementNoticeTemplateFragment(this.selectedEntity!);
  }
  getGetObservable(): Observable<AnnouncementNoticeTemplateFragment[]> {
    return this.announcementNoticeTemplateFragmentService.getAnnouncementNoticeTemplateFragments();
  }

  ngOnInit() {
    this.setDataTable();
    if (this.saveEvent)
      this.saveEventSubscription = this.saveEvent.subscribe(() => this.saveEntity());
    if (this.addEvent)
      this.addEventSubscription = this.addEvent.subscribe(() => this.addEntity());
    if (this.cloneEvent)
      this.cloneEventSubscription = this.cloneEvent.subscribe(() => this.cloneEntity());
  }

  selectEntity(element: AnnouncementNoticeTemplateFragment) {
    this.selectedEntity = element;
    this.initialNoticeValue = this.selectedEntity.text
    this.selectedEntityChange.emit(this.selectedEntity);
  }

  ckEditorNotice = ClassicEditor;
  config = {
    toolbar: ['undo', 'redo', '|', 'fontFamily', 'fontSize', 'bold', 'italic', 'underline', 'fontColor', 'fontBackgroundColor', '|',
      'alignment:left', 'alignment:right', 'alignment:center', 'alignment:justify', '|', 'link', 'bulletedList', 'numberedList', 'outdent', 'indent', 'removeformat'
    ],
    plugins: [
      Bold, Essentials, Italic, Mention, Paragraph, Undo, Font, Alignment, Link, List, Indent, IndentBlock, RemoveFormat, Clipboard, GeneralHtmlSupport, Underline, PasteFromOffice
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
