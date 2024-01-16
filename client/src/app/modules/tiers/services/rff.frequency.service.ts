import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { RffFrequency } from '../../tiers/model/RffFrequency';

@Injectable({
  providedIn: 'root'
})
export class RffFrequencyService extends AppRestService<RffFrequency>{

  constructor(http: HttpClient) {
    super(http, "tiers");
  }

  getRffFrequencies() {
    return this.getList(new HttpParams(), "rff-frequencies");
  }
  
   addOrUpdateRffFrequency(rffFrequency: RffFrequency) {
    return this.addOrUpdate(new HttpParams(), "rff-frequency", rffFrequency, "Enregistré", "Erreur lors de l'enregistrement");
  }

}
