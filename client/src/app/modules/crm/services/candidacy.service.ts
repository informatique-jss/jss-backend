import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { Candidacy } from '../../crm/model/Candidacy';

@Injectable({
  providedIn: 'root'
})
export class CandidacyService extends AppRestService<Candidacy> {

  constructor(http: HttpClient) {
    super(http, "crm");
  }

  getCandidacies() {
    return this.getList(new HttpParams(), "candidacies");
  }

}
