import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { NgbDropdownModule, NgbNavModule, NgbTooltipModule } from '@ng-bootstrap/ng-bootstrap';
import { capitalizeName, getListMails, getListPhones } from '../../../../libs/FormatHelper';
import { SHARED_IMPORTS } from '../../../../libs/SharedImports';
import { AppService } from '../../../main/services/app.service';
import { ConstantService } from '../../../main/services/constant.service';
import { UserPreferenceService } from '../../../main/services/user.preference.service';
import { AvatarComponent } from '../../../miscellaneous/components/avatar/avatar.component';
import { GenericInputComponent } from '../../../miscellaneous/components/forms/generic-input/generic-input.component';
import { Responsable } from '../../../profile/model/Responsable';
import { LoginService } from '../../../profile/services/login.service';
import { BillingLabelType } from '../../model/BillingLabelType';
import { Document } from '../../model/Document';
import { DocumentType } from '../../model/DocumentType';
import { DocumentService } from '../../services/document.service';

@Component({
  selector: 'app-user-settings',
  templateUrl: './user-settings.component.html',
  styleUrls: ['./user-settings.component.css'],
  standalone: true,
  imports: [SHARED_IMPORTS, GenericInputComponent, AvatarComponent, NgbDropdownModule, NgbNavModule, NgbTooltipModule]
})
export class UserSettingsComponent implements OnInit {

  currentUser: Responsable | undefined;
  userScope: Responsable[] | undefined;
  idResponsable: number | undefined;

  documents: Document[] | undefined;
  documentForm!: FormGroup;

  documentTypeBilling!: DocumentType;
  documentTypeDigital!: DocumentType;
  documentTypePaper!: DocumentType;

  billingLabelTypeAffaire!: BillingLabelType;
  billingLabelTypeCustomer!: BillingLabelType;
  billingLabelTypeOther!: BillingLabelType;

  constructor(
    private loginService: LoginService,
    private formBuilder: FormBuilder,
    private constantService: ConstantService,
    private documentService: DocumentService,
    private appService: AppService,
    private userPreferenceService: UserPreferenceService,

  ) { }

  capitalizeName = capitalizeName;
  getListPhones = getListPhones;
  getListMails = getListMails;

  ngOnInit() {
    this.documentForm = this.formBuilder.group({});

    this.documentTypeBilling = this.constantService.getDocumentTypeBilling();
    this.documentTypeDigital = this.constantService.getDocumentTypeDigital();
    this.documentTypePaper = this.constantService.getDocumentTypePaper();

    this.billingLabelTypeAffaire = this.constantService.getBillingLabelTypeCodeAffaire();
    this.billingLabelTypeCustomer = this.constantService.getBillingLabelTypeCustomer();
    this.billingLabelTypeOther = this.constantService.getBillingLabelTypeOther();

    this.userScope = [];
    this.loginService.getCurrentUser().subscribe(response => {
      this.userScope = [response];
      this.changeCurrentUser(this.userScope[0]);
    });
  }

  changeCurrentUser(user: Responsable) {
    this.currentUser = user;
    if (this.currentUser)
      this.documentService.getDocumentForResponsable(this.currentUser.id).subscribe(response => {
        this.userPreferenceService.setUserSearchBookmark(user.id, "settings-current-responsable");
        if (response)
          this.documents = response;

        if (this.documents)
          for (let document of this.documents)
            if (document.documentType.id == this.documentTypeBilling.id && document.billingLabelType) {
              if (document.billingLabelType.id == this.billingLabelTypeAffaire.id)
                document.billingLabelType = this.billingLabelTypeAffaire;
              if (document.billingLabelType.id == this.billingLabelTypeCustomer.id)
                document.billingLabelType = this.billingLabelTypeCustomer;
              if (document.billingLabelType.id == this.billingLabelTypeOther.id)
                document.billingLabelType = this.billingLabelTypeOther;
            }
      })
  }

}
