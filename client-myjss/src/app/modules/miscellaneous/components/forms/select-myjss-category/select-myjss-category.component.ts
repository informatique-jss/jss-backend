import { Component, Input, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { MyJssCategory } from '../../../../tools/model/MyJssCategory';
import { MyJssCategoryService } from '../../../../tools/services/myjss.category.service';
import { GenericSelectComponent } from '../generic-select/generic-select.component';

@Component({
  selector: 'select-myjss-category',
  templateUrl: './select-myjss-category.component.html',
  styleUrls: ['./select-myjss-category.component.css'],
  standalone: false
})
export class SelectMyJssCategoryComponent extends GenericSelectComponent<MyJssCategory> implements OnInit {


  types: MyJssCategory[] = [] as Array<MyJssCategory>;
  @Input() additionnalType: MyJssCategory = {} as MyJssCategory;

  constructor(private formBuild: UntypedFormBuilder, private myJssCategoryService: MyJssCategoryService) {
    super(formBuild)
  }

  initTypes(): void {
    this.myJssCategoryService.getMyJssCategories().subscribe(response => {
      this.types.push(this.additionnalType);
      this.types.push(...response);
      this.model = this.types[0];
    });
  }
}
