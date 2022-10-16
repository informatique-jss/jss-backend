import { ChangeDetectorRef, Component, Input, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { AffaireStatus } from 'src/app/modules/quotation/model/AffaireStatus';
import { AffaireStatusService } from 'src/app/modules/quotation/services/affaire.status.service';
import { GenericMultipleSelectComponent } from '../generic-select/generic-multiple-select.component';

@Component({
  selector: 'select-affaire-status',
  templateUrl: './select-affaire-status.component.html',
  styleUrls: ['./select-affaire-status.component.css']
})
export class SelectAffaireStatusComponent extends GenericMultipleSelectComponent<AffaireStatus> implements OnInit {

  types: AffaireStatus[] = [] as Array<AffaireStatus>;

  /**
 * List of code to autoselect at loading
 */
  @Input() defaultCodesSelected: string[] | undefined;

  constructor(private changeDetectorRef: ChangeDetectorRef,
    private affaireStatusService: AffaireStatusService,
    private formBuild: UntypedFormBuilder) {
    super(changeDetectorRef, formBuild);
  }

  initTypes(): void {
    this.affaireStatusService.getAffaireStatus().subscribe(response => {
      this.types = response;
      if (this.defaultCodesSelected) {
        this.model = [];
        for (let type of this.types) {
          for (let defaultCode of this.defaultCodesSelected) {
            if (type.code == defaultCode) {
              this.model.push(type);
            }
          }
        }
        this.modelChange.emit(this.model);
        this.selectionChange.emit(this.model);
      }
    });
  }
}
