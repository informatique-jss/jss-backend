import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { OptionParticuliereRegimeBenefi } from 'src/app/modules/quotation/model/guichet-unique/referentials/OptionParticuliereRegimeBenefi';
import { AppRestService } from 'src/app/services/appRest.service';

@Injectable({
  providedIn: 'root'
})
export class OptionParticuliereRegimeBenefiService extends AppRestService<OptionParticuliereRegimeBenefi>{

  constructor(http: HttpClient) {
    super(http, 'quotation/guichet-unique');
  }

  getOptionParticuliereRegimeBenefi() {
    return this.getListCached(new HttpParams(), 'option-particuliere-regime-benefi');
  }

}
