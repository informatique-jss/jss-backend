import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { AppService } from 'src/app/services/app.service';
import { PublishingDepartment } from '../../../model/PublishingDepartment';
import { PublishingDepartmentService } from '../../../services/publishing.department.service';
import { GenericSelectComponent } from '../generic-select/generic-select.component';

@Component({
  selector: 'select-publishing-department',
  templateUrl: './select-publishing-department.component.html',
  styleUrls: ['./select-publishing-department.component.css']
})
export class SelectPublishingDepartmentComponent extends GenericSelectComponent<PublishingDepartment> implements OnInit {

  types: PublishingDepartment[] = [] as Array<PublishingDepartment>;

  constructor(private formBuild: UntypedFormBuilder, private publishingDepartmentService: PublishingDepartmentService, private appService3: AppService) {
    super(formBuild, appService3)
  }

  initTypes(): void {
    this.publishingDepartmentService.getAvailablePublishingDepartments().subscribe(response => {
      this.types = response;
    })
  }

  displayLabel(object: any): string {
    return object ? (object.code + " - " + object.name) : '';
  }
}
