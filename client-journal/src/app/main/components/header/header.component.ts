import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { MyJssCategory } from '../../model/MyJssCategory';
import { Page } from '../../model/Page';
import { PublishingDepartment } from '../../model/PublishingDepartment';
import { DepartmentService } from '../../services/department.service';
import { MyJssCategoryService } from '../../services/myjss.category.service';
import { PageService } from '../../services/page.service';

@Component({
  selector: 'main-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})
export class HeaderComponent implements OnInit {

  departments: PublishingDepartment[] = [];
  categories: MyJssCategory[] = [];
  pages: Page[] = [];

  constructor(
    private router: Router,
    private departmentService: DepartmentService,
    private myJssCategoryService: MyJssCategoryService,
    private pageService: PageService,
  ) { }

  ngOnInit() {
    this.departmentService.getAvailablePublishingDepartments().subscribe(departments => {
      this.departments = departments
    });
    this.myJssCategoryService.getAvailableMyJssCategories().subscribe(categories => {
      this.categories = categories
    });
    this.pageService.getAllPages().subscribe(pages => {
      this.pages = pages
    });
  }

  public getCurrentRoute = () => {
    return this.router.url;
  }

}
