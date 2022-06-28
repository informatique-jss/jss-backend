

import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { WeekDay } from '../../../model/WeekDay';
import { WeekDayService } from '../../../services/weekday.service';
import { GenericMultipleSelectComponent } from '../generic-select/generic-multiple-select.component';

@Component({
  selector: 'select-day',
  templateUrl: './select-day.component.html',
  styleUrls: ['./select-day.component.css']
})
export class SelectDayComponent extends GenericMultipleSelectComponent<WeekDay> implements OnInit {

  types: WeekDay[] = [] as Array<WeekDay>;

  constructor(private changeDetectorRef: ChangeDetectorRef,
    private formBuild: UntypedFormBuilder,
    private weekDayService: WeekDayService) {
    super(changeDetectorRef, formBuild);
  }

  initTypes(): void {
    this.weekDayService.getWeekDays().subscribe(response => {
      this.types = response;
    })
  }
}
