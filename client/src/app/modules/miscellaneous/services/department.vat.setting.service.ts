import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { DepartmentVatSetting } from '../../miscellaneous/model/DepartmentVatSetting';

@Injectable({
  providedIn: 'root'
})
export class DepartmentVatSettingService extends AppRestService<DepartmentVatSetting>{

  constructor(http: HttpClient) {
    super(http, "miscellaneous");
  }

  getDepartmentVatSettings() {
    return this.getList(new HttpParams(), "department-vat-settings");
  }
  
   addOrUpdateDepartmentVatSetting(departmentVatSetting: DepartmentVatSetting) {
    return this.addOrUpdate(new HttpParams(), "department-vat-setting", departmentVatSetting, "Enregistré", "Erreur lors de l'enregistrement");
  }

}
