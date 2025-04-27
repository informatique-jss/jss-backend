import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { Dictionnary } from 'src/app/libs/Dictionnary';
import { AppService } from 'src/app/services/app.service';
import { NotificationService } from '../../../services/notification.service';
import { GenericMultipleSelectComponent } from '../generic-select/generic-multiple-select.component';

@Component({
  selector: 'select-notification-types',
  templateUrl: './../generic-select/generic-multiple-select.component.html',
  styleUrls: ['./../generic-select/generic-select.component.html'],
  standalone: false
})

export class SelectNotificationTypesComponent extends GenericMultipleSelectComponent<string> implements OnInit {

  types: string[] = [] as Array<string>;

  constructor(private formBuild: UntypedFormBuilder,
    private notificationService: NotificationService,
    private appService3: AppService
  ) {
    super(formBuild, appService3)
  }

  initTypes(): void {
    this.notificationService.getAllNotificationTypes().subscribe(response => {
      this.types = response.sort((a: string, b: string) => a.localeCompare(b));
    });
  }

  displayLabel(object: any): string {
    let dic = Dictionnary as any;
    if (dic[object])
      return dic[object];
    return object;
  }

}
