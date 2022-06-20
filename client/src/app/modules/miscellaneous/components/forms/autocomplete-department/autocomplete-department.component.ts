import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { Department } from '../../../model/Department';
import { DepartmentService } from '../../../services/department.service';
import { GenericLocalAutocompleteComponent } from '../generic-local-autocomplete/generic-local-autocomplete.component';

@Component({
  selector: 'autocomplete-department',
  templateUrl: './autocomplete-department.component.html',
  styleUrls: ['./autocomplete-department.component.css']
})
export class AutocompleteDepartmentComponent extends GenericLocalAutocompleteComponent<Department> implements OnInit {

  types: Department[] = [] as Array<Department>;

  constructor(private formBuild: UntypedFormBuilder, private departmentService: DepartmentService) {
    super(formBuild)
  }

  filterEntities(types: Department[], value: string): Department[] {
    const filterValue = (value != undefined && value != null && value.toLowerCase != undefined) ? value.toLowerCase() : "";
    return types.filter(department =>
      department.label != undefined && department.code != undefined
      && (department.label.toLowerCase().includes(filterValue) || department.code.includes(filterValue)));
  }

  initTypes(): void {
    this.departmentService.getDepartments().subscribe(response => this.types = response);
  }

  displayLabel(object: Department): string {
    return object ? object.label + " (" + object.code + ")" : '';
  }

}
