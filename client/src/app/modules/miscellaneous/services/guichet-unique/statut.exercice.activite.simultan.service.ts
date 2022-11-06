import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { StatutExerciceActiviteSimultan } from 'src/app/modules/quotation/model/guichet-unique/referentials/StatutExerciceActiviteSimultan';
import { AppRestService } from 'src/app/services/appRest.service';

@Injectable({
  providedIn: 'root'
})
export class StatutExerciceActiviteSimultanService extends AppRestService<StatutExerciceActiviteSimultan>{

  constructor(http: HttpClient) {
    super(http, 'quotation/guichet-unique');
  }

  getStatutExerciceActiviteSimultan() {
    return this.getList(new HttpParams(), 'statut-exercice-activite-simultan');
  }

}                        
