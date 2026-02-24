import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { TooltipEntry } from '../../miscellaneous/model/TooltipEntry';

@Injectable({
  providedIn: 'root'
})
export class TooltipEntryService extends AppRestService<TooltipEntry> {

  constructor(http: HttpClient) {
    super(http, "miscellaneous");
  }

  getTooltipEntries() {
    return this.getList(new HttpParams(), "tooltip-entries");
  }

  addOrUpdateTooltipEntry(tooltipEntry: TooltipEntry) {
    return this.addOrUpdate(new HttpParams(), "tooltip-entry", tooltipEntry, "Enregistré", "Erreur lors de l'enregistrement");
  }
}