import { Component, Input, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { AnnouncementStatus } from 'src/app/modules/quotation/model/AnnouncementStatus';
import { DomiciliationStatus } from 'src/app/modules/quotation/model/DomiciliationStatus';
import { FormaliteStatus } from 'src/app/modules/quotation/model/FormaliteStatus';
import { AnnouncementStatusService } from 'src/app/modules/quotation/services/announcement.status.service';
import { DomiciliationStatusService } from 'src/app/modules/quotation/services/domiciliation-status.service';
import { FormaliteStatusService } from 'src/app/modules/quotation/services/formalite.status.service';
import { AppService } from 'src/app/services/app.service';
import { SimpleProvisionStatus } from '../../../../quotation/model/SimpleProvisonStatus';
import { SimpleProvisionStatusService } from '../../../../quotation/services/simple.provision.status.service';
import { IWorkflowElement } from '../../../model/IWorkflowElement';
import { GenericMultipleSelectComponent } from '../generic-select/generic-multiple-select.component';

@Component({
  selector: 'select-provision-stauts',
  templateUrl: './select-provision-stauts.component.html',
  styleUrls: ['./select-provision-stauts.component.css']
})
export class SelectProvisionStautsComponent extends GenericMultipleSelectComponent<IWorkflowElement<any>> implements OnInit {

  types: IWorkflowElement<any>[] = [] as Array<IWorkflowElement<any>>;

  /**
 * List of code to autoselect at loading
 */
  @Input() defaultCodesSelected: string[] | undefined;

  @Input() loadAnnouncement: boolean = true;
  @Input() loadDomiciliation: boolean = true;
  @Input() loadSimpleprovision: boolean = true;
  @Input() loadFormalite: boolean = true;

  announcementStatus: AnnouncementStatus[] = [] as Array<AnnouncementStatus>;
  formaliteStatus: FormaliteStatus[] = [] as Array<FormaliteStatus>;
  domiciliationStatus: DomiciliationStatus[] = [] as Array<DomiciliationStatus>;
  simpleProvisionStatus: SimpleProvisionStatus[] = [] as Array<SimpleProvisionStatus>;



  constructor(private formBuild: UntypedFormBuilder,
    private formaliteStatusService: FormaliteStatusService,
    private domiciliationStatusService: DomiciliationStatusService,
    private announcementStatusService: AnnouncementStatusService,
    private simpleProvisionStatusService: SimpleProvisionStatusService,
    private appService3: AppService
  ) {
    super(formBuild, appService3)
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
    if (this.loadFormalite)
      this.formaliteStatusService.getFormaliteStatus().subscribe(response => {
        this.formaliteStatus = response;
        this.types.push(...response);
      });
    if (this.loadDomiciliation)
      this.domiciliationStatusService.getDomiciliationStatus().subscribe(response => {
        this.domiciliationStatus = response;
        this.types.push(...response);
      });
    if (this.loadAnnouncement)
      this.announcementStatusService.getAnnouncementStatus().subscribe(response => {
        this.announcementStatus = response;
        this.types.push(...response);
      });
    if (this.loadSimpleprovision)
      this.simpleProvisionStatusService.getSimpleProvisionStatus().subscribe(response => {
        this.simpleProvisionStatus = response;
        this.types.push(...response);
      })
  }

  compareWithCode(o1: IWorkflowElement<any>, o2: IWorkflowElement<any>) {
    if (o1.code && o2.code)
      return o1.code == o2.code;
    return o1 == o2;
  }

}
