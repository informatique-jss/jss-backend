import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { AppRestService } from '../../main/services/appRest.service';
import { TooltipEntry } from '../model/TooltipEntry';

@Injectable({
  providedIn: 'root'
})
export class TooltipEntryService extends AppRestService<TooltipEntry> {

  tooltipEntries: TooltipEntry[] = []; // cache for tooltip entries

  constructor(http: HttpClient) {
    super(http, "miscellaneous");
  }

  getTooltipEntries() {
    if (this.tooltipEntries.length > 0)
      return new Observable<TooltipEntry[]>(observer => {
        observer.next(this.tooltipEntries);
        observer.complete;
      })
    else
      return new Observable<TooltipEntry[]>(observer => {
        this.getList(new HttpParams(), "tooltip-entries").subscribe(response => {
          this.tooltipEntries = response;
          observer.next(this.tooltipEntries);
          observer.complete;
        });
      })
  }
}