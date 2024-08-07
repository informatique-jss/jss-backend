import { Component, Input, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { Department } from '../../../model/Department';
import { DepartmentService } from '../../../services/department.service';
import { GenericLocalAutocompleteComponent } from '../generic-local-autocomplete/generic-local-autocomplete.component';
import { AppService } from 'src/app/services/app.service';

@Component({
  selector: 'autocomplete-department',
  templateUrl: '../generic-local-autocomplete/generic-local-autocomplete.component.html',
  styleUrls: ['../generic-local-autocomplete/generic-local-autocomplete.component.css'],
})
export class AutocompleteDepartmentComponent extends GenericLocalAutocompleteComponent<Department> implements OnInit {

  types: Department[] = [] as Array<Department>;

  @Input() filterAvailableEntities: Department[] | undefined;

  constructor(private formBuild: UntypedFormBuilder, private departmentService: DepartmentService, private appService3: AppService) {
    super(formBuild, appService3)
  }

  filterEntities(types: Department[], value: string): Department[] {
    const filterValue = (value != undefined && value != null && value.toLowerCase != undefined) ? value.toLowerCase() : "";
    let filteredTypes = types.filter(department =>
      department.label != undefined && department.code != undefined
      && (department.label.toLowerCase().includes(filterValue) || department.code.includes(filterValue)));

    let finalFilteredTypes = [];
    if (this.filterAvailableEntities) {
      for (let filterType of filteredTypes)
        for (let filterTypeAvailable of this.filterAvailableEntities)
          if (filterType.id == filterTypeAvailable.id)
            finalFilteredTypes.push(filterType);
    } else {
      finalFilteredTypes = filteredTypes;
    }
    return finalFilteredTypes;
  }

  initTypes(): void {
    this.departmentService.getDepartments().subscribe(response => this.types = response);
  }

  displayLabel(object: Department): string {
    return object ? object.label + " (" + object.code + ")" : '';
  }

}
