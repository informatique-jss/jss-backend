import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'email-communication-preference',
  templateUrl: './email-communication-preference.component.html',
  styleUrls: ['./email-communication-preference.component.css']
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
