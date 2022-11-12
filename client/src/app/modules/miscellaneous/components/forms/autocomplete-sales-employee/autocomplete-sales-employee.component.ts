import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { Employee } from 'src/app/modules/profile/model/Employee';
import { EmployeeService } from 'src/app/modules/profile/services/employee.service';
import { UserNoteService } from 'src/app/services/user.notes.service';
import { GenericLocalAutocompleteComponent } from '../generic-local-autocomplete/generic-local-autocomplete.component';

@Component({
  selector: 'autocomplete-sales-employee',
  templateUrl: './../autocomplete-employee/autocomplete-employee.component.html',
  styleUrls: ['./../autocomplete-employee/autocomplete-employee.component.css']
})
export class AutocompleteSalesEmployeeComponent extends GenericLocalAutocompleteComponent<Employee> implements OnInit {

  types: Employee[] = [] as Array<Employee>;

  constructor(private formBuild: UntypedFormBuilder, private employeeService: EmployeeService, private userNoteService2: UserNoteService,) {
    super(formBuild, userNoteService2)
  }

  filterEntities(types: Employee[], value: string): Employee[] {
    const filterValue = (value != undefined && value != null && value.toLowerCase != undefined) ? value.toLowerCase() : "";
    return types.filter(employee =>
      employee.firstname != undefined && employee.lastname != undefined && employee.isActive == true
      && (employee.firstname.toLowerCase().includes(filterValue) || employee.lastname.toLowerCase().includes(filterValue)));
  }

  initTypes(): void {
    this.employeeService.getEmployees().subscribe(response => this.types = response);
  }

  displayLabel(object: Employee): string {
    return object ? object.firstname + " " + object.lastname : '';
  }

}
