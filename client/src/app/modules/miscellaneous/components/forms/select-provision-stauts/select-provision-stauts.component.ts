import { Component, Input, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { AnnouncementStatus } from 'src/app/modules/quotation/model/AnnouncementStatus';
import { BodaccStatus } from 'src/app/modules/quotation/model/BodaccStatus';
import { DomiciliationStatus } from 'src/app/modules/quotation/model/DomiciliationStatus';
import { FormaliteStatus } from 'src/app/modules/quotation/model/FormaliteStatus';
import { AnnouncementStatusService } from 'src/app/modules/quotation/services/announcement.status.service';
import { BodaccStatusService } from 'src/app/modules/quotation/services/bodacc.status.service';
import { DomiciliationStatusService } from 'src/app/modules/quotation/services/domiciliation-status.service';
import { FormaliteStatusService } from 'src/app/modules/quotation/services/formalite.status.service';
import { UserNoteService } from 'src/app/services/user.notes.service';
import { IWorkflowElement } from '../../../model/IWorkflowElement';
import { GenericMultipleSelectComponent } from '../generic-select/generic-multiple-select.component';

@Component({
  selector: 'select-provision-stauts',
  templateUrl: './select-provision-stauts.component.html',
  styleUrls: ['./select-provision-stauts.component.css']
})
export class SelectProvisionStautsComponent extends GenericMultipleSelectComponent<IWorkflowElement> implements OnInit {

  types: IWorkflowElement[] = [] as Array<IWorkflowElement>;

  /**
 * List of code to autoselect at loading
 */
  @Input() defaultCodesSelected: string[] | undefined;

  announcementStatus: AnnouncementStatus[] = [] as Array<AnnouncementStatus>;
  formaliteStatus: FormaliteStatus[] = [] as Array<FormaliteStatus>;
  bodaccStatus: BodaccStatus[] = [] as Array<BodaccStatus>;
  domiciliationStatus: DomiciliationStatus[] = [] as Array<DomiciliationStatus>;

  constructor(private formBuild: UntypedFormBuilder,
    private formaliteStatusService: FormaliteStatusService,
    private bodaccStatusService: BodaccStatusService,
    private domiciliationStatusService: DomiciliationStatusService,
    private announcementStatusService: AnnouncementStatusService,
    private userNoteService2: UserNoteService,) {
    super(formBuild, userNoteService2)
  }

  selectDefault() {
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
  }

  initTypes(): void {
    this.formaliteStatusService.getFormaliteStatus().subscribe(response => {
      this.formaliteStatus = response;
      this.types.push(...response);
    });
    this.bodaccStatusService.getBodaccStatus().subscribe(response => {
      this.bodaccStatus = response;
      this.types.push(...response);
    });
    this.domiciliationStatusService.getDomiciliationStatus().subscribe(response => {
      this.domiciliationStatus = response;
      this.types.push(...response);
    });
    this.announcementStatusService.getAnnouncementStatus().subscribe(response => {
      this.announcementStatus = response;
      this.types.push(...response);
    });
  }
}
