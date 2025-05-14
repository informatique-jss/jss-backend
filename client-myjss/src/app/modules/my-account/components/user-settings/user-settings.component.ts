import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { AppService } from '../../../../libs/app.service';
import { ConstantService } from '../../../../libs/constant.service';
import { capitalizeName, getListMails, getListPhones } from '../../../../libs/FormatHelper';
import { UserPreferenceService } from '../../../../libs/user.preference.service';
import { Responsable } from '../../../profile/model/Responsable';
import { LoginService } from '../../../profile/services/login.service';
import { UserScopeService } from '../../../profile/services/user.scope.service';
import { Document } from '../../model/Document';
import { DocumentService } from '../../services/document.service';

@Component({
  selector: 'app-user-settings',
  templateUrl: './user-settings.component.html',
  styleUrls: ['./user-settings.component.css'],
  standalone: false
})
export class UserSettingsComponent implements OnInit {

  currentUser: Responsable | undefined;
  userScope: Responsable[] | undefined;
  idResponsable: number | undefined;

  documents: Document[] | undefined;
  documentForm = this.formBuilder.group({});

  documentTypeBilling = this.constantService.getDocumentTypeBilling();
  documentTypeDigital = this.constantService.getDocumentTypeDigital();
  documentTypePaper = this.constantService.getDocumentTypePaper();

  billingLabelTypeAffaire = this.constantService.getBillingLabelTypeCodeAffaire();
  billingLabelTypeCustomer = this.constantService.getBillingLabelTypeCustomer();
  billingLabelTypeOther = this.constantService.getBillingLabelTypeOther();

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
              else if (scope.id == response.id)
                this.changeCurrentUser(scope);
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
