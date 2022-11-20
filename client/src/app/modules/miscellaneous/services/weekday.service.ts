import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { WeekDay } from '../../miscellaneous/model/WeekDay';

@Injectable({
  providedIn: 'root'
})
export class WeekDayService extends AppRestService<WeekDay>{

  constructor(http: HttpClient) {
    super(http, "miscellaneous");
  }

  getWeekDays() {
    return this.getListCached(new HttpParams(), "weekdays");
  }

  addOrUpdateWeekDay(weekDay: WeekDay) {
    this.clearListCache(new HttpParams(), "weekdays");
    return this.addOrUpdate(new HttpParams(), "weekday", weekDay, "Enregistré", "Erreur lors de l'enregistrement");
  }

}
