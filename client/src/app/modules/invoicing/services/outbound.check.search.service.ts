import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { OutboundCheckSearchResult } from '../model/OutboundCheckSearchResult';
import { OutboundCheckSearch } from '../model/OutboundCheckSearch';

@Injectable({
  providedIn: 'root'
})
export class OutboundCheckSearchResultService extends AppRestService<OutboundCheckSearchResult> {
  constructor(http: HttpClient) {
    super(http, "invoicing");
  }

  getOutboundChecks(outboundCheckSearch: OutboundCheckSearch) {
    return this.postList(new HttpParams(), "outbound-checks/search", outboundCheckSearch);
  }
}
