import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from '../../main/services/appRest.service';
import { TooltipEntry } from '../model/TooltipEntry';

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
}