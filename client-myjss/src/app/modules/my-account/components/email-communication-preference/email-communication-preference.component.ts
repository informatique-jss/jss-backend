import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { SHARED_IMPORTS } from '../../../../libs/SharedImports';
import { CommunicationPreferenceComponent } from '../communication-preference/communication-preference.component';

@Component({
  selector: 'email-communication-preference',
  templateUrl: './email-communication-preference.component.html',
  styleUrls: ['./email-communication-preference.component.css'],
  standalone: true,
  imports: [SHARED_IMPORTS, CommunicationPreferenceComponent]
})
export class EmailCommunicationPreferenceComponent implements OnInit {

  urlMail: string = '';
  validationToken: string = '';

  constructor(
    private activatedRoute: ActivatedRoute,
  ) { }

  ngOnInit() {
    this.urlMail = this.activatedRoute.snapshot.params["mail"];
    this.validationToken = this.activatedRoute.snapshot.params["validationToken"];
  }
}
