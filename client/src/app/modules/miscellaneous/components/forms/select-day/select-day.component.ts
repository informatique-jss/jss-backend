

import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { WeekDay } from '../../../model/WeekDay';
import { WeekDayService } from '../../../services/weekday.service';
import { GenericMultipleSelectComponent } from '../generic-select/generic-multiple-select.component';
import { AppService } from 'src/app/services/app.service';

@Component({
  selector: 'select-day',
  templateUrl: './select-day.component.html',
  styleUrls: ['./select-day.component.css']
})
export class SelectDayComponent extends GenericMultipleSelectComponent<WeekDay> implements OnInit {

  types: WeekDay[] = [] as Array<WeekDay>;

  constructor(private formBuild: UntypedFormBuilder, private weekDayService: WeekDayService, private appService3: AppService) {
    super(formBuild, appService3)
  }

  initTypes(): void {
    this.weekDayService.getWeekDays().subscribe(response => {
      this.types = response;
    })
  }

  displayLabel(object: any): string {
    return object ? (object.code + " - " + object.label) : '';
  }
}
