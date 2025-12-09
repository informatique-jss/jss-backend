import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { AppRestService } from '../../main/services/appRest.service';
import { PagedContent } from '../../main/services/PagedContent';
import { Responsable } from '../../profile/model/Responsable';
import { ResponsableDto } from '../model/ResponsableDto';
import { ResponsableSearch } from '../model/ResponsableSearch';

@Injectable({
  providedIn: 'root'
})
export class ResponsableService extends AppRestService<Responsable> {

  constructor(http: HttpClient) {
    super(http, "tiers");
  }

  selectedResponsable: ResponsableDto[] = [];
  selectedResponsableUnique: ResponsableDto | undefined;
  selectedResponsableUniqueChange: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);
  selectedKpiStartDate: Date | undefined;
  selectedKpiEndDate: Date | undefined;

  getCurrentSelectedResponsable() {
    if (this.selectedResponsable.length == 0) {
      let toParse = localStorage.getItem("selected-responsable");
      if (toParse)
        this.selectedResponsable = JSON.parse(toParse);
    }
    return this.selectedResponsable;
  }

  setCurrentSelectedResponsable(tiers: ResponsableDto[]) {
    this.selectedResponsable = tiers;
    localStorage.setItem("selected-responsable", JSON.stringify(tiers));
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
    this.setCurrentSelectedResponsable([]);
  }

  getSelectedResponsableUnique() {
    return this.selectedResponsableUnique;
  }

  getSelectedResponsableUniqueChangeEvent() {
    return this.selectedResponsableUniqueChange.asObservable();
  }

  setSelectedResponsableUnique(tiers: ResponsableDto) {
    this.selectedResponsableUnique = tiers;
    this.selectedResponsableUniqueChange.next(true);
  }

  getResponsablesByTiers(idTiers: number) {
    return this.getList(new HttpParams().set("idTiers", idTiers), "responsables") as any as Observable<ResponsableDto[]>;
  }

  getResponsable(id: number) {
    return this.getById("responsable", id) as any as Observable<ResponsableDto>;
  }

  getResponsables(value: string): Observable<PagedContent<Responsable>> {
    return this.getPagedList(new HttpParams().set("searchedValue", value), "responsable/search");
  }

  searchResponsable(responsableSearch: ResponsableSearch) {
    return this.postList(new HttpParams(), "responsables/search", responsableSearch) as any as Observable<ResponsableDto[]>;
  }
}
