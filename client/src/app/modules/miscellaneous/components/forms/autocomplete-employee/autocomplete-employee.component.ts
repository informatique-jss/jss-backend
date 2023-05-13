import { Component, Input, OnInit, SimpleChanges, ViewChild } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { MatLegacyAutocompleteTrigger as MatAutocompleteTrigger } from '@angular/material/legacy-autocomplete';
import { Employee } from 'src/app/modules/profile/model/Employee';
import { EmployeeService } from 'src/app/modules/profile/services/employee.service';
import { UserNoteService } from 'src/app/services/user.notes.service';
import { GenericLocalAutocompleteComponent } from '../generic-local-autocomplete/generic-local-autocomplete.component';

@Component({
  selector: 'autocomplete-employee',
  templateUrl: './autocomplete-employee.component.html',
  styleUrls: ['./autocomplete-employee.component.css']
})
export class AutocompleteEmployeeComponent extends GenericLocalAutocompleteComponent<Employee> implements OnInit {

  types: Employee[] = [] as Array<Employee>;

  @Input() defaultEmployee: Employee | undefined;
  @ViewChild(MatAutocompleteTrigger) autocomplete!: MatAutocompleteTrigger;

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
    this.employeeService.getEmployees().subscribe(response => {
      this.types = response;
      if (this.types)
        this.types = this.types.sort(function (a: Employee, b: Employee) {
          return (a.firstname + a.lastname).localeCompare(b.firstname + b.lastname);
        });
      this.setDefaultEmployee();
    });
  }

  ngOnChanges(changes: SimpleChanges): void {
    super.ngOnChanges(changes);
    if (changes.defaultEmployee)
      this.setDefaultEmployee();
  }

  setDefaultEmployee() {
    if (this.defaultEmployee) {
      for (let type of this.types) {
        if (type.username == this.defaultEmployee.username) {
          this.model = type;
        }
      }
      this.modelChange.emit(this.model);
    }
  }

  displayLabel(object: any): string {
    if (object && object.firstname && object.lastname)
      return object.firstname + " " + object.lastname;
    if (typeof object === "string")
      return object;
    return "";
  }

  chooseMyself() {
    this.employeeService.getCurrentEmployee().subscribe(response => {
      this.autocomplete.closePanel();
      this.model = response;
      this.modelChange.emit(this.model);
    })
  }

}
