import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { AppRestService } from '../../main/services/appRest.service';
import { Tiers } from '../../profile/model/Tiers';
import { TiersDto } from '../model/TiersDto';
import { TiersSearch } from '../model/TiersSearch';

@Injectable({
  providedIn: 'root'
})
export class TiersService extends AppRestService<Tiers> {

  constructor(http: HttpClient) {
    super(http, "tiers");
  }

  selectedTiers: TiersDto[] = [];
  selectedTiersUnique: TiersDto | undefined;
  selectedTiersUniqueChange: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);


  getCurrentSelectedTiers() {
    if (this.selectedTiers.length == 0) {
      let toParse = localStorage.getItem("selected-tiers");
      if (toParse)
        this.selectedTiers = JSON.parse(toParse);
    }
    return this.selectedTiers;
  }

  setCurrentSelectedTiers(tiers: TiersDto[]) {
    this.selectedTiers = tiers;
    localStorage.setItem("selected-tiers", JSON.stringify(tiers));
  }

  getSelectedTiersUnique() {
    return this.selectedTiersUnique;
  }

  getSelectedTiersUniqueChangeEvent() {
    return this.selectedTiersUniqueChange.asObservable();
  }

  setSelectedTiersUnique(tiers: TiersDto) {
    this.selectedTiersUnique = tiers;
    this.selectedTiersUniqueChange.next(true);
  }

  getTiersById(id: number) {
    return this.getById("tiers", id);
  }

  searchTiers(tiersSearch: TiersSearch) {
    return this.postList(new HttpParams(), "tiers/search", tiersSearch) as any as Observable<TiersDto[]>;
  }

  deleteTiers(tiers: Tiers) {
    return this.get(new HttpParams().set("idTiers", tiers.id), "tiers/delete", "Tiers supprimé avec succès");
  }
}
