import { HttpClient, HttpParams } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { AppRestService } from "src/app/services/appRest.service";
import { PublishingDepartment } from "../model/PublishingDepartment";

@Injectable({
  providedIn: 'root'
})
export class PublishingDepartmentService extends AppRestService<PublishingDepartment> {

  constructor(http: HttpClient) {
    super(http, "miscellaneous");
  }

  getAvailablePublishingDepartments() {
    return this.getListCached(new HttpParams(), "publishing-departments");
  }

}
