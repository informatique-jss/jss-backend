import { AfterContentChecked, ChangeDetectorRef, Component, Input, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { AppService } from 'src/app/services/app.service';
import { Responsable } from '../../../tiers/model/Responsable';
import { CommunicationPreference } from '../../model/CommunicationPreference';
import { CommunicationPreferenceService } from '../../services/communication.preference.service';


@Component({
  selector: 'communication-preference',
  templateUrl: './communication-preference.component.html',
  styleUrls: ['./communication-preference.component.css']
})
export class CommunicationPreferenceComponent implements OnInit, AfterContentChecked {

  @Input() responsable: Responsable | undefined;

  communicationPreference: CommunicationPreference = {} as CommunicationPreference;

  constructor(
    private communicationPreferenceService: CommunicationPreferenceService,
    private formBuilder: FormBuilder,
    private changeDetectorRef: ChangeDetectorRef,
    private appService: AppService
  ) { }

  communicationPreferenceForm = this.formBuilder.group({});

  ngOnInit() {
    if (this.responsable) {
      this.communicationPreferenceService.getCommunicationPreferenceByMail(this.responsable.mail.mail).subscribe((preferences) => {
        this.communicationPreference = preferences;
      });
    }
  }

  ngAfterContentChecked(): void {
    this.changeDetectorRef.detectChanges();
  }

  toggleNewspaperNewsletter() {
    if (this.communicationPreference.isSubscribedToNewspaperNewletter) {
      this.communicationPreferenceService.subscribeToNewspaperNewsletter(this.responsable!.mail.mail).subscribe();
    } else {
      this.communicationPreferenceService.unsubscribeToNewspaperNewsletter(this.responsable!.mail.mail).subscribe();
    }
  }

  toggleCorporateNewsletter() {
    if (this.communicationPreference.isSubscribedToCorporateNewsletter) {
      this.communicationPreferenceService.subscribeToCorporateNewsletter(this.responsable!.mail.mail).subscribe();
    } else {
      this.communicationPreferenceService.unsubscribeToCorporateNewsletter(this.responsable!.mail.mail).subscribe();
    }
  }
}