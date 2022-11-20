import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { FormeExercice } from 'src/app/modules/quotation/model/guichet-unique/referentials/FormeExercice';
import { AppRestService } from 'src/app/services/appRest.service';

@Injectable({
  providedIn: 'root'
})
export class FormeExerciceService extends AppRestService<FormeExercice>{

  constructor(http: HttpClient) {
    super(http, 'quotation/guichet-unique');
  }

  getFormeExercice() {
    return this.getListCached(new HttpParams(), 'forme-exercice');
  }

}
