import { Component, Input, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { AppService } from 'src/app/services/app.service';
import { JssCategory } from '../../../model/JssCategory';
import { JssCategoryService } from '../../../services/jss.category.service';
import { GenericSelectComponent } from '../generic-select/generic-select.component';

@Component({
  selector: 'select-jss-category',
  templateUrl: './../generic-select/generic-select.component.html',
  styleUrls: ['./../generic-select/generic-select.component.css']
})
export class SelectJssCategoryComponent extends GenericSelectComponent<JssCategory> implements OnInit {

  @Input() types: JssCategory[] = [] as Array<JssCategory>;

  constructor(
    private formBuild: UntypedFormBuilder,
    private jssCategoryService: JssCategoryService,
    private appService3: AppService) {
    super(formBuild, appService3)
  }

  initTypes(): void {
    this.jssCategoryService.getJssCategories().subscribe(response => {
      this.types = response;
    })
  }
}
