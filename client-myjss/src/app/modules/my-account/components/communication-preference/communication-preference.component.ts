import { AfterContentChecked, ChangeDetectorRef, Component, Input, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { AppService } from '../../../../libs/app.service';
import { LoginService } from '../../../profile/services/login.service';
import { CommunicationPreference } from '../../model/CommunicationPreference';
import { CommunicationPreferencesService } from '../../services/communication.preference.service';


@Component({
  selector: 'communication-preference',
  templateUrl: './communication-preference.component.html',
  styleUrls: ['./communication-preference.component.css']
})
export class CommunicationPreferenceComponent implements OnInit, AfterContentChecked {

  @Input() mail: string | undefined;
  @Input() validationToken: string | null = null;

  communicationPreference: CommunicationPreference = {} as CommunicationPreference;

  constructor(
    private communicationPreferenceService: CommunicationPreferencesService,
    private loginService: LoginService,
    private formBuilder: FormBuilder,
    private changeDetectorRef: ChangeDetectorRef,
    private appService: AppService
  ) { }

  communicationPreferenceForm = this.formBuilder.group({});

  ngOnInit() {
    if (!this.mail) {
      this.loginService.getCurrentUser().subscribe((user) => {
        this.mail = user.mail.mail;
        this.loadPreferenceByMail(this.mail);
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
