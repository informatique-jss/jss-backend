import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/appRest.service';
import { Affaire } from '../../quotation/model/Affaire';

@Injectable({
  providedIn: 'root'
})
export class AffaireService extends AppRestService<Affaire>{

  constructor(http: HttpClient) {
    super(http, "quotation");
  }

  getAffaire(affaireId: number) {
    return this.getById("affaire", affaireId);
  }
}
