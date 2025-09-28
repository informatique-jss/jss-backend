import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { AppService } from 'src/app/services/app.service';
import { ActiveDirectoryGroup } from '../../../model/ActiveDirectoryGroup';
import { ActiveDirectoryGroupService } from '../../../services/active.directory.group.service';
import { GenericMultipleSelectComponent } from '../generic-select/generic-multiple-select.component';

@Component({
  selector: 'select-active-directory-group-multiple',
  templateUrl: './../generic-select/generic-multiple-select.component.html',
  styleUrls: ['./../generic-select/generic-select.component.html']
})
export class SelectActiveDirectoryGroupMultipleComponent extends GenericMultipleSelectComponent<ActiveDirectoryGroup> implements OnInit {

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
