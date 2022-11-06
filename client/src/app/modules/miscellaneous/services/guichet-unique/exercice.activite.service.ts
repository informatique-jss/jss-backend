import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ExerciceActivite } from 'src/app/modules/quotation/model/guichet-unique/referentials/ExerciceActivite';
import { AppRestService } from 'src/app/services/appRest.service';

@Injectable({
  providedIn: 'root'
})
export class ExerciceActiviteService extends AppRestService<ExerciceActivite>{

  constructor(http: HttpClient) {
    super(http, 'quotation/guichet-unique');
  }

  getExerciceActivite() {
    return this.getList(new HttpParams(), 'exercice-activite');
  }

}                        
