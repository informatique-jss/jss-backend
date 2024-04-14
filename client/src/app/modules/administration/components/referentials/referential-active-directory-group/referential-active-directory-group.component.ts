import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import { ActiveDirectoryGroup } from 'src/app/modules/miscellaneous/model/ActiveDirectoryGroup';
import { ActiveDirectoryGroupService } from 'src/app/modules/miscellaneous/services/active.directory.group.service';
import { AppService } from 'src/app/services/app.service';
import { GenericReferentialComponent } from '../generic-referential/generic-referential-component';

@Component({
  selector: 'referential-active-directory-group',
  templateUrl: './referential-active-directory-group.component.html',
  styleUrls: ['./../generic-referential/generic-referential.component.css']
})
export class ReferentialActiveDirectoryGroupComponent extends GenericReferentialComponent<ActiveDirectoryGroup> implements OnInit {
  constructor(private activeDirectoryGroupService: ActiveDirectoryGroupService,
    private formBuilder2: FormBuilder,
    private appService2: AppService,) {
    super(formBuilder2, appService2);
  }

  getAddOrUpdateObservable(): Observable<ActiveDirectoryGroup> {
    return this.activeDirectoryGroupService.addOrUpdateActiveDirectoryGroup(this.selectedEntity!);
  }
  getGetObservable(): Observable<ActiveDirectoryGroup[]> {
    return this.activeDirectoryGroupService.getActiveDirectoryGroups();
  }
}
