import { Component, EventEmitter, Input, OnInit, Output, SimpleChanges, ViewChild } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { MatExpansionPanel } from '@angular/material/expansion';
import { CustomErrorStateMatcher } from 'src/app/app.component';
import { compareWithId } from 'src/app/libs/CompareHelper';
import { PROVISION_SCREEN_TYPE_ANNOUNCEMENT, PROVISION_SCREEN_TYPE_BODACC, PROVISION_SCREEN_TYPE_DOMICILIATION, PROVISION_SCREEN_TYPE_STANDARD } from 'src/app/libs/Constants';
import { Employee } from 'src/app/modules/profile/model/Employee';
import { Affaire } from '../../model/Affaire';
import { Announcement } from '../../model/Announcement';
import { Bodacc } from '../../model/Bodacc';
import { Domiciliation } from '../../model/Domiciliation';
import { Provision } from '../../model/Provision';
import { ProvisionFamilyType } from '../../model/ProvisionFamilyType';
import { ProvisionType } from '../../model/ProvisionType';
import { ProvisionFamilyTypeService } from '../../services/provision.family.type.service';
import { ProvisionService } from '../../services/provision.service';
import { ProvisionTypeService } from '../../services/provision.type.service';
import { AnnouncementComponent } from '../announcement/announcement.component';
import { BodaccMainComponent } from '../bodacc-main/bodacc-main.component';
import { DomiciliationComponent } from '../domiciliation/domiciliation.component';


@Component({
  selector: 'provision-item',
  templateUrl: './provision-item.component.html',
  styleUrls: ['./provision-item.component.css'],
  viewProviders: [MatExpansionPanel]
})
export class ProvisionItemComponent implements OnInit {

  matcher: CustomErrorStateMatcher = new CustomErrorStateMatcher();
  @Input() provision: Provision = {} as Provision;
  @Input() affaire: Affaire = {} as Affaire;
  @Input() editMode: boolean = false;
  @Input() instanceOfCustomerOrder: boolean = false;
  @Input() isStatusOpen: boolean = true;
  @Output() selectedProvisionTypeChange: EventEmitter<void> = new EventEmitter<void>();
  @ViewChild(DomiciliationComponent) domiciliationComponent: DomiciliationComponent | undefined;
  @ViewChild(AnnouncementComponent) announcementComponent: AnnouncementComponent | undefined;
  @ViewChild(BodaccMainComponent) bodaccComponent: BodaccMainComponent | undefined;

  provisionFamilyTypes: ProvisionFamilyType[] = [] as Array<ProvisionFamilyType>;
  provisionTypes: ProvisionType[] = [] as Array<ProvisionType>;

  PROVISION_SCREEN_TYPE_BODACC = PROVISION_SCREEN_TYPE_BODACC;
  PROVISION_SCREEN_TYPE_DOMICILIATION = PROVISION_SCREEN_TYPE_DOMICILIATION;
  PROVISION_SCREEN_TYPE_ANNOUNCEMENT = PROVISION_SCREEN_TYPE_ANNOUNCEMENT;
  PROVISION_SCREEN_TYPE_STANDARD = PROVISION_SCREEN_TYPE_STANDARD;

  constructor(private formBuilder: UntypedFormBuilder,
    protected provisionFamilyTypeService: ProvisionFamilyTypeService,
    protected provisionTypeService: ProvisionTypeService,
    protected provisionService: ProvisionService,
  ) { }

  ngOnInit() {
    this.provisionFamilyTypeService.getProvisionFamilyTypes().subscribe(response => {
      this.provisionFamilyTypes = response;
    })
    this.provisionTypeService.getProvisionTypes().subscribe(response => {
      this.provisionTypes = response;
    })
  }

  ngOnChanges(changes: SimpleChanges) {
    this.provisionItemForm.markAllAsTouched();
  }

  updateAssignedToForProvision(employee: Employee, provision: Provision) {
    this.provisionService.updateAssignedToForProvision(provision, employee).subscribe(response => {
    });
  }

  getFormStatus(): boolean {
    let status = true;
    if (this.domiciliationComponent)
      status = status && this.domiciliationComponent.getFormStatus();

    if (this.announcementComponent)
      status = status && this.announcementComponent.getFormStatus();

    if (this.bodaccComponent)
      status = status && this.bodaccComponent.getFormStatus();
    return status && (this.provisionItemForm.status == "DISABLED" || this.provisionItemForm.valid);
  }

  provisionItemForm = this.formBuilder.group({
  });

  changeProvisionType() {
    if (this.provision.provisionFamilyType) {
      for (let provisionType of this.provisionTypes) {
        if (provisionType.label == this.provision.provisionFamilyType.label)
          this.provision.provisionType = provisionType;
      }
    }

    if (!this.provision.provisionFamilyType || !this.provision.provisionType) {
      this.provision.announcement = undefined;
      this.provision.domiciliation = undefined;
      this.provision.bodacc = undefined;
      this.selectedProvisionTypeChange.emit();
      return;
    }

    if (this.provision.provisionType.provisionScreenType.code != PROVISION_SCREEN_TYPE_DOMICILIATION) {
      this.provision.domiciliation = undefined;
    } else if (!this.provision.domiciliation) {
      this.provision.domiciliation = {} as Domiciliation;
    }

    if (this.provision.provisionType.provisionScreenType.code != PROVISION_SCREEN_TYPE_ANNOUNCEMENT) {
      this.provision.announcement = undefined;
    } else if (!this.provision.announcement) {
      this.provision.announcement = {} as Announcement;
    }

    if (this.provision.provisionType.provisionScreenType.code != PROVISION_SCREEN_TYPE_BODACC) {
      this.provision.bodacc = undefined;
    } else if (!this.provision.bodacc) {
      this.provision.bodacc = {} as Bodacc;
    }
    this.selectedProvisionTypeChange.emit();
  }

  noticeChange() {
    this.selectedProvisionTypeChange.emit();
  }

  compareWithId = compareWithId;

}
