import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { AGGREGATE_STATUS_DONE, AGGREGATE_STATUS_IN_PROGRESS, AGGREGATE_STATUS_NEW, AGGREGATE_STATUS_WAITING } from 'src/app/libs/Constants';
import { SortTableColumn } from 'src/app/modules/miscellaneous/model/SortTableColumn';
import { UserPreferenceService } from 'src/app/services/user.preference.service';
import { Employee } from '../../../profile/model/Employee';
import { ProvisionBoardResult } from '../../model/ProvisionBoardResult';
import { ProvisionBoardResultAggregated } from '../../model/ProvisionBoardResultAggregated';
import { ProvisionBoardResultService } from '../../services/provision.board.result.service';

@Component({
  selector: 'provision-board',
  templateUrl: './provision-board.component.html',
  styleUrls: ['./provision-board.component.css']
})
export class ProvisionBoardComponent implements OnInit {

  employeesSelected: Employee[] | undefined;
  provisionBoardResults: ProvisionBoardResult[] | undefined;
  provisionBoardResultsAggregated: ProvisionBoardResultAggregated[] | undefined;
  displayedColumns: SortTableColumn[] = [];

  constructor(private provisionBoardResultService: ProvisionBoardResultService,
    private formBuilder: FormBuilder,
    private userPreferenceService: UserPreferenceService,
  ) { }

  provisionBoardForm = this.formBuilder.group({});

  ngOnInit() {
    this.displayedColumns = [];
    this.displayedColumns.push({ id: "employeeName", fieldName: "employeeName", label: "Collaborateur" } as SortTableColumn);
    this.displayedColumns.push({ id: "type", fieldName: "type", label: "Type de prestation" } as SortTableColumn);
    this.displayedColumns.push({ id: "aggregateStatusNewNumber", fieldName: "aggregateStatusNewNumber", label: "Nouveau" } as SortTableColumn);
    this.displayedColumns.push({ id: "aggregateStatusInProgress", fieldName: "aggregateStatusInProgress", label: "En cours" } as SortTableColumn);
    this.displayedColumns.push({ id: "aggregateStatusWaiting", fieldName: "aggregateStatusWaiting", label: "En attente" } as SortTableColumn);
    this.displayedColumns.push({ id: "aggregateStatusDone", fieldName: "aggregateStatusDone", label: "Terminé" } as SortTableColumn);

    this.employeesSelected = this.userPreferenceService.getUserSearchBookmark("provisionBoardEmployees") as Employee[];
    this.refreshData();
  }

  refreshData() {
    if (this.employeesSelected && this.employeesSelected.length > 0) {
      this.userPreferenceService.setUserSearchBookmark(this.employeesSelected, "provisionBoardEmployees");
      this.provisionBoardResultService.getTeamBoards(this.employeesSelected).subscribe(response => {
        this.provisionBoardResults = response;
        this.pivotData();
      })
    }
  }

  pivotData() {
    if (this.provisionBoardResults) {
      this.provisionBoardResultsAggregated = [];
      for (let result of this.provisionBoardResults) {
        let found = false;
        for (let aggregatedResult of this.provisionBoardResultsAggregated) {
          if (aggregatedResult.employeeName == result.employeeName && aggregatedResult.type == result.type) {
            if (result.aggregateStatus == AGGREGATE_STATUS_NEW)
              aggregatedResult.aggregateStatusNewNumber = result.number;
            else if (result.aggregateStatus == AGGREGATE_STATUS_IN_PROGRESS)
              aggregatedResult.aggregateStatusInProgress = result.number;
            else if (result.aggregateStatus == AGGREGATE_STATUS_WAITING)
              aggregatedResult.aggregateStatusWaiting = result.number;
            else if (result.aggregateStatus == AGGREGATE_STATUS_DONE)
              aggregatedResult.aggregateStatusDone = result.number;

            found = true;
            break;
          }
        }
        if (!found)
          if (result.aggregateStatus == AGGREGATE_STATUS_NEW)
            this.provisionBoardResultsAggregated.push({ employeeName: result.employeeName, type: result.type, aggregateStatusNewNumber: result.number } as ProvisionBoardResultAggregated)
          else if (result.aggregateStatus == AGGREGATE_STATUS_IN_PROGRESS)
            this.provisionBoardResultsAggregated.push({ employeeName: result.employeeName, type: result.type, aggregateStatusInProgress: result.number } as ProvisionBoardResultAggregated)
          else if (result.aggregateStatus == AGGREGATE_STATUS_WAITING)
            this.provisionBoardResultsAggregated.push({ employeeName: result.employeeName, type: result.type, aggregateStatusWaiting: result.number } as ProvisionBoardResultAggregated)
          else if (result.aggregateStatus == AGGREGATE_STATUS_DONE)
            this.provisionBoardResultsAggregated.push({ employeeName: result.employeeName, type: result.type, aggregateStatusDone: result.number } as ProvisionBoardResultAggregated)
      }
      console.log(this.provisionBoardResultsAggregated);
    }
  }
}
