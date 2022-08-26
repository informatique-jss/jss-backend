import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import { Department } from 'src/app/modules/miscellaneous/model/Department';
import { DepartmentService } from 'src/app/modules/miscellaneous/services/department.service';
import { AppService } from 'src/app/services/app.service';
import { GenericReferentialComponent } from '../generic-referential/generic-referential-component';

@Component({
  selector: 'referential-department',
  templateUrl: 'referential-department.component.html',
  styleUrls: ['./../generic-referential/generic-referential.component.css']
})
export class ReferentialDepartmentComponent extends GenericReferentialComponent<Department> implements OnInit {
  constructor(private departmentService: DepartmentService,
    private formBuilder2: FormBuilder,
    private appService2: AppService,) {
    super(formBuilder2, appService2);
  }

  getAddOrUpdateObservable(): Observable<Department> {
    return this.departmentService.addOrUpdateDepartment(this.selectedEntity!);
  }
  getGetObservable(): Observable<Department[]> {
    return this.departmentService.getDepartments();
  }
}
