import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { SalesReclamationProblem } from '../../miscellaneous/model/SalesReclamationProblem';

@Injectable({
  providedIn: 'root'
})
export class SalesReclamationProblemService extends AppRestService<SalesReclamationProblem>{

  constructor(http: HttpClient) {
    super(http, "tiers");
  }

  getReclamationProblems() {
    return this.getList(new HttpParams(), "sales-reclamation-problems");
  }

   addOrUpdateReclamationProblem(salesReclamationProblem: SalesReclamationProblem) {
    return this.addOrUpdate(new HttpParams(), "sales-reclamation-problem", salesReclamationProblem, "Enregistré", "Erreur lors de l'enregistrement");
  }
}
