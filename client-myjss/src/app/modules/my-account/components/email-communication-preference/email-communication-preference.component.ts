import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'email-communication-preference',
  templateUrl: './email-communication-preference.component.html',
  styleUrls: ['./email-communication-preference.component.css']
})
export class EmailCommunicationPreferenceComponent implements OnInit {

  urlMail: string = '';

  constructor(
    private route: ActivatedRoute,
  ) { }

  ngOnInit() {
    this.route.paramMap.subscribe(params => {
      this.urlMail = params.get('mail') || '';
    });
  }

}
