import { Component, Input, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { PaperSetType } from '../../../model/PaperSetType';
import { PaperSetTypeService } from '../../../services/paper.set.type.service';
import { GenericSelectComponent } from '../generic-select/generic-select.component';
import { AppService } from 'src/app/services/app.service';

@Component({
  selector: 'select-paper-set-type',
  templateUrl: './../generic-select/generic-select.component.html',
  styleUrls: ['./../generic-select/generic-select.component.html']
})

export class SelectPaperSetTypeComponent extends GenericSelectComponent<PaperSetType> implements OnInit {

  types: PaperSetType[] = [] as Array<PaperSetType>;

  /**
 * List of code to exclude
 */
  @Input() excludedPaperSetTypes: PaperSetType[] | undefined;

  constructor(private formBuild: UntypedFormBuilder,
    private paperSetTypeService: PaperSetTypeService, private appService3: AppService) {
    super(formBuild, appService3)
  }

  initTypes(): void {
    this.paperSetTypeService.getPaperSetTypes().subscribe(response => {
      if (this.excludedPaperSetTypes && response) {
        let outTypes = [];
        for (let type of response) {
          let found = false;
          for (let excludedType of this.excludedPaperSetTypes) {
            if (type.id == excludedType.id) {
              found = true;
              break;
            }
          }
          if (!found) {
            outTypes.push(type);
          }
        }
        this.types = outTypes;
      } else {
        this.types = response;
      }
    });
  }
}
