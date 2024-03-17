import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { SalesComplainProblem } from '../../miscellaneous/model/SalesComplainProblem';

@Injectable({
  providedIn: 'root'
})
export class SalesComplainProblemService extends AppRestService<SalesComplainProblem>{

  constructor(http: HttpClient) {
    super(http, "tiers");
  }

  getComplainProblems() {
    return this.getList(new HttpParams(), "sales-complain-problems");
  }

   addOrUpdateComplainProblem(salesComplainProblem: SalesComplainProblem) {
    return this.addOrUpdate(new HttpParams(), "sales-complain-problem", salesComplainProblem, "Enregistré", "Erreur lors de l'enregistrement");
  }
}
