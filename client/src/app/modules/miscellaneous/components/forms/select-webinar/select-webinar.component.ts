import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { Webinar } from 'src/app/modules/crm/model/Webinar';
import { WebinarService } from 'src/app/modules/crm/services/webinar.service';
import { AppService } from 'src/app/services/app.service';
import { GenericSelectComponent } from '../generic-select/generic-select.component';

@Component({
  selector: 'select-webinar',
  templateUrl: './../generic-select/generic-select.component.html',
  styleUrls: ['./../generic-select/generic-select.component.css']
})
export class SelectWebinarComponent extends GenericSelectComponent<Webinar> implements OnInit {

  types: Webinar[] = [] as Array<Webinar>;

  constructor(private formBuild: UntypedFormBuilder, private webinarService: WebinarService, private appService3: AppService) {
    super(formBuild, appService3)
  }

  initTypes(): void {
    this.webinarService.getWebinars().subscribe(response => {
      this.types = response.sort((a, b) => a.label.localeCompare(b.label));
    })
  }

  displayLabel(object: Webinar): string {
    if (object)
      return object.label;
    return "";
  }

}
