import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/appRest.service';
import { WeekDay } from '../../miscellaneous/model/WeekDay';

@Injectable({
  providedIn: 'root'
})
export class WeekDayService extends AppRestService<WeekDay>{

  constructor(http: HttpClient) {
    super(http, "miscellaneous");
  }

  getWeekDays() {
    return this.getList(new HttpParams(), "weekdays");
  }

}
