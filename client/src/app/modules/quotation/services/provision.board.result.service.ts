import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { ProvisionBoardResult } from '../model/ProvisionBoardResult';

@Injectable({
  providedIn: 'root'
})
export class ProvisionBoardResultService extends AppRestService<ProvisionBoardResult>{

  constructor(http: HttpClient) {
    super(http, "quotation");
  }

  getTeamBoards(teamBoard: string, employee: number[]) {
    let provisionToRetrieve = '';

    switch (teamBoard) {
      case 'AL' :
        provisionToRetrieve = 'announcement';
        break;
      case 'FORMALITE' :
        provisionToRetrieve = 'formalite';
        break;
      default :
        provisionToRetrieve = 'formalite';
    }

    return this.postList(new HttpParams(), "dashboard/employee-"+provisionToRetrieve, employee);
  }

}
