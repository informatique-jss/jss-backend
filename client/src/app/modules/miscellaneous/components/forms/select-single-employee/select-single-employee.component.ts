import { Component, Input, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { Employee } from 'src/app/modules/profile/model/Employee';
import { EmployeeService } from 'src/app/modules/profile/services/employee.service';
import { AppService } from 'src/app/services/app.service';
import { ActiveDirectoryGroup } from '../../../model/ActiveDirectoryGroup';
import { ConstantService } from '../../../services/constant.service';
import { GenericSelectComponent } from '../generic-select/generic-select.component';

@Component({
  selector: 'select-single-employee',
  templateUrl: './select-single-employee.component.html',
  styleUrls: ['./../generic-select/generic-select.component.html']
})

export class SelectSingleEmployeeComponent extends GenericSelectComponent<Employee> implements OnInit {

  types: Employee[] = [] as Array<Employee>;

  @Input() filteredAdGroups: ActiveDirectoryGroup[] | undefined;
  @Input() addProductionDirector: boolean = false;

  constructor(private formBuild: UntypedFormBuilder, private employeeService: EmployeeService, private appService3: AppService, private constantService: ConstantService) {
    super(formBuild, appService3)
  }

  initTypes(): void {
    this.types = [];
    this.employeeService.getEmployees().subscribe(response => {
      for (let employee of response)
        if (employee.isActive) {
          let addIt = false;

          if (this.filteredAdGroups) {
            for (let group of this.filteredAdGroups) {
              if (employee.adPath.indexOf(group.activeDirectoryPath) >= 0) {
                addIt = true;
                break;
              }
            }
          } else {
            addIt = true;
          }

          if (addIt)
            this.types.push(employee);
        }

      if (this.addProductionDirector)
        this.types.push(this.constantService.getEmployeeProductionDirector())
      this.types.sort((a, b) => a.firstname.localeCompare(b.firstname));
    });
  }

  displayLabel(object: any): string {
    if (object && object.firstname && object.lastname)
      return object.firstname + " " + object.lastname;
    if (typeof object === "string")
      return object;
    return "";
  }

}
