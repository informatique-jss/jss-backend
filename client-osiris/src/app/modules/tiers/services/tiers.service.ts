import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { AppRestService } from '../../main/services/appRest.service';
import { Tiers } from '../../profile/model/Tiers';

@Injectable({
  providedIn: 'root'
})
export class TiersService extends AppRestService<Tiers> {

  private selectedTiersIdSubject: BehaviorSubject<number> = new BehaviorSubject<number>({} as number);

  constructor(http: HttpClient) {
    super(http, "tiers");
  }

  getSelectedTiers(): Observable<number> {
    return this.selectedTiersIdSubject;
  }

  selectTiers(idTiers: number) {
    this.selectedTiersIdSubject.next(idTiers);
  }

  getTiersById(id: number) {
    return this.getById("tiers", id);
  }

  getTiers(searchText: string, page: number, pageSize: number) {
    return this.getPagedList(new HttpParams().set("searchText", searchText).set("page", page).set("pageSize", pageSize), "tiers");
  }

  deleteTiers(tiers: Tiers) {
    return this.get(new HttpParams().set("idTiers", tiers.id), "tiers/delete", "Tiers supprimé avec succès");
  }
}