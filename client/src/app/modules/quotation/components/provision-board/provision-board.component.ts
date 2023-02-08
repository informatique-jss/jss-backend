import { Component, Input, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Subject } from 'rxjs/internal/Subject';
import { STATUS_DONE, STATUS_IN_PROGRESS, STATUS_NEW, STATUS_PUBLISHED, STATUS_REFUSED_GREFFE, STATUS_VALIDATE_GREFFE, STATUS_WAITING, STATUS_WAITING_AUTHORITY, STATUS_WAITING_CONFRERE_PUBLISHED, STATUS_WAITING_GREFFE } from 'src/app/libs/Constants';
import { getObjectPropertybyString } from 'src/app/libs/FormatHelper';
import { SortTableAction } from 'src/app/modules/miscellaneous/model/SortTableAction';
import { SortTableColumn } from 'src/app/modules/miscellaneous/model/SortTableColumn';
import { Employee } from 'src/app/modules/profile/model/Employee';
import { EmployeeService } from 'src/app/modules/profile/services/employee.service';
import { AppService } from 'src/app/services/app.service';
import { ProvisionBoardDisplayed } from '../../model/ProvisionBoardDisplayed';
import { ProvisionBoardDisplayedResult } from '../../model/ProvisionBoardDisplayedResult';
import { ProvisionBoardResultService } from '../../services/provision.board.result.service';

@Component({
  selector: 'provision-board',
  templateUrl: './provision-board.component.html',
  styleUrls: ['./provision-board.component.css']
})
export class ProvisionBoardComponent implements OnInit {

  @Input() currentEmployee: Employee | undefined = {} as Employee;
  @Input() teamBoard: string = '';

  nbProvisions!: ProvisionBoardDisplayedResult[];
  allEmployees: Employee[] = [];
  employeeSelected: Employee[];
  displayedEmployees: Employee[] = [] as Employee[];
  listStatusUsed: string[] = [];

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

    // Set all used status in columnToDisplayOnDashboard
    this.listStatusUsed.push(STATUS_NEW);        // allway used
    this.listStatusUsed.push(STATUS_IN_PROGRESS);// allway used

    let found: string|undefined;
    this.nbProvisions.forEach((item:ProvisionBoardDisplayedResult) => {
      if (item.nbProvisionWaiting > 0) {
        this.setStatusUsed(STATUS_WAITING);
      }
      if (item.nbProvisionWaitingGreffe > 0) {
        this.setStatusUsed(STATUS_WAITING_GREFFE);
      }
      if (item.nbProvisionWaitingAuthority > 0) {
        this.setStatusUsed(STATUS_WAITING_AUTHORITY);
      }
      if (item.nbProvisionConfrerePublished > 0) {
        this.setStatusUsed(STATUS_WAITING_CONFRERE_PUBLISHED);
      }
      if (item.nbProvisionValidateGreffe > 0) {
        this.setStatusUsed(STATUS_VALIDATE_GREFFE);
      }
      if (item.nbProvisionRefusedGreffe > 0) {
        this.setStatusUsed(STATUS_REFUSED_GREFFE);
      }
      if (item.nbProvisionPublished > 0) {
        this.setStatusUsed(STATUS_PUBLISHED);
      }
      if (item.nbProvisionDone > 0) {
        this.setStatusUsed(STATUS_DONE);
      }

    });

