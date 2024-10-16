import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from '../../../libs/appRest.service';
import { Affaire } from '../model/Affaire';

@Injectable({
  providedIn: 'root'
})
export class AffaireService extends AppRestService<Affaire> {

  constructor(http: HttpClient) {
    super(http, "quotation");
  }

  getAffaire(affaireId: number) {
    return this.getById("affaire", affaireId);
  }

  addOrUpdateAffaire(affaire: Affaire) {
    return this.addOrUpdate(new HttpParams(), "affaire", affaire);
  }

  searchAffairesForCurrentUser(searchText: string, page: number, sorter: string) {
    return this.getList(new HttpParams().set("page", page).set("sortBy", sorter).set("searchText", searchText), "affaire/search/current");
  }
}
