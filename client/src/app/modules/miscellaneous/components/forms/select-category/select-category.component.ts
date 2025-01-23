import { Component, Input, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { AppService } from 'src/app/services/app.service';
import { Category } from '../../../model/Category';
import { CategoryService } from '../../../services/category.service';
import { GenericSelectComponent } from '../generic-select/generic-select.component';

@Component({
  selector: 'select-category',
  templateUrl: './../generic-select/generic-select.component.html',
  styleUrls: ['./../generic-select/generic-select.component.css']
})
export class SelectCategoryComponent extends GenericSelectComponent<Category> implements OnInit {

  @Input() types: Category[] = [] as Array<Category>;

  constructor(private formBuild: UntypedFormBuilder,
    private categoryService: CategoryService, private appService3: AppService) {
    super(formBuild, appService3)
  }

  initTypes(): void {
    this.categoryService.getCategories().subscribe(response => {
      this.types = response;
    })
  }
}
