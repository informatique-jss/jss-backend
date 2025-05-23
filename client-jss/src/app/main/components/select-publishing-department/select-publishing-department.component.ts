import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { PublishingDepartment } from '../../model/PublishingDepartment';
import { DepartmentService } from '../../services/department.service';
import { GenericSelectComponent } from '../generic-select/generic-select.component';

@Component({
  selector: 'select-publishing-department',
  templateUrl: '../generic-select/generic-select.component.html',
  styleUrls: ['../generic-select/generic-select.component.css'],
  standalone: false
})
export class SelectPublishingDepartmentComponent extends GenericSelectComponent<PublishingDepartment> implements OnInit {

  types: PublishingDepartment[] = [] as Array<PublishingDepartment>;

  constructor(private formBuild: UntypedFormBuilder,
    private publishingDepartmentService: DepartmentService) {
    super(formBuild)
  }

  initTypes(): void {
    this.publishingDepartmentService.getAvailablePublishingDepartments().subscribe(response => {
      this.types = response;
    });
  }

  override displayLabel(object: any): string {
    return object ? (object.name) : '';
  }
}
