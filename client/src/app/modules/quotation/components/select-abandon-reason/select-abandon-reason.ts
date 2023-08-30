import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { UserNoteService } from 'src/app/services/user.notes.service';
import { AbandonReasonService } from '../../../miscellaneous/services/abandon.reason.service';
import { GenericSelectComponent } from '../../../miscellaneous/components/forms/generic-select/generic-select.component';
import { AbandonReason } from '../../../miscellaneous/model/AbandonReason';

@Component({
  selector: 'select-abandon-reason',
  templateUrl: '../../../miscellaneous/components/forms/generic-select/generic-select.component.html',
  styleUrls: ['../../../miscellaneous/components/forms/generic-select/generic-select.component.css']
})
  export class SelectAbandonReasonComponent extends GenericSelectComponent<AbandonReason> implements OnInit {

  types: AbandonReason[] = [] as Array<AbandonReason>;

  constructor(private formBuild: UntypedFormBuilder, private abandonReasonService: AbandonReasonService, private userNoteService2: UserNoteService) {
    super(formBuild, userNoteService2);
  }

  ngOnInit(){
    this.initTypes();
  }

  initTypes(): void {
    this.abandonReasonService.getAbandonReasons().subscribe(response => {
      this.types = response;
    });
  }
}
