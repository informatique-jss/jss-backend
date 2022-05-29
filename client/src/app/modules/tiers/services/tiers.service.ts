import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/appRest.service';
import { Responsable } from '../model/Responsable';
import { Tiers } from '../model/Tiers';
import { TiersType } from '../model/TiersType';

@Injectable({
  providedIn: 'root'
})
export class TiersService extends AppRestService<Tiers>{

  tiers: Tiers | null = null;
  responsable: Responsable | null = null;

  constructor(http: HttpClient) {
    super(http, "tiers");
  }

  getTiers(id: number) {
    return this.getById("tiers", id);
  }

  getTiersByResponsable(idTiers: number) {
    return this.get(new HttpParams().set("idResponsable", idTiers), "responsable");
  }

  addOrUpdateTiers(tiers: Tiers) {
    return this.addOrUpdate(new HttpParams(), "tiers", tiers, "Tiers enregistré", "Erreur lors de l'enregistrement du tiers");
  }

  setCurrentViewedTiers(tiers: Tiers) {
    this.tiers = tiers;
  }

  getCurrentViewedTiers(): Tiers | null {
    return this.tiers;
  }

  setCurrentViewedResponsable(responsable: Responsable | null) {
    this.responsable = responsable;
  }

  getCurrentViewedResponsable(): Responsable | null {
    return this.responsable;
  }
}
