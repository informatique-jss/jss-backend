import { Component, Input, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { UserNoteService } from 'src/app/services/user.notes.service';
import { PaperSetType } from '../../../model/PaperSetType';
import { PaperSetTypeService } from '../../../services/paper.set.type.service';
import { GenericSelectComponent } from '../generic-select/generic-select.component';

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
    private paperSetTypeService: PaperSetTypeService,
    private userNoteService2: UserNoteService,) {
    super(formBuild, userNoteService2)
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
