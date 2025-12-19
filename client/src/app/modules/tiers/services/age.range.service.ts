import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { AgeRange } from '../../tiers/model/AgeRange';

@Injectable({
  providedIn: 'root'
})
export class AgeRangeService extends AppRestService<AgeRange>{

  constructor(http: HttpClient) {
    super(http, "tiers");
  }

  getAgeRanges() {
    return this.getList(new HttpParams(), "age-ranges");
  }
  
   addOrUpdateAgeRange(ageRange: AgeRange) {
    return this.addOrUpdate(new HttpParams(), "age-range", ageRange, "Enregistré", "Erreur lors de l'enregistrement");
  }

}
