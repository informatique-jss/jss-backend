import { Component, ElementRef, Input, OnInit, ViewChild } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { MatLegacyAutocompleteSelectedEvent as MatAutocompleteSelectedEvent } from '@angular/material/legacy-autocomplete';
import { map, Observable, startWith } from 'rxjs';
import { displayInTeams } from 'src/app/libs/MailHelper';
import { Employee } from 'src/app/modules/profile/model/Employee';
import { EmployeeService } from 'src/app/modules/profile/services/employee.service';
import { UserNoteService } from 'src/app/services/user.notes.service';
import { GenericChipsComponent } from '../generic-chips/generic-chips.component';

@Component({
  selector: 'chips-employee',
  templateUrl: './chips-employee.component.html',
  styleUrls: ['./chips-employee.component.css']
})
export class ChipsEmployeeComponent extends GenericChipsComponent<Employee> implements OnInit {

  Employees: Employee[] = [] as Array<Employee>;
  filteredEmployees: Observable<Employee[]> | undefined;
  @ViewChild('employeeInput') EmployeeInput: ElementRef<HTMLInputElement> | undefined;

  /**
 * Hint to display
 */
  @Input() hint: string = "";

  constructor(private formBuild: UntypedFormBuilder, private employeeService: EmployeeService, private userNoteService2: UserNoteService,) {
    super(formBuild, userNoteService2)
  }

  callOnNgInit(): void {
    this.employeeService.getEmployees().subscribe(response => {
      this.Employees = response;
    })
    if (this.form)
      this.filteredEmployees = this.form.get(this.propertyName)?.valueChanges.pipe(
        startWith(''),
        map(value => this._filterByName(this.Employees, value))
      );
  }


  validateInput(value: string): boolean {
    return true;
  }

  setValueToObject(value: string, object: Employee): Employee {
    return object;
  }

  getValueFromObject(object: Employee): string {
    return object.firstname + " " + object.lastname;
  }

  private _filterByName(inputList: any, value: string): any {
    const filterValue = (value != undefined && value.toLowerCase != undefined) ? value.toLowerCase() : "";
    return inputList.filter((input: any) => input.firstname != undefined && input.firstname.toLowerCase().includes(filterValue) || input.lastname != undefined && input.lastname.toLowerCase().includes(filterValue));
  }

  addEmployee(event: MatAutocompleteSelectedEvent): void {
    if (this.form != undefined) {
      if (!this.model)
        this.model = [] as Array<Employee>;
      // Do not add twice
      if (this.model.map(Employee => Employee.id).indexOf(event.option.value.id) >= 0)
        return;
      if (event.option && event.option.value && event.option.value.id)
        this.model.push(event.option.value);
      this.modelChange.emit(this.model);
      this.form.get(this.propertyName)?.setValue(null);
      this.EmployeeInput!.nativeElement.value = '';
    }
  }

  displayInTeams = function (employee: Employee) {
    displayInTeams(employee);
  }
}
