import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { StatusService } from 'src/app/modules/miscellaneous/services/guichet-unique/status.service';
import { Status } from '../../../../../quotation/model/guichet-unique/referentials/Status';
import { GenericSelectComponent } from '../../generic-select/generic-select.component';
import { AppService } from 'src/app/services/app.service';

@Component({
  selector: 'select-status',
  templateUrl: '../../generic-select/generic-select.component.html',
  styleUrls: ['../../generic-select/generic-select.component.css']
})
export class SelectStatusComponent extends GenericSelectComponent<Status> implements OnInit {

  types: Status[] = [] as Array<Status>;

  constructor(private formBuild: UntypedFormBuilder, private StatusService: StatusService, private appService3: AppService) {
    super(formBuild, appService3)
  }

  initTypes(): void {
    this.StatusService.getStatus().subscribe(response => {
      this.types = response;
    })
  }
}
