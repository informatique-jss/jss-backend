import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { Department } from '../../../model/Department';
import { DepartmentService } from '../../../services/department.service';
import { GenericMultipleSelectComponent } from '../generic-select/generic-multiple-select.component';

@Component({
  selector: 'select-departments',
  templateUrl: './select-departments.component.html',
  styleUrls: ['./select-departments.component.css']
})
export class SelectDepartmentsComponent extends GenericMultipleSelectComponent<Department> implements OnInit {

  types: Department[] = [] as Array<Department>;

  constructor(private changeDetectorRef: ChangeDetectorRef,
    private formBuild: UntypedFormBuilder,
    private departmentService: DepartmentService) {
    super(changeDetectorRef, formBuild);
  }

  initTypes(): void {
    this.departmentService.getDepartments().subscribe(response => {
      this.types = response;
    })
  }
}
