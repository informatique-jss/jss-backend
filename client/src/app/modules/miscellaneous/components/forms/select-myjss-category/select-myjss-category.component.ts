import { Component, Input, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { AppService } from 'src/app/services/app.service';
import { MyJssCategory } from '../../../model/MyJssCategory';
import { MyJssCategoryService } from '../../../services/myjss.category.service';
import { GenericSelectComponent } from '../generic-select/generic-select.component';

@Component({
  selector: 'select-myjss-category',
  templateUrl: './../generic-select/generic-select.component.html',
  styleUrls: ['./../generic-select/generic-select.component.css']
})
export class SelectMyJssCategoryComponent extends GenericSelectComponent<MyJssCategory> implements OnInit {

  @Input() types: MyJssCategory[] = [] as Array<MyJssCategory>;

  constructor(private formBuild: UntypedFormBuilder,
    private myJssCategoryService: MyJssCategoryService, private appService3: AppService) {
    super(formBuild, appService3)
  }

  initTypes(): void {
    this.myJssCategoryService.getMyJssCategories().subscribe(response => {
      this.types = response;
    })
  }
}
