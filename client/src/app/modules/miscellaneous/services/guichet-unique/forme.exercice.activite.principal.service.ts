import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { FormeExerciceActivitePrincipal } from 'src/app/modules/quotation/model/guichet-unique/referentials/FormeExerciceActivitePrincipal';
import { AppRestService } from 'src/app/services/appRest.service';

@Injectable({
  providedIn: 'root'
})
export class FormeExerciceActivitePrincipalService extends AppRestService<FormeExerciceActivitePrincipal>{

  constructor(http: HttpClient) {
    super(http, 'quotation/guichet-unique');
  }

  getFormeExerciceActivitePrincipal() {
    return this.getList(new HttpParams(), 'forme-exercice-activite-principal');
  }

}                        
