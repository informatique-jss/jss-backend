import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { BatchCategory } from 'src/app/modules/monitoring/model/BatchCategory';
import { BatchCategoryService } from 'src/app/modules/monitoring/services/batch.category.service';
import { GenericSelectComponent } from '../generic-select/generic-select.component';
import { AppService } from 'src/app/services/app.service';

@Component({
  selector: 'select-batch-category',
  templateUrl: './../generic-select/generic-select.component.html',
  styleUrls: ['./../generic-select/generic-select.component.html']
})

export class SelectBatchCategoryComponent extends GenericSelectComponent<BatchCategory> implements OnInit {

  types: BatchCategory[] = [] as Array<BatchCategory>;

  constructor(private formBuild: UntypedFormBuilder,
    private batchCategoryService: BatchCategoryService, private appService3: AppService) {
    super(formBuild, appService3)
  }

  initTypes(): void {
    this.batchCategoryService.getBatchCategories().subscribe(response => {
      this.types = response.sort((a, b) => a.label.localeCompare(b.label));
    });
  }
}
