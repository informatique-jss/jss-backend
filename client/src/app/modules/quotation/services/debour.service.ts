import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { Debour } from '../../quotation/model/Debour';

@Injectable({
  providedIn: 'root'
})
export class DebourService extends AppRestService<Debour>{

  constructor(http: HttpClient) {
    super(http, "quotation");
  }

  getDebours() {
    return this.getList(new HttpParams(), "debours");
  }
  
   addOrUpdateDebour(debour: Debour) {
    return this.addOrUpdate(new HttpParams(), "debour", debour, "Enregistré", "Erreur lors de l'enregistrement");
  }

}
