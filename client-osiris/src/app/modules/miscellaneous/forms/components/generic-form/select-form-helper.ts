import { Injectable } from "@angular/core";
import { Observable, of } from "rxjs";
import { ConstantService } from "../../../../main/services/constant.service";
import { Employee } from "../../../../profile/model/Employee";
import { EmployeeService } from "../../../../profile/services/employee.service";
import { SimpleProvisionStatus } from "../../../../quotation/model/SimpleProvisonStatus";
import { SimpleProvisionStatusService } from "../../../../quotation/services/simple.provision.status.service";


export const SELECT_TYPES = ['commercial', 'formaliste', 'provisionStatus'] as const;
export type SelectType = typeof SELECT_TYPES[number] | undefined;

@Injectable({
  providedIn: 'root'
})
export class SelectFormHelper {

  constructor(
    private employeeService: EmployeeService,
    private simpleProvisionStatusService: SimpleProvisionStatusService,
    private constantService: ConstantService
  ) { }

  selectValues: { [key: string]: any[] } = {};

  getValues(type: SelectType): Observable<any> {
    if (type == 'commercial') {
      if (this.selectValues[type])
        return of(this.selectValues[type]);
      return new Observable<Employee[]>(observer => {
        this.employeeService.getEmployees().subscribe(response => {
          let adSales = this.constantService.getActiveDirectoryGroupSales();
          let outEmployees = [];
          if (adSales && response)
            for (let employee of response) {
              if (employee.isActive && employee.adPath && employee.adPath.indexOf(adSales.activeDirectoryPath) >= 0)
                outEmployees.push(employee);
            }
          let values = outEmployees.sort((a, b) => a.lastname.localeCompare(b.lastname));
          this.selectValues[type] = values;
          observer.next(values);
          observer.complete;
        })
      })
    }
    if (type == 'formaliste') {
      if (this.selectValues[type])
        return of(this.selectValues[type]);
      return new Observable<Employee[]>(observer => {
        this.employeeService.getEmployees().subscribe(response => {
          let adFormalites = this.constantService.getActiveDirectoryGroupFormalites();
          let outEmployees = [];
          if (adFormalites && response)
            for (let employee of response) {
              if (employee.isActive && employee.adPath && employee.adPath.indexOf(adFormalites.activeDirectoryPath) >= 0)
                outEmployees.push(employee);
            }
          let values = outEmployees.sort((a, b) => a.lastname.localeCompare(b.lastname));
          this.selectValues[type] = values;
          observer.next(values);
          observer.complete;
        })
      })
    }
    if (type == 'provisionStatus') {
      if (this.selectValues[type])
        return of(this.selectValues[type]);
      return new Observable<SimpleProvisionStatus[]>(observer => {
        this.simpleProvisionStatusService.getSimpleProvisionStatus().subscribe(response => {
          this.selectValues[type] = response;
          observer.next(response);
          observer.complete;
        })
      })
    }
    throw new Error('select type not implemented');
  }
}
