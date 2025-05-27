import { AfterContentChecked, ChangeDetectorRef, Component, Input, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { SHARED_IMPORTS } from '../../../../libs/SharedImports';
import { CommunicationPreference } from '../../../general/model/CommunicationPreference';
import { CommunicationPreferencesService } from '../../../general/services/communication.preference.service';
import { AppService } from '../../../main/services/app.service';
import { GenericToggleComponent } from '../../../miscellaneous/components/forms/generic-toggle/generic-toggle.component';
import { LoginService } from '../../../profile/services/login.service';


@Component({
  selector: 'communication-preference',
  templateUrl: './communication-preference.component.html',
  styleUrls: ['./communication-preference.component.css'],
  standalone: true,
  imports: [SHARED_IMPORTS, GenericToggleComponent]
})
export class CommunicationPreferenceComponent implements OnInit, AfterContentChecked {

  @Input() mail: string | undefined;
  @Input() validationToken: string | null = null;

  communicationPreference: CommunicationPreference = {} as CommunicationPreference;
  communicationPreferenceForm!: FormGroup;

  constructor(
    private communicationPreferenceService: CommunicationPreferencesService,
    private loginService: LoginService,
    private formBuilder: FormBuilder,
    private changeDetectorRef: ChangeDetectorRef,
    private appService: AppService
  ) { }


  ngOnInit() {
    this.communicationPreferenceForm = this.formBuilder.group({});
    if (!this.mail) {
      this.loginService.getCurrentUser().subscribe((user) => {
        this.mail = user.mail.mail;
        this.loadPreferenceByMail(this.mail!);
      })
    } else {
      this.loadPreferenceByMail(this.mail);
    }
  }

  loadPreferenceByMail(mail: string) {
    this.communicationPreferenceService.getCommunicationPreferenceByMail(mail, this.validationToken).subscribe((preferences) => {
      this.communicationPreference = preferences;
    });
  }

  ngAfterContentChecked(): void {
    this.changeDetectorRef.detectChanges();
  }

  toggleNewspaperNewsletter() {
    if (this.mail)
      if (!this.communicationPreference.isSubscribedToNewspaperNewletter) {
        this.communicationPreferenceService.subscribeToNewspaperNewsletter(this.mail).subscribe();
      } else {
        this.communicationPreferenceService.unsubscribeToNewspaperNewsletter(this.mail, this.validationToken).subscribe();
      }
  }

  toggleCorporateNewsletter() {
    if (this.mail)
      if (!this.communicationPreference.isSubscribedToCorporateNewsletter) {
        this.communicationPreferenceService.subscribeToCorporateNewsletter(this.mail).subscribe();
      } else {
        this.communicationPreferenceService.unsubscribeToCorporateNewsletter(this.mail, this.validationToken).subscribe();
      }
  }
}
