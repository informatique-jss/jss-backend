import { Component, OnInit } from '@angular/core';
import { ActiveDirectoryGroup } from '../../../model/ActiveDirectoryGroup';
import { GenericSelectComponent } from '../generic-select/generic-select.component';
import { UntypedFormBuilder } from '@angular/forms';
import { ActiveDirectoryGroupService } from '../../../services/active.directory.group.service';
import { AppService } from 'src/app/services/app.service';

@Component({
  selector: 'select-active-directory-group',
  templateUrl: './../select-accounting-journal/select-accounting-journal.component.html',
  styleUrls: ['./../generic-select/generic-select.component.html']
})
export class SelectActiveDirectoryGroupComponent extends GenericSelectComponent<ActiveDirectoryGroup> implements OnInit {

  types: ActiveDirectoryGroup[] = [] as Array<ActiveDirectoryGroup>;
  isDisplayPreviewShortcut: boolean = true;
  constructor(private formBuild: UntypedFormBuilder, private activeDirectoryGroupService: ActiveDirectoryGroupService, private appService3: AppService) {
    super(formBuild, appService3)
  }

  initTypes(): void {
    this.activeDirectoryGroupService.getActiveDirectoryGroups().subscribe(response => {
      this.types = response;
    })
  }

}
