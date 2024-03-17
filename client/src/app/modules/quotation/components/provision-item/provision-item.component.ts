import { Component, EventEmitter, Input, OnInit, Output, SimpleChanges, ViewChild } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { MatExpansionPanel } from '@angular/material/expansion';
import { compareWithId } from 'src/app/libs/CompareHelper';
import { PROVISION_SCREEN_TYPE_ANNOUNCEMENT, PROVISION_SCREEN_TYPE_DOMICILIATION, PROVISION_SCREEN_TYPE_FORMALITE, PROVISION_SCREEN_TYPE_STANDARD } from 'src/app/libs/Constants';
import { Employee } from 'src/app/modules/profile/model/Employee';
import { getDocument } from '../../../../libs/DocumentHelper';
import { ConstantService } from '../../../miscellaneous/services/constant.service';
import { Affaire } from '../../model/Affaire';
import { Announcement } from '../../model/Announcement';
import { Confrere } from '../../model/Confrere';
import { Domiciliation } from '../../model/Domiciliation';
import { Formalite } from '../../model/Formalite';
import { IQuotation } from '../../model/IQuotation';
import { Provision } from '../../model/Provision';
import { ProvisionFamilyType } from '../../model/ProvisionFamilyType';
import { ProvisionType } from '../../model/ProvisionType';
import { SimpleProvision } from '../../model/SimpleProvision';
import { ProvisionFamilyTypeService } from '../../services/provision.family.type.service';
import { ProvisionService } from '../../services/provision.service';
import { ProvisionTypeService } from '../../services/provision.type.service';
import { AnnouncementComponent } from '../announcement/announcement.component';
import { DomiciliationComponent } from '../domiciliation/domiciliation.component';
import { FormaliteComponent } from '../formalite/formalite.component';
import { SimpleProvisionComponent } from '../simple-provision/simple-provision.component';


@Component({
  selector: 'provision-item',
  templateUrl: './provision-item.component.html',
  styleUrls: ['./provision-item.component.css'],
  viewProviders: [MatExpansionPanel]
})
export class ProvisionItemComponent implements OnInit {


  @Input() provision: Provision | undefined;
  @Output() provisionChange: EventEmitter<Provision> = new EventEmitter<Provision>();
  @Input() affaire: Affaire = {} as Affaire;
  @Input() editMode: boolean = false;
  @Input() instanceOfCustomerOrder: boolean = false;
  @Input() isStatusOpen: boolean = true;
  @Input() quotation: IQuotation | undefined;
  @Output() selectedProvisionTypeChange: EventEmitter<void> = new EventEmitter<void>();
  @ViewChild(DomiciliationComponent) domiciliationComponent: DomiciliationComponent | undefined;
  @ViewChild(AnnouncementComponent) announcementComponent: AnnouncementComponent | undefined;
  @ViewChild(FormaliteComponent) formaliteComponent: FormaliteComponent | undefined;
  @ViewChild(SimpleProvisionComponent) simpleProvisionComponent: SimpleProvisionComponent | undefined;

  provisionFamilyTypes: ProvisionFamilyType[] = [] as Array<ProvisionFamilyType>;
  provisionTypes: ProvisionType[] = [] as Array<ProvisionType>;

  announcementConfrere: Confrere | undefined;

  PROVISION_SCREEN_TYPE_DOMICILIATION = PROVISION_SCREEN_TYPE_DOMICILIATION;
  PROVISION_SCREEN_TYPE_ANNOUNCEMENT = PROVISION_SCREEN_TYPE_ANNOUNCEMENT;
  PROVISION_SCREEN_TYPE_FORMALITE = PROVISION_SCREEN_TYPE_FORMALITE;
  PROVISION_SCREEN_TYPE_STANDARD = PROVISION_SCREEN_TYPE_STANDARD;

  constructor(private formBuilder: UntypedFormBuilder,
    protected provisionFamilyTypeService: ProvisionFamilyTypeService,
    protected provisionTypeService: ProvisionTypeService,
    protected provisionService: ProvisionService,
    private constantService: ConstantService,
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

    if (changes.provision)
      this.changeProvisionType();
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

    if (this.formaliteComponent)
      status = status && this.formaliteComponent.getFormStatus();

    if (this.simpleProvisionComponent)
      status = status && this.simpleProvisionComponent.getFormStatus();

    return status && (this.provisionItemForm.valid);
  }

  provisionItemForm = this.formBuilder.group({
  });

  changeProvisionFamilyType() {
    if (this.provision) {
      if (this.provision.provisionType && this.provision.provisionFamilyType && this.provision.provisionType.provisionFamilyType.id != this.provision.provisionFamilyType.id)
        this.provision.provisionType = undefined!;
    }
  }

  changeProvisionType() {
    if (this.provision) {
      if (!this.provision.provisionFamilyType && !this.provision.provisionType) {
        this.provision.announcement = undefined;
        this.provision.domiciliation = undefined;
        this.provision.formalite = undefined;
        this.provision.simpleProvision = undefined;
        this.selectedProvisionTypeChange.emit();
        return;
      }

      if (!this.provision.provisionType)
        return;

      if (this.provision.announcement != undefined && this.provision.announcement.actuLegaleId != undefined && this.provision.announcement.actuLegaleId > 0)
        return;

      if (this.provision.provisionType.provisionScreenType.code != PROVISION_SCREEN_TYPE_DOMICILIATION) {
        this.provision.domiciliation = undefined;
      } else if (!this.provision.domiciliation) {
        this.provision.domiciliation = {} as Domiciliation;
      }

      if (this.provision.provisionType.provisionScreenType.code != PROVISION_SCREEN_TYPE_ANNOUNCEMENT) {
        this.provision.announcement = undefined;
      } else if (!this.provision.announcement) {
        this.provision.announcement = {} as Announcement;
        this.provision.announcement.documents = [];
        let paperDocument = getDocument(this.constantService.getDocumentTypePaper(), this.quotation!);
        paperDocument.id = undefined;
        this.provision.announcement.documents.push(paperDocument);
      }

      if (this.provision.provisionType.provisionScreenType.code != PROVISION_SCREEN_TYPE_FORMALITE) {
        this.provision.formalite = undefined;
      } else if (!this.provision.formalite) {
        this.provision.formalite = {} as Formalite;
        if (this.provision.provisionType.defaultCompetentAuthorityServiceProvider && !this.provision.formalite.competentAuthorityServiceProvider)
          this.provision.formalite.competentAuthorityServiceProvider = this.provision.provisionType.defaultCompetentAuthorityServiceProvider;

      }

      if (this.provision.provisionType.provisionScreenType.code != PROVISION_SCREEN_TYPE_STANDARD) {
        this.provision.simpleProvision = undefined;
      } else if (!this.provision.simpleProvision) {
        this.provision.simpleProvision = {} as SimpleProvision;
      }

      this.selectedProvisionTypeChange.emit();
    }
  }

  noticeChange() {
    this.selectedProvisionTypeChange.emit();
  }

  compareWithId = compareWithId;

}
