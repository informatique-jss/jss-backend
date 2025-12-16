import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { AppRestService } from '../../main/services/appRest.service';
import { TiersDto } from '../model/TiersDto';
import { TiersSearch } from '../model/TiersSearch';

@Injectable({
  providedIn: 'root'
})
export class TiersService extends AppRestService<TiersDto> {

  constructor(http: HttpClient) {
    super(http, "tiers");
  }

  selectedTiers: TiersDto[] = [];
  selectedTiersUnique: TiersDto | undefined;
  selectedTiersUniqueChange: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);
  selectedKpiStartDate: Date | undefined;
  selectedKpiEndDate: Date | undefined;


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

  setSelectedKpiStartDate(date: Date) {
    this.selectedKpiStartDate = date;
  }

  getSelectedKpiStartDate() {
    return this.selectedKpiStartDate;
  }

  setSelectedKpiEndDate(date: Date) {
    this.selectedKpiEndDate = date;
  }

  getSelectedKpiEndDate() {
    return this.selectedKpiEndDate;
  }

  clearKpiSelection() {
    this.selectedKpiStartDate = undefined;
    this.selectedKpiEndDate = undefined;
    this.setCurrentSelectedTiers([]);
  }

  setSelectedTiersUnique(tiers: TiersDto) {
    this.selectedTiersUnique = tiers;
    this.selectedTiersUniqueChange.next(true);
  }

  getTiersById(id: number) {
    return this.getById("tiers/detail", id);
  }

  searchTiers(tiersSearch: TiersSearch) {
    return this.postList(new HttpParams(), "tiers/search", tiersSearch);
  }

  deleteTiers(tiers: TiersDto) {
    return this.get(new HttpParams().set("idTiers", tiers.id), "tiers/delete", "Tiers supprimé avec succès");
  }

  printTiersLabel(idTiers: number, idResponsable: number | undefined | null) {
    let params = new HttpParams().set("idTiers", idTiers);
    if (idResponsable)
      params = params.set("idResponsable", idResponsable);
    return this.get(params, "label/print", "Etiquette imprimée");
  }
}
