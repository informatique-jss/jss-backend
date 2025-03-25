import { Component, Input, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { AppService } from '../../../../../libs/app.service';
import { MyJssCategory } from '../../../../tools/model/MyJssCategory';
import { MyJssCategoryService } from '../../../../tools/services/myjss.category.service';
import { GenericSelectComponent } from '../generic-select/generic-select.component';

@Component({
  selector: 'select-myjss-category',
  templateUrl: '../generic-select/generic-select.component.html',
  styleUrls: ['../generic-select/generic-select.component.css']
})
export class SelectMyJssCategoryComponent extends GenericSelectComponent<MyJssCategory> implements OnInit {
  @Input() types: MyJssCategory[] = [] as Array<MyJssCategory>;
  @Input() additionnalType: MyJssCategory = {} as MyJssCategory;

  constructor(private formBuild: UntypedFormBuilder,
    private myJssCategoryService: MyJssCategoryService, private appService3: AppService) {
    super(formBuild, appService3)
  }

  initTypes(): void {
    this.myJssCategoryService.getMyJssCategories().subscribe(response => {
      this.types.push(this.additionnalType);
      this.types.push(...response);
    })
  }
}
