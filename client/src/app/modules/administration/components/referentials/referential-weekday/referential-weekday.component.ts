import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import { WeekDay } from 'src/app/modules/miscellaneous/model/WeekDay';
import { WeekDayService } from 'src/app/modules/miscellaneous/services/weekday.service';
import { GenericReferentialComponent } from '../generic-referential/generic-referential-component';

@Component({
  selector: 'referential-weekday',
  templateUrl: './../generic-referential/generic-referential.component.html',
  styleUrls: ['./../generic-referential/generic-referential.component.css']
})
export class ReferentialWeekDayComponent extends GenericReferentialComponent<WeekDay> implements OnInit {
  constructor(private weekDayService: WeekDayService,
    private formBuilder2: FormBuilder) {
    super(formBuilder2);
  }

  getAddOrUpdateObservable(): Observable<WeekDay> {
    return this.weekDayService.addOrUpdateWeekDay(this.selectedEntity!);
  }
  getGetObservable(): Observable<WeekDay[]> {
    return this.weekDayService.getWeekDays();
  }
}
