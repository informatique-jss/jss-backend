import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from '../../services/appRest.service';
import { PublishingDepartment } from '../model/PublishingDepartment';

@Injectable({
  providedIn: 'root'
})
export class DepartmentService extends AppRestService<PublishingDepartment> {

  constructor(http: HttpClient) {
    super(http, "wordpress");
  }

  getAvailablePublishingDepartments() {
    return this.getListCached(new HttpParams(), "publishing-departments");
  }

}
