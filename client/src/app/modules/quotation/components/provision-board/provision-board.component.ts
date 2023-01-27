import { Component, Input, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Subject } from 'rxjs/internal/Subject';
import { getObjectPropertybyString } from 'src/app/libs/FormatHelper';
import { SortTableAction } from 'src/app/modules/miscellaneous/model/SortTableAction';
import { SortTableColumn } from 'src/app/modules/miscellaneous/model/SortTableColumn';
import { Employee } from 'src/app/modules/profile/model/Employee';
import { EmployeeService } from 'src/app/modules/profile/services/employee.service';
import { AppService } from 'src/app/services/app.service';
import { UserPreferenceService } from 'src/app/services/user.preference.service';
import { ProvisionBoardDisplayed } from '../../model/ProvisionBoardDisplayed';
import { ProvisionBoardResult } from '../../model/ProvisionBoardResult';
import { ProvisionBoardResultService } from '../../services/provision.board.result.service';

@Component({
  selector: 'provision-board',
  templateUrl: './provision-board.component.html',
  styleUrls: ['./provision-board.component.css']
})
export class ProvisionBoardComponent implements OnInit {

  @Input() currentEmployee: Employee | undefined = {} as Employee;
  @Input() teamBoard: string = '';

  nbProvisions!: ProvisionBoardResult[];
  allEmployees: Employee[] = [];
  employeeSelected: Employee[];
  displayedEmployees: Employee[] = [] as Employee[];

  // List of headers of columns
  columnToDisplayOnDashboard: string[] = ["Employé"];

  // nbProvisionsDisplayed define line of datas
  nbProvisionsDisplayed: ProvisionBoardDisplayed[] = [];

  // displayedColumns define columns of the board
  displayedColumns: SortTableColumn[] = [];

  // Actions in the board
  tableAction: SortTableAction[] = [];

  // Refresh
  refreshDatasTable: Subject<void> = new Subject<void>();

  FIELD_NAME_NB_PROVISION: string = "nbProvision";


  constructor(    private appService: AppService,
    private provisionBoardResultService: ProvisionBoardResultService,
    private employeeService: EmployeeService,
    private formBuilder: FormBuilder,
) {
  this.employeeSelected = [];
}

  ngOnInit()  {


    let employeeSelectedIds = this.getEmployeeSelectedIds();

    // Get the datas from the board
    this.provisionBoardResultService.getTeamBoards(this.teamBoard, employeeSelectedIds).subscribe(response => {
      this.nbProvisions = response;


      this.nbProvisions.sort(function(n1, n2){
        if (n1.priority !== n2.priority)
          return n1.priority - n2.priority;
        else {
          if (n1.status < n2.status)
            return -1;
          else if (n1.status > n2.status)
            return 1;
          else
            return 0;
        }
      }); // this.nbProvisions?.sort

      // Set all used status in columnToDisplayOnDashboard
      let lastLabelStatus = '';
      this.nbProvisions.forEach((item:ProvisionBoardResult) => {
        if (item.status != lastLabelStatus) {
          this.columnToDisplayOnDashboard.push(item.status);
          lastLabelStatus = item.status;
        }
      });

      // displayedColumns define columns of the board
      this.displayedColumns = [];
      this.displayedColumns.push({ id: "employee", fieldName: "employee", label: "Personne" } as SortTableColumn);
      for(var i: number = 1; i < this.columnToDisplayOnDashboard.length; i++) {
        this.displayedColumns.push({ id:this.FIELD_NAME_NB_PROVISION+i,
                              fieldName: this.FIELD_NAME_NB_PROVISION+i,
                              label: this.columnToDisplayOnDashboard[i] } as SortTableColumn);
      }

      // Get Datas on allEmployees
      this.employeeService.getEmployees().subscribe(response => {
        this.allEmployees = response;

        this.refreshDataDisplayed(this.allEmployees);

      }); // this.employeeService.getEmployees()


    }); // this.provisionBoardResultService.getTeamBoards


  }

  /**
   * Convert this.employeeSelected to an array of their ids
   * @returns an array of employees ids
   */
  getEmployeeSelectedIds(): number[] {
    let employeeSelectedIds: number[] = [];
    this.employeeSelected.forEach((emp:Employee) => {
      employeeSelectedIds.push(emp.id);
    });
    return employeeSelectedIds;
  }

  /**
   * Called when list employees changes
   */
  refreshEmployeesSelectedDataDisplayed(event: Event) {
    if (this.employeeSelected.length > 0) {
      this.refreshDataDisplayed(this.employeeSelected);
    } else {
      this.refreshDataDisplayed(this.allEmployees);
    }
  }

