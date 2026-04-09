import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from '../../main/services/appRest.service';
import { InformationBanner } from '../model/InformationBanner';

@Injectable({
    providedIn: 'root'
})
export class InformationBannerService extends AppRestService<InformationBanner> {

    constructor(http: HttpClient) {
        super(http, "miscellaneous");
    }
    getInformationbanner() {
        return this.get(new HttpParams(), 'information-banner');
    }
}