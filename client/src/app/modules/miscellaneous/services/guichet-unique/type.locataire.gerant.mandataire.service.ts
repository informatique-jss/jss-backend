import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { TypeLocataireGerantMandataire } from 'src/app/modules/quotation/model/guichet-unique/referentials/TypeLocataireGerantMandataire';
import { AppRestService } from 'src/app/services/appRest.service';

@Injectable({
  providedIn: 'root'
})
export class TypeLocataireGerantMandataireService extends AppRestService<TypeLocataireGerantMandataire>{

  constructor(http: HttpClient) {
    super(http, 'quotation/guichet-unique');
  }

  getTypeLocataireGerantMandataire() {
    return this.getList(new HttpParams(), 'type-locataire-gerant-mandataire');
  }

}                        
