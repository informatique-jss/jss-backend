import { Component, Input, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
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
  employees: Employee[] = [];

  // List of status columns
  columnToDisplayOnDashboard: string[] = ["Employé"];

  // nbProvisionsDisplayed define line of datas
  nbProvisionsDisplayed: ProvisionBoardDisplayed[] = [];

  // displayedColumns define columns of the board
  displayedColumns: SortTableColumn[] = [];

  // Actions in the board
  tableAction: SortTableAction[] = [];

  constructor(    private appService: AppService,
    private provisionBoardResultService: ProvisionBoardResultService,
    private employeeService: EmployeeService,
    private formBuilder: FormBuilder,
) {

}

  ngOnInit()  {

    const FIELD_NAME_NB_PROVISION: string = "nbProvision";

    // Get the datas from the board
    this.provisionBoardResultService.getTeamBoards(this.teamBoard, []).subscribe(response => {
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
      this.displayedColumns.push({ id: "employee", fieldName: "employee", label: "Employé" } as SortTableColumn);
      for(var i: number = 1; i < this.columnToDisplayOnDashboard.length; i++) {
        this.displayedColumns.push({ id:FIELD_NAME_NB_PROVISION+(i+1), fieldName: FIELD_NAME_NB_PROVISION+(i+1), label: this.columnToDisplayOnDashboard[i] } as SortTableColumn);
      }

      // Get Datas on employees
      this.employeeService.getEmployees().subscribe(response => {
        this.employees = response;

        this.nbProvisions.sort(function(n1, n2){
          if (n1.employee !== n2.employee)
            return n1.employee - n2.employee;
          else {
            return n1.priority - n2.priority;
          }
        }); // this.nbProvisions?.sort

        // nbProvisionsDisplayed define line of datas
        let current:ProvisionBoardDisplayed;
        let emp:Employee|undefined;
        let item:ProvisionBoardResult;
        let isNewEmployee = true;
        for (let indexDataSource=0; indexDataSource < this.nbProvisions.length; ) {
          item = this.nbProvisions[indexDataSource];

          emp = this.employees.find(function(e) {
            return item.employee === e.id
          });

          isNewEmployee = (emp === undefined);

          if (!isNewEmployee && emp !== undefined) {
            current = new ProvisionBoardDisplayed();
            current.employee = emp.firstname+' '+emp.lastname;
            current.initNbProvision();

            // Set in nbProvisionsDisplayed in function of emp and values in nbProvisions
            for(var i: number = 1; i < this.columnToDisplayOnDashboard.length && !isNewEmployee; i++) {

              if (this.columnToDisplayOnDashboard[i] === item.status) {

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

            if (isNewEmployee) {
              this.nbProvisionsDisplayed.push(current);
            } else {
              console.log("Status "+item.status+" not found for "+item.employee);
            }
          } else { // employe incorrect
            console.log("Employe incorrect "+item.employee);
            indexDataSource++;
            item = this.nbProvisions[indexDataSource];
          } // if (!isNewEmployee && emp !== undefined)


        } //for (let indexDataSource=0; indexDataSource < this.nbProvisions.length; indexDataSource++) {


      }); // this.employeeService.getEmployees()


    }); // this.provisionBoardResultService.getTeamBoards


  }

  quotationSearchForm = this.formBuilder.group({   });

  getObjectPropertybyString = getObjectPropertybyString;

}
