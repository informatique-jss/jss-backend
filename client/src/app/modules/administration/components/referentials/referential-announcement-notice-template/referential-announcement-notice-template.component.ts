import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { FormBuilder } from '@angular/forms';
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

  @Output() noticeChange: EventEmitter<void> = new EventEmitter<void>();

  ngOnInit() {
    this.entityForm.addControl("text", this.formBuilder2.control({}));
    this.entityForm.get("text")!.valueChanges.subscribe(
      (newValue) => {
        if (newValue != undefined && this.selectedEntity) {
          this.selectedEntity.text = newValue;
        }
      }
    )
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
    this.entityForm.patchValue({
      text: this.selectedEntity.text
    });
    this.selectedEntityChange.emit(this.selectedEntity);
  }

  getAddOrUpdateObservable(): Observable<AnnouncementNoticeTemplate> {
    return this.announcementNoticeTemplateService.addOrUpdateAnnouncementNoticeTemplate(this.selectedEntity!);
  }
  getGetObservable(): Observable<AnnouncementNoticeTemplate[]> {
    return this.announcementNoticeTemplateService.getAnnouncementNoticeTemplates();
  }

  setTextModel(event: any) {
    if (this.selectedEntity)
      this.selectedEntity.text = event.html;
    this.noticeChange.emit();
  }

}
