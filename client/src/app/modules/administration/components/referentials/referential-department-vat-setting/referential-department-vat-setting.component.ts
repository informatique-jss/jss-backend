import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import { DepartmentVatSetting } from 'src/app/modules/miscellaneous/model/DepartmentVatSetting';
import { DepartmentVatSettingService } from 'src/app/modules/miscellaneous/services/department.vat.setting.service';
import { AppService } from 'src/app/services/app.service';
import { GenericReferentialComponent } from '../generic-referential/generic-referential-component';

@Component({
  selector: 'referential-department-vat-setting',
  templateUrl: './referential-department-vat-setting.component.html',
  styleUrls: ['./../generic-referential/generic-referential.component.css']
})
export class ReferentialDepartmentVatSettingComponent extends GenericReferentialComponent<DepartmentVatSetting> implements OnInit {
  constructor(private departmentVatSettingService: DepartmentVatSettingService,
    private formBuilder2: FormBuilder,
    private appService2: AppService,) {
    super(formBuilder2, appService2);
  }

  getAddOrUpdateObservable(): Observable<DepartmentVatSetting> {
    return this.departmentVatSettingService.addOrUpdateDepartmentVatSetting(this.selectedEntity!);
  }
  getGetObservable(): Observable<DepartmentVatSetting[]> {
    return this.departmentVatSettingService.getDepartmentVatSettings();
  }
}