      // displayedColumns define columns of the board
      this.displayedColumns = [];
      this.displayedColumns.push({ id: "employee", fieldName: "employee", label: "Personne" } as SortTableColumn);
      if (this.foundStatusUsed(STATUS_NEW))
        this.displayedColumns.push({ id: "nbProvisionNew", fieldName: "nbProvisionNew", label: "Ouvert" } as SortTableColumn);
      if (this.foundStatusUsed(STATUS_IN_PROGRESS))
        this.displayedColumns.push({ id: "nbProvisionInProgress", fieldName: "nbProvisionInProgress", label: "En cours" } as SortTableColumn);
      if (this.foundStatusUsed(STATUS_WAITING))
        this.displayedColumns.push({ id: "nbProvisionWaiting", fieldName: "nbProvisionWaiting", label: "En attente" } as SortTableColumn);
      if (this.foundStatusUsed(STATUS_WAITING_AUTHORITY))
        this.displayedColumns.push({ id: "nbProvisionWaitingAuthority", fieldName: "nbProvisionWaitingAuthority", label: "En attente de l'autorité compétente" } as SortTableColumn);
      if (this.foundStatusUsed(STATUS_WAITING_GREFFE))
        this.displayedColumns.push({ id: "nbProvisionWaitingGreffe", fieldName: "nbProvisionWaitingGreffe", label: "Envoyé au greffe" } as SortTableColumn);
      if (this.foundStatusUsed(STATUS_VALIDATE_GREFFE))
        this.displayedColumns.push({ id: "nbProvisionValidateGreffe", fieldName: "nbProvisionValidateGreffe", label: "Validé par le greffe" } as SortTableColumn);
      if (this.foundStatusUsed(STATUS_REFUSED_GREFFE))
        this.displayedColumns.push({ id: "nbProvisionRefusedGreffe", fieldName: "nbProvisionRefusedGreffe", label: "Refusé par le greffe" } as SortTableColumn);
      if (this.foundStatusUsed(STATUS_WAITING_CONFRERE_PUBLISHED))
        this.displayedColumns.push({ id: "nbProvisionConfrerePublished", fieldName: "nbProvisionConfrerePublished", label: "En attente de publication par le confrère" } as SortTableColumn);
      if (this.foundStatusUsed(STATUS_PUBLISHED))
        this.displayedColumns.push({ id: "nbProvisionPublished", fieldName: "nbProvisionPublished", label: "Publié" } as SortTableColumn);
      if (this.foundStatusUsed(STATUS_DONE))
        this.displayedColumns.push({ id: "nbProvisionDone", fieldName: "nbProvisionDone", label: "Terminé" } as SortTableColumn);

      // Get Datas on allEmployees
      this.employeeService.getEmployees().subscribe(response => {
        this.allEmployees = response;

        this.refreshDataDisplayed(this.allEmployees);

      }); // this.employeeService.getEmployees()


    }); // this.provisionBoardResultService.getTeamBoards


  }

  /**
   * Set all used status found in nbProvisions
   */
  setStatusUsed(status: string) {
    let found: string|undefined;

    found = this.listStatusUsed.find((value: string) =>
      value === status
    );
    if (found === undefined) {
      this.listStatusUsed.push(status);
    }

  }

  foundStatusUsed(status: string) {
    let found: string|undefined;

    found = this.listStatusUsed.find((value: string) =>
      value === status
    );
    return (found !== undefined);

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

    this.nbProvisions.sort((n1, n2) => {
        return n1.employee - n2.employee;
      }); // this.nbProvisions?.sort

    this.nbProvisionsDisplayed = [];

    // nbProvisionsDisplayed define lines of datas
    let current:ProvisionBoardDisplayed|undefined = undefined;
    let emp:Employee|undefined;
    let indexEmp: number = -1;
    let item:ProvisionBoardDisplayedResult|undefined = undefined;
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
        current.initNbProvision(item.nbProvisionNew, item.nbProvisionInProgress,
                  item.nbProvisionWaiting, item.nbProvisionWaitingGreffe,
                  item.nbProvisionWaitingAuthority, item.nbProvisionConfrerePublished,
                  item.nbProvisionValidateGreffe, item.nbProvisionRefusedGreffe,
                  item.nbProvisionPublished, item.nbProvisionDone);

        this.nbProvisionsDisplayed.push(current);

        // L'employe will be displayed : we remove him from the array
        employeesToAddForDisplaying.splice(indexEmp, 1);

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
        current.initNbProvision(0, 0, 0, 0, 0, 0, 0, 0, 0, 0);

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
