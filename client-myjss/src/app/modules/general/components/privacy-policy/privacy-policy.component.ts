import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, UrlSegment } from '@angular/router';

@Component({
  selector: 'privacy-policy',
  templateUrl: './privacy-policy.component.html',
  styleUrls: ['./privacy-policy.component.css'],
  standalone: false
})
export class PrivacyPolicyComponent implements OnInit {

  constructor(private activatedRoute: ActivatedRoute) { }

  tabs = [
    { id: 'privacy-policy', label: 'Politique de protection des données personnelles' },
    { id: 'disclaimer', label: 'Mentions légales' },
    { id: 'terms', label: 'CGU' },
  ];

  selectedTab = this.tabs[0];

  ngOnInit() {
    let url: UrlSegment[] = this.activatedRoute.snapshot.url;
    const tab = this.tabs.find(t => t.id === url[0].path);
    if (tab) {
      this.selectedTab = tab;
    } else {
      this.selectedTab = this.tabs[0];
    }
  }

  selectTab(tab: any) {
    this.selectedTab = tab;
  }
}
