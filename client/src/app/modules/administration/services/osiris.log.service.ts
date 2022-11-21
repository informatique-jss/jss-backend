import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { OsirisLog } from '../model/OsirisLog';

@Injectable({
  providedIn: 'root'
})
export class OsirisLogService extends AppRestService<OsirisLog>{

  constructor(http: HttpClient) {
    super(http, "miscellaneous");
  }

  getOsirisLogs(hideRead: boolean) {
    return this.getList(new HttpParams().set("hideRead", hideRead), "logs");
  }

  getLog(id: number) {
    return this.getById("log", id);
  }

  addOrUpdateOsirisLog(log: OsirisLog) {
    return this.addOrUpdate(new HttpParams(), "logs", log, "Enregistré", "Erreur lors de l'enregistrement");
  }

}
