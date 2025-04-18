import { Component, OnInit } from '@angular/core';
import { PublishingDepartment } from '../../model/PublishingDepartment';
import { DepartmentService } from '../../services/department.service';

@Component({
  selector: 'post-idf-header',
  templateUrl: './post-idf-header.component.html',
  styleUrls: ['./post-idf-header.component.css'],
  standalone: false
})
export class PostIdfHeaderComponent implements OnInit {

  constructor(private departmentService: DepartmentService,
  ) { }

  selectedDepartment: PublishingDepartment | undefined;

  ngOnInit() {
    this.departmentService.getAvailablePublishingDepartments().subscribe(response => {
      if (response)
        this.selectedDepartment = response[0];
    });
  }

}
