import { Component, OnInit } from '@angular/core';
import { Meta, Title } from '@angular/platform-browser';
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
    private titleService: Title, private meta: Meta,
    private activeRoute: ActivatedRoute
  ) { }

  selectedDepartment: PublishingDepartment | undefined;

  ngOnInit() {
    this.titleService.setTitle("Tous nos articles - JSS");
    this.meta.updateTag({ name: 'description', content: "Retrouvez l'actualité juridique et économique. JSS analyse pour vous les dernières annonces, formalités et tendances locales." });
    let code = this.activeRoute.snapshot.params['code'];
    if (code)
      this.departmentService.getPublishingDepartmentByCode(code).subscribe(response => {
        if (response) {
          this.selectedDepartment = response;
          this.titleService.setTitle("Les articles en " + this.selectedDepartment!.name + " (" + this.selectedDepartment!.code + ") - JSS");
          this.meta.updateTag({ name: 'description', content: "Retrouvez l'actualité juridique et économique en " + this.selectedDepartment!.name + " (" + this.selectedDepartment!.code + "). JSS analyse pour vous les dernières annonces, formalités et tendances locales." });
        }
      });
  }

  updateSelectedDepartment(department: PublishingDepartment) {
    this.selectedDepartment = department;
  }
}
