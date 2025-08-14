import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { AppRestService } from '../../main/services/appRest.service';
import { PagedContent } from '../../main/services/PagedContent';
import { Responsable } from '../../profile/model/Responsable';

@Injectable({
  providedIn: 'root'
})
export class ResponsableService extends AppRestService<Responsable> {

  private selectedResponsablesSubject: BehaviorSubject<Responsable[]> = new BehaviorSubject<Responsable[]>([]);

  selectedResponsables: Set<Responsable> = new Set<Responsable>;

  constructor(http: HttpClient) {
    super(http, "tiers");
  }

  getSelectedResponsables(): Observable<Responsable[]> {
    return this.selectedResponsablesSubject;
  }

  // TODO : modifier en faisant un set et retirer add et remove
  addSelectedResponsables(responsables: Responsable[]) {
    for (let respo of responsables) {
      this.selectedResponsables.add(respo);
    }
    this.selectedResponsablesSubject.next(Array.from(this.selectedResponsables.values()));
  }

  removeSelectedResponsables(responsables: Responsable[]) {
    for (let respo of responsables) {
      this.selectedResponsables.delete(respo);
    }
    this.selectedResponsablesSubject.next(Array.from(this.selectedResponsables.values()));
  }

  getResponsablesByTiers(idTiers: number) {
    return this.getList(new HttpParams().set("idTiers", idTiers), "responsables");
  }

  getResponsable(id: number) {
    return this.getById("responsable", id);
  }

  getResponsables(value: string): Observable<PagedContent<Responsable>> {
    return this.getPagedList(new HttpParams().set("searchedValue", value), "responsable/search");
  }
}