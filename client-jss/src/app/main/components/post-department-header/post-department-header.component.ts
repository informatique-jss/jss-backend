import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { PublishingDepartment } from '../../model/PublishingDepartment';
import { DepartmentService } from '../../services/department.service';

@Component({
  selector: 'post-department-header',
  templateUrl: './post-department-header.component.html',
  styleUrls: ['./post-department-header.component.css'],
  standalone: false
})
export class PostDepartmentHeaderComponent implements OnInit {

  constructor(private departmentService: DepartmentService,
    private activeRoute: ActivatedRoute
  ) { }

  selectedDepartment: PublishingDepartment | undefined;

  ngOnInit() {
    let id = this.activeRoute.snapshot.params['id'];
    if (id)
      this.departmentService.getPublishingDepartmentById(id).subscribe(response => {
        if (response)
          this.selectedDepartment = response;
      });
  }
}
