import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { NgbDropdownModule, NgbNavModule } from '@ng-bootstrap/ng-bootstrap';
import { capitalizeName, getListMails, getListPhones } from '../../../../libs/FormatHelper';
import { SHARED_IMPORTS } from '../../../../libs/SharedImports';
import { AppService } from '../../../main/services/app.service';
import { ConstantService } from '../../../main/services/constant.service';
import { UserPreferenceService } from '../../../main/services/user.preference.service';
import { AvatarComponent } from '../../../miscellaneous/components/avatar/avatar.component';
import { GenericInputComponent } from '../../../miscellaneous/components/forms/generic-input/generic-input.component';
import { Responsable } from '../../../profile/model/Responsable';
import { LoginService } from '../../../profile/services/login.service';
import { UserScopeService } from '../../../profile/services/user.scope.service';
import { BillingLabelType } from '../../model/BillingLabelType';
import { Document } from '../../model/Document';
import { DocumentType } from '../../model/DocumentType';
import { DocumentService } from '../../services/document.service';

@Component({
  selector: 'app-user-settings',
  templateUrl: './user-settings.component.html',
  styleUrls: ['./user-settings.component.css'],
  standalone: true,
  imports: [SHARED_IMPORTS, GenericInputComponent, AvatarComponent, NgbDropdownModule, NgbNavModule]
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
    private userScopeService: UserScopeService,
    private documentService: DocumentService,
    private appService: AppService,
    private activatedRoute: ActivatedRoute,
    private userPreferenceService: UserPreferenceService
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

    this.idResponsable = this.activatedRoute.snapshot.params['idResponsable'];
    this.userScopeService.getUserScope().subscribe(response => {
      this.userScope = [];
      if (response)
        for (let scope of response)
          this.userScope.push(scope.responsableViewed);

      this.loginService.getCurrentUser().subscribe(response => {
        if (this.userScope)
          for (let scope of this.userScope)
            if (!this.idResponsable) {
              let bookmark = this.userPreferenceService.getUserSearchBookmark("settings-current-responsable");
              if (bookmark != null && scope.id == parseInt(bookmark))
                this.changeCurrentUser(scope);
              else {
                this.changeCurrentUser(scope);
                break;
              }
            } else {
              if (scope.id == this.idResponsable)
                this.changeCurrentUser(scope);
            }

      })
    })
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

  modifyAddresse() {
    if (this.currentUser)
      this.appService.openRoute(event, "account/settings/address/edit/" + this.currentUser.id, undefined);
  }
}
