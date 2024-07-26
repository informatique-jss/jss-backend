import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { Department } from '../../../model/Department';
import { DepartmentService } from '../../../services/department.service';
import { GenericSelectComponent } from '../generic-select/generic-select.component';

@Component({
  selector: 'select-department',
  templateUrl: './select-department.component.html',
  styleUrls: ['./select-department.component.css']
})
export class SelectDepartmentComponent extends GenericSelectComponent<Department> implements OnInit {

  types: Department[] = [] as Array<Department>;

  constructor(private formBuild: UntypedFormBuilder, private departmentService: DepartmentService,) {
    super(formBuild)
  }

  initTypes(): void {
    this.departmentService.getDepartments().subscribe(response => {
      this.types = response;
    })
  }

  displayLabel(object: any): string {
    return object ? (object.code + " - " + object.label) : '';
  }
}
