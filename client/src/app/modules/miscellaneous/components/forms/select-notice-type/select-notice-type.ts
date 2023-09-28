import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { UserNoteService } from 'src/app/services/user.notes.service';
import { NoticeType } from 'src/app/modules/quotation/model/NoticeType';
import { NoticeTypeService } from '../../../../quotation/services/notice.type.service';
import { GenericSelectComponent } from '../generic-select/generic-select.component';


@Component({
  selector: 'select-notice-type',
  templateUrl: './../generic-select/generic-select.component.html',
  styleUrls: ['./../generic-select/generic-select.component.css']
})
  export class SelectNoticeTypeComponent extends GenericSelectComponent<NoticeType> implements OnInit {

  types: NoticeType[] = [] as Array<NoticeType>;

  constructor(private formBuild: UntypedFormBuilder, private noticeTypeService: NoticeTypeService, private userNoteService2: UserNoteService) {
    super(formBuild, userNoteService2);
  }

  ngOnInit(){
    this.initTypes();
  }

  initTypes(): void {
    this.noticeTypeService.getNoticeTypes().subscribe(response => {
      this.types = response.sort((a, b) => a.label.localeCompare(b.label));
    });
  }
  displayLabel(object: NoticeType): string {
    if (object)
      return object.label;
    return "";
  }
}
