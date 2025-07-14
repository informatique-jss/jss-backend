import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { SHARED_IMPORTS } from '../../../libs/SharedImports';
import { PublishingDepartment } from '../../model/PublishingDepartment';
import { DepartmentService } from '../../services/department.service';
import { DepartmentHubComponent } from '../department-hub/department-hub.component';

@Component({
  selector: 'post-department-header',
  templateUrl: './post-department-header.component.html',
  styleUrls: ['./post-department-header.component.css'],
  imports: [SHARED_IMPORTS, DepartmentHubComponent],
  standalone: true
})
export class PostDepartmentHeaderComponent implements OnInit {

  constructor(private departmentService: DepartmentService,
    private activeRoute: ActivatedRoute
  ) { }

  selectedDepartment: PublishingDepartment | undefined;

  ngOnInit() {
    let code = this.activeRoute.snapshot.params['code'];
    if (code)
      this.departmentService.getPublishingDepartmentByCode(code).subscribe(response => {
        if (response)
          this.selectedDepartment = response;
      });
  }

  updateSelectedDepartment(department: PublishingDepartment) {
    this.selectedDepartment = department;
  }
}
