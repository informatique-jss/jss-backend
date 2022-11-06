import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { StatusService } from 'src/app/modules/miscellaneous/services/guichet-unique/status.service';
import { UserNoteService } from 'src/app/services/user.notes.service';
import { Status } from '../../../../../quotation/model/guichet-unique/referentials/Status';
import { GenericSelectComponent } from '../../generic-select/generic-select.component';

@Component({
  selector: 'select-status',
  templateUrl: '../../generic-select/generic-select.component.html',
  styleUrls: ['../../generic-select/generic-select.component.css']
})
export class SelectStatusComponent extends GenericSelectComponent<Status> implements OnInit {

  types: Status[] = [] as Array<Status>;

  constructor(private formBuild: UntypedFormBuilder, private StatusService: StatusService, private userNoteService2: UserNoteService,) {
    super(formBuild, userNoteService2)
  }

  initTypes(): void {
    this.StatusService.getStatus().subscribe(response => {
      this.types = response;
    })
  }
}
