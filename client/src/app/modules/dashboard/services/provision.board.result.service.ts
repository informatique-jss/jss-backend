import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { Employee } from '../../profile/model/Employee';
import { ProvisionBoardResult } from '../model/ProvisionBoardResult';

@Injectable({
  providedIn: 'root'
})
export class ProvisionBoardResultService extends AppRestService<ProvisionBoardResult>{

  constructor(http: HttpClient) {
    super(http, "quotation");
  }

  getTeamBoards(employee: Employee[]) {
    return this.postList(new HttpParams(), "dashboard/employee", employee);
  }
}
