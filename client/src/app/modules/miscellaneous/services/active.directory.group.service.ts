import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { ActiveDirectoryGroup } from '../../miscellaneous/model/ActiveDirectoryGroup';
import { Employee } from '../../profile/model/Employee';

@Injectable({
  providedIn: 'root'
})
export class ActiveDirectoryGroupService extends AppRestService<ActiveDirectoryGroup> {

  constructor(http: HttpClient) {
    super(http, "miscellaneous");
  }

  getActiveDirectoryGroups() {
    return this.getList(new HttpParams(), "active-directory-groups");
  }

  addOrUpdateActiveDirectoryGroup(activeDirectoryGroup: ActiveDirectoryGroup) {
    return this.addOrUpdate(new HttpParams(), "active-directory-group", activeDirectoryGroup, "Enregistré", "Erreur lors de l'enregistrement");
  }

  isEmployeeInGroupList(employee: Employee, groups: ActiveDirectoryGroup[]) {
    if (groups && employee.adPath)
      for (let group of groups)
        if (employee.adPath.indexOf(group.activeDirectoryPath) >= 0)
          return true;
    return false;
  }

}
