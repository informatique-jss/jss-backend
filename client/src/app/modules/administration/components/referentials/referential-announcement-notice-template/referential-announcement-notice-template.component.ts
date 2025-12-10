import { Component, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { ChangeEvent } from '@ckeditor/ckeditor5-angular';
import { Alignment, Bold, ClassicEditor, Clipboard, Essentials, Font, GeneralHtmlSupport, Indent, IndentBlock, Italic, Link, List, Mention, Paragraph, PasteFromOffice, RemoveFormat, Underline, Undo } from 'ckeditor5';
import { Observable } from 'rxjs';
import { AnnouncementNoticeTemplate } from 'src/app/modules/quotation/model/AnnouncementNoticeTemplate';
import { AnnouncementNoticeTemplateFragment } from 'src/app/modules/quotation/model/AnnouncementNoticeTemplateFragment';
import { AssoAnnouncementNoticeTemplateAnnouncementFragment } from 'src/app/modules/quotation/model/AssoAnnouncementNoticeTemplateAnnouncementFragment';
import { AnnouncementNoticeTemplateService } from 'src/app/modules/quotation/services/announcement.notice.template.service';
import { AssoNoticeTemplateAnnouncementFragmentService } from 'src/app/modules/quotation/services/asso.notice.template.announcement.fragment.service';
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
    private appService2: AppService,
    private assoNoticeTemplateFragmentService: AssoNoticeTemplateAnnouncementFragmentService,
  ) {
    super(formBuilder2, appService2);
  }

  initialNoticeValue: string = '';

  assosNoticeTemplateFragments: AssoAnnouncementNoticeTemplateAnnouncementFragment[] = [];

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
    if (this.selectedEntity && !this.selectedEntity.announcementNoticeTemplateFragments)
      this.selectedEntity.announcementNoticeTemplateFragments = [];
  }

  selectEntity(element: AnnouncementNoticeTemplate) {
    this.selectedEntity = element;
    if (element && element.id)
      this.assoNoticeTemplateFragmentService.getAssoAnnouncementNoticeTemplateFragmentByNoticeTemplate(element.id).subscribe(res => {
        this.assosNoticeTemplateFragments = res;
      });
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

  addFragment() {
    if (this.selectedEntity) {
      if (!this.selectedEntity.announcementNoticeTemplateFragments)
        this.selectedEntity.announcementNoticeTemplateFragments = [];
      this.selectedEntity.announcementNoticeTemplateFragments.push({} as AnnouncementNoticeTemplateFragment);
    }
  }

  deleteFragment(fragment: AnnouncementNoticeTemplateFragment) {
    if (this.selectedEntity) {
      if (this.selectedEntity.announcementNoticeTemplateFragments && this.selectedEntity.announcementNoticeTemplateFragments.indexOf(fragment) >= 0) {
        this.selectedEntity.announcementNoticeTemplateFragments.splice(this.selectedEntity.announcementNoticeTemplateFragments.indexOf(fragment), 1);
      }
    }
  }

  override saveEntity(): void {
    super.saveEntity();
    if (this.getFormStatus()) {

      this.assoNoticeTemplateFragmentService.saveAssoAnnouncementNoticeTemplateFragment(this.assosNoticeTemplateFragments).subscribe(response => {
        this.setDataTable();

      });
    } else {
      this.appService2.displaySnackBar("Erreur, certains champs des fragments ne sont pas correctement renseignés !", true, 15);
    }
  }
}
