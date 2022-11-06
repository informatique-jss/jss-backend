import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { TypeDissolution } from 'src/app/modules/quotation/model/guichet-unique/referentials/TypeDissolution';
import { AppRestService } from 'src/app/services/appRest.service';

@Injectable({
  providedIn: 'root'
})
export class TypeDissolutionService extends AppRestService<TypeDissolution>{

  constructor(http: HttpClient) {
    super(http, 'quotation/guichet-unique');
  }

  getTypeDissolution() {
    return this.getList(new HttpParams(), 'type-dissolution');
  }

}                        
