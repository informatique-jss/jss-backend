import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { Employee } from 'src/app/modules/profile/model/Employee';
import { EmployeeService } from 'src/app/modules/profile/services/employee.service';
import { GenericLocalAutocompleteComponent } from '../generic-local-autocomplete/generic-local-autocomplete.component';

@Component({
  selector: 'autocomplete-formaliste-employee',
  templateUrl: './autocomplete-formaliste-employee.component.html',
  styleUrls: ['./autocomplete-formaliste-employee.component.css']
})
export class AutocompleteFormalisteEmployeeComponent extends GenericLocalAutocompleteComponent<Employee> implements OnInit {

  types: Employee[] = [] as Array<Employee>;

  constructor(private formBuild: UntypedFormBuilder, private employeeService: EmployeeService) {
    super(formBuild)
  }

  filterEntities(types: Employee[], value: string): Employee[] {
    const filterValue = (value != undefined && value != null && value.toLowerCase != undefined) ? value.toLowerCase() : "";
    return types.filter(employee =>
      employee.firstname != undefined && employee.lastname != undefined
      && (employee.firstname.toLowerCase().includes(filterValue) || employee.lastname.toLowerCase().includes(filterValue)));
  }

  initTypes(): void {
    this.employeeService.getFormalisteEmployees().subscribe(response => this.types = response);
  }

  displayLabel(object: Employee): string {
    return object ? object.firstname + " " + object.lastname : '';
  }

}
