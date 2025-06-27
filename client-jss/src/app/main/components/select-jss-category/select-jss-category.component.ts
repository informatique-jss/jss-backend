import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { SHARED_IMPORTS } from '../../../libs/SharedImports';
import { JssCategory } from '../../model/JssCategory';
import { JssCategoryService } from '../../services/jss.category.service';
import { GenericSelectComponent } from '../generic-select/generic-select.component';

@Component({
  selector: 'select-jss-category',
  templateUrl: '../generic-select/generic-select.component.html',
  styleUrls: ['../generic-select/generic-select.component.css'],
  standalone: true,
  imports: [SHARED_IMPORTS]
})
export class SelectJssCategoryComponent extends GenericSelectComponent<JssCategory> implements OnInit {

  types: JssCategory[] = [] as Array<JssCategory>;

  constructor(private formBuild: UntypedFormBuilder,
    private jssCategoryService: JssCategoryService) {
    super(formBuild)
  }

  initTypes(): void {
    this.jssCategoryService.getAvailableJssCategories().subscribe(response => {
      this.types = response.sort((a, b) => a.name.localeCompare(b.name));
    });
  }
}
