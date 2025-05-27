import { Component, Input, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { SHARED_IMPORTS } from '../../../../../libs/SharedImports';
import { Department } from '../../../../profile/model/Department';
import { DepartmentService } from '../../../../quotation/services/department.service';
import { GenericSelectComponent } from '../generic-select/generic-select.component';

@Component({
  selector: 'select-department',
  templateUrl: '../generic-select/generic-select.component.html',
  styleUrls: ['../generic-select/generic-select.component.css'],
  standalone: true,
  imports: [SHARED_IMPORTS]
})
export class SelectDepartmentComponent extends GenericSelectComponent<Department> implements OnInit {

  @Input() types: Department[] = [] as Array<Department>;

  constructor(private formBuild: UntypedFormBuilder,
    private departmentService: DepartmentService) {
    super(formBuild)
  }

  initTypes(): void {
    this.departmentService.getDepartments().subscribe(response => {
      this.types = response;
    })
  }

  override   displayLabel(object: any): string {
    return object ? (object.code + " - " + object.label) : '';
  }
}
