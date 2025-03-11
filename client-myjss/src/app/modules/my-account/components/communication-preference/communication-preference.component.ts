import { AfterContentChecked, ChangeDetectorRef, Component, Input, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { AppService } from '../../../../libs/app.service';
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
    private changeDetectorRef: ChangeDetectorRef,
    private appService: AppService
  ) { }

  communicationPreferenceForm = this.formBuilder.group({});

  ngOnInit() {
    if (!this.mail) {
      this.loginService.getCurrentUser().subscribe((user) => {
        this.currentUser = user;
        this.mail = user.mail.mail;
        this.loadPreferenceByMail(this.mail);
      })
    } else {
      this.loadPreferenceByMail(this.mail);
    }
  }

  private loadPreferenceByMail(mail: string) {
    this.communicationPreferenceService.getCommunicationPreferenceByMail(mail).subscribe((preferences) => {
      this.communicationPreference = preferences;
    });
  }

  ngAfterContentChecked(): void {
    this.changeDetectorRef.detectChanges();
  }

  toggleNewspaperNewsletter() {
    if (this.communicationPreference.isSubscribedToNewspaperNewletter) {
      this.communicationPreferenceService.subscribeToNewspaperNewsletter(this.mail!).subscribe();
      this.appService.displayToast("Préférences de communication mises à jours.", false, "Mise à jour effectuée", 2000);
    } else {
      this.communicationPreferenceService.unsubscribeToNewspaperNewsletter(this.mail!).subscribe();
      this.appService.displayToast("Préférences de communication mises à jours.", false, "Mise à jour effectuée", 2000);
    }
  }

  toggleCorporateNewsletter() {
    if (this.communicationPreference.isSubscribedToCorporateNewsletter) {
      this.communicationPreferenceService.subscribeToCorporateNewsletter(this.mail!).subscribe();
      this.appService.displayToast("Préférences de communication mises à jours.", false, "Mise à jour effectuée", 2000);
    } else {
      this.communicationPreferenceService.unsubscribeToCorporateNewsletter(this.mail!).subscribe();
      this.appService.displayToast("Préférences de communication mises à jours.", false, "Mise à jour effectuée", 2000);
    }
  }
}