  /**
   * Refresh datas displayed in function of an array of employees
   * Don't update headers
   */
  refreshDataDisplayed(employeesToDisplay: Employee[]) {

    // Appel a l init par le subject pour le refresh
    if (this.nbProvisions === undefined) {
      return;
    }

    let employeesToAddForDisplaying: Employee[] = [];
    employeesToAddForDisplaying = employeesToAddForDisplaying.concat(employeesToDisplay);


    /* ***************************************************** */
    /* Display in first lines : employee who have provisions */

    this.nbProvisions.sort(function(n1, n2){
      if (n1.employee !== n2.employee)
        return n1.employee - n2.employee;
      else {
        return n1.priority - n2.priority;
      }
    }); // this.nbProvisions?.sort

    this.nbProvisionsDisplayed = [];

    // nbProvisionsDisplayed define lines of datas
    let current:ProvisionBoardDisplayed|undefined = undefined;
    let emp:Employee|undefined;
    let indexEmp: number = -1;
    let item:ProvisionBoardResult|undefined = undefined;
    let isNewEmployee = true;
    for (let indexDataSource=0; indexDataSource < this.nbProvisions.length; ) {
      item = this.nbProvisions[indexDataSource];

      indexEmp = -1;
      indexEmp = employeesToAddForDisplaying.findIndex(function(e) {
        if (item !== undefined) {
          return item.employee === e.id
        } else {
          return false;
        }
      });

      emp = undefined;
      if (indexEmp !== -1) {
        emp = employeesToAddForDisplaying[indexEmp];
      }
      isNewEmployee = (emp === undefined);

      if (!isNewEmployee && emp !== undefined) {

        current = new ProvisionBoardDisplayed();
        current.employee = emp.firstname+' '+emp.lastname;
        current.initNbProvision();

        // Set in nbProvisionsDisplayed in function of emp and values in nbProvisions
        for(var i: number = 1; i < this.columnToDisplayOnDashboard.length && !isNewEmployee; i++) {

          if (item !== undefined && this.columnToDisplayOnDashboard[i] === item.status) {

            let indexNnProv = i;
            switch (indexNnProv) {
              case 1: {current.nbProvision1 = item.nbProvision; break;}
              case 2: {current.nbProvision2 = item.nbProvision; break;}
              case 3: {current.nbProvision3 = item.nbProvision; break;}
              case 4: {current.nbProvision4 = item.nbProvision; break;}
              case 5: {current.nbProvision5 = item.nbProvision; break;}
              case 6: {current.nbProvision6 = item.nbProvision; break;}
              case 7: {current.nbProvision7 = item.nbProvision; break;}
              case 8: {current.nbProvision8 = item.nbProvision; break;}
              case 9: {current.nbProvision9 = item.nbProvision; break;}
              case 10: {current.nbProvision10 = item.nbProvision; break;}
              case 11: {current.nbProvision11 = item.nbProvision; break;}
              case 12: {current.nbProvision12 = item.nbProvision; break;}
              case 13: {current.nbProvision13 = item.nbProvision; break;}
              case 14: {current.nbProvision14 = item.nbProvision; break;}
              case 15: {current.nbProvision15 = item.nbProvision; break;}
              case 16: {current.nbProvision16 = item.nbProvision; break;}
              case 17: {current.nbProvision17 = item.nbProvision; break;}
              case 18: {current.nbProvision18= item.nbProvision; break;}
              case 19: {current.nbProvision19 = item.nbProvision; break;}
              case 20: {current.nbProvision20 = item.nbProvision; break;}
            }

            indexDataSource++;
            item = this.nbProvisions[indexDataSource];
            if (item === undefined || item.employee !== emp.id) {
              isNewEmployee = true;
            }
          }
        } // for(var i: number = 1; i < this.columnToDisplayOnDashboard.length ...

        if (isNewEmployee && current !== undefined) {
          this.nbProvisionsDisplayed.push(current);

          // L'employe will be displayed : we remove him from the array
          employeesToAddForDisplaying.splice(indexEmp, 1);
        } else {
          console.log("Status "+item.status+" not found for "+item.employee);
        }
      } else { // employe incorrect
        console.log("Employe n'a pas de prestation "+item.employee);
        indexDataSource++;
        item = this.nbProvisions[indexDataSource];
      } // if (!isNewEmployee && emp !== undefined)


    } //for (let indexDataSource=0; indexDataSource < this.nbProvisions.length; indexDataSource++) {




    /* ***************************************************** */
    /* Display in last lines : employee who do not have provisions */

    if (employeesToAddForDisplaying.length > 0) {

      employeesToAddForDisplaying.sort(function(n1, n2){
        if (n1.lastname < n2.lastname)
          return -1;
        else if (n1.lastname > n2.lastname)
          return 1;
        else
          return 0;
      }); // this.nbProvisions?.sort

      // nbProvisionsDisplayed define lines of datas
      current = undefined;
      employeesToAddForDisplaying.forEach((unEmp:Employee) => {

        current = new ProvisionBoardDisplayed();
        current.employee = unEmp.firstname+' '+unEmp.lastname;
        current.initNbProvision();

        this.nbProvisionsDisplayed.push(current);

      });

    } // if (employeesToAddForDisplaying.length > 0)


    this.refreshNbProvisionsDisplayed();

  }



  provisionBoardForm = this.formBuilder.group({   });

  getObjectPropertybyString = getObjectPropertybyString;

  refreshNbProvisionsDisplayed() {
    this.refreshDatasTable.next();
  }

}
