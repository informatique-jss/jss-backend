import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { UserNoteService } from 'src/app/services/user.notes.service';
import { AbandonReasonService } from '../../../miscellaneous/services/abandon.reason.service';
import { GenericSelectComponent } from '../../../miscellaneous/components/forms/generic-select/generic-select.component';
import { IAbandonReason } from '../../../miscellaneous/model/AbandonReason';

@Component({
  selector: 'select-abandon-reason',
  templateUrl: './select-abandon-reason.html',
  styleUrls: ['./select-abandon-reason.css']
})
  export class SelectAbandonReasonComponent extends GenericSelectComponent<IAbandonReason> implements OnInit {

  types: IAbandonReason[] = [] as Array<IAbandonReason>;

  constructor(private formBuild: UntypedFormBuilder, private abandonReasonService: AbandonReasonService, private userNoteService2: UserNoteService) {
    super(formBuild, userNoteService2);
  }

  initTypes(): void {
    this.abandonReasonService.getAbandonReasons().subscribe(response => {
      this.types = response;
      console.log(this.types);
    });
  }
}
