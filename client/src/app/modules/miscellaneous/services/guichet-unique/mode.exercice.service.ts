import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ModeExercice } from 'src/app/modules/quotation/model/guichet-unique/referentials/ModeExercice';
import { AppRestService } from 'src/app/services/appRest.service';

@Injectable({
  providedIn: 'root'
})
export class ModeExerciceService extends AppRestService<ModeExercice>{

  constructor(http: HttpClient) {
    super(http, 'quotation/guichet-unique');
  }

  getModeExercice() {
    return this.getList(new HttpParams(), 'mode-exercice');
  }

}                        
