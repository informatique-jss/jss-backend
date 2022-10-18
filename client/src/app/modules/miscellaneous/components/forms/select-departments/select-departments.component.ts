import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { UserNoteService } from 'src/app/services/user.notes.service';
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

  constructor(private formBuild: UntypedFormBuilder, private departmentService: DepartmentService, private userNoteService2: UserNoteService,) {
    super(formBuild, userNoteService2)
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
