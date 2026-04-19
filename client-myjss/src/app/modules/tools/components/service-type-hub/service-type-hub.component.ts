import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { environment } from '../../../../../environments/environment';
import { SERVICE_FIELD_TYPE_SELECT } from '../../../../libs/Constants';
import { SHARED_IMPORTS } from '../../../../libs/SharedImports';
import { MenuItem } from '../../../general/model/MenuItem';
import { AppService } from '../../../main/services/app.service';
import { PlatformService } from '../../../main/services/platform.service';
import { Attachment } from '../../../my-account/model/Attachment';
import { ServiceFieldType } from '../../../my-account/model/ServiceFieldType';
import { ServiceType } from '../../../my-account/model/ServiceType';
import { UploadAttachmentService } from '../../../my-account/services/upload.attachment.service';
import { Responsable } from '../../../profile/model/Responsable';
import { LoginService } from '../../../profile/services/login.service';
import { ServiceTypeService } from '../../../quotation/services/service.type.service';

@Component({
  selector: 'service-type-hub',
  templateUrl: './service-type-hub.component.html',
  styleUrls: ['./service-type-hub.component.css'],
  standalone: true,
  imports: [SHARED_IMPORTS,]
})
export class ServiceTypeHubComponent implements OnInit {

  constructor(private activeRoute: ActivatedRoute,
    private serviceTypeService: ServiceTypeService,
    private uploadAttachmentService: UploadAttachmentService,
    private loginService: LoginService,
    private appService: AppService,
    private platformService: PlatformService,
  ) { }
  serviceType: ServiceType | undefined;
  currentUser: Responsable | undefined;
  toolsItems!: MenuItem[];
  selectedTab: MenuItem | null = null;
  SERVICE_FIELD_TYPE_SELECT = SERVICE_FIELD_TYPE_SELECT;

  ngOnInit() {
    let id = this.activeRoute.snapshot.params['service-type-id'];
    if (id) {
      this.serviceTypeService.getServiceType(id).subscribe(response => {
        if (response)
          this.serviceType = response;
      });
    }
    this.loginService.getCurrentUser().subscribe(res => this.currentUser = res);
    this.toolsItems = this.appService.getAllToolsMenuItems();
  }


  activeSection: string = 'mandatoryDocuments';
  observer: IntersectionObserver | null = null;

  scrollTo(sectionId: string, event: Event) {
    event.preventDefault();
    document.getElementById(sectionId)?.scrollIntoView({
      behavior: 'smooth',
      block: 'start'
    });
  }

  hasMandatoryDocuments(): boolean {
    if (this.serviceType)
      return this.serviceType.assoServiceTypeDocuments && this.serviceType.assoServiceTypeDocuments.filter(s => s.isMandatory).length > 0;
    else return false;
  }

  hasNonMandatoryDocuments(): boolean {
    if (this.serviceType)
      return this.serviceType.assoServiceTypeDocuments && this.serviceType.assoServiceTypeDocuments.filter(s => !s.isMandatory).length > 0;
    else return false;
  }

  downloadAttachment(attachment: Attachment) {
    this.uploadAttachmentService.downloadAttachment(attachment);
  }

  getPossibleFieldsValuesForSelect(field: ServiceFieldType): string {
    if (field.dataType == SERVICE_FIELD_TYPE_SELECT) {
      return field.label.concat(' (', field.serviceFieldTypePossibleValues.map(item => item.value).join('/'), ')');
    }
    return "";
  }

  showSummary(): boolean {
    if (this.hasMandatoryDocuments() || this.hasNonMandatoryDocuments() || (this.serviceType?.assoServiceTypeFieldTypes && this.serviceType.assoServiceTypeFieldTypes.length > 0) ||
      this.currentUser || (this.serviceType && this.serviceType.descriptionText && this.serviceType.descriptionText.length > 0))
      return true;
    else return false;
  }

  shareByMail() {
    if (this.serviceType && this.platformService.isBrowser()) {
      let url = environment.frontendUrl + "tools/mandatory-documents/service-type/" + this.serviceType.id;
      window.open('mailto:?subject=Découvrez cette intéressante page sur MyJSS.FR&body=Bonjour,%0A%0AJe voulais vous partager ce lien :%0A%0A%0A' + url + '%0A%0ABonne lecture!', "_blank");
    }
  }
}
