import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { SHARED_IMPORTS } from '../../../libs/SharedImports';
import { PublishingDepartment } from '../../model/PublishingDepartment';
import { DepartmentService } from '../../services/department.service';
import { GenericSelectComponent } from '../generic-select/generic-select.component';

@Component({
  selector: 'select-publishing-department',
  templateUrl: '../generic-select/generic-select.component.html',
  styleUrls: ['../generic-select/generic-select.component.css'],
  standalone: true,
  imports: [SHARED_IMPORTS]
})
export class SelectPublishingDepartmentComponent extends GenericSelectComponent<PublishingDepartment> implements OnInit {

  types: PublishingDepartment[] = [] as Array<PublishingDepartment>;

  constructor(private formBuild: UntypedFormBuilder,
    private publishingDepartmentService: DepartmentService) {
    super(formBuild)
  }

  initTypes(): void {
    this.publishingDepartmentService.getAvailablePublishingDepartments().subscribe(response => {
      this.types = response.sort((a, b) => a.code.localeCompare(b.code));
    });
  }

  override displayLabel(object: any): string {
    return (object)
      ? ((object.code !== null && object.code !== undefined && object.code.toString().trim() !== '' && object.code.toString() !== '0')
        ? (object.code + " - " + object.name)
        : object.name)
      : '';
  }
}
