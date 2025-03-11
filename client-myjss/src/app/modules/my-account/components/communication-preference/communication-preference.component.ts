import { AfterContentChecked, ChangeDetectorRef, Component, Input, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Responsable } from '../../../profile/model/Responsable';
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
  currentUser: Responsable | undefined;

  communicationPreference: CommunicationPreference = {} as CommunicationPreference;

  constructor(
    private communicationPreferenceService: CommunicationPreferencesService,
    private loginService: LoginService,
    private formBuilder: FormBuilder,
    private changeDetectorRef: ChangeDetectorRef
  ) { }

  communicationPreferenceForm = this.formBuilder.group({});

  ngOnInit() {
    if (!this.mail) {
      this.loginService.getCurrentUser().subscribe((user) => {
        this.currentUser = user;
        this.mail = user.mail.mail;
        if (this.mail) {
          this.communicationPreferenceService.getCommunicationPreferenceByMail(this.mail).subscribe((preferences) => {
            this.communicationPreference = preferences;
          });
        }
      })
    } else {
      this.communicationPreferenceService.getCommunicationPreferenceByMail(this.mail).subscribe((preferences) => {
        this.communicationPreference = preferences;
      });
    }
  }

  ngAfterContentChecked(): void {
    this.changeDetectorRef.detectChanges();
  }

  toggleNewspaperNewsletter() {
    if (this.communicationPreference.isSubscribedToNewspaperNewletter) {
      this.communicationPreferenceService.subscribeToNewspaperNewsletter(this.mail!).subscribe();
    } else {
      this.communicationPreferenceService.unsubscribeToNewspaperNewsletter(this.mail!).subscribe();
    }
  }

  toggleCorporateNewsletter() {
    if (this.communicationPreference.isSubscribedToCorporateNewsletter) {
      this.communicationPreferenceService.subscribeToCorporateNewsletter(this.mail!).subscribe();
    } else {
      this.communicationPreferenceService.unsubscribeToCorporateNewsletter(this.mail!).subscribe();
    }
  }
}