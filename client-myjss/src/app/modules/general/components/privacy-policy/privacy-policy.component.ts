import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'privacy-policy',
  templateUrl: './privacy-policy.component.html',
  styleUrls: ['./privacy-policy.component.css'],
  standalone: false
})
export class PrivacyPolicyComponent implements OnInit {

  constructor(private route: ActivatedRoute) { }

  tabs = [
    { id: 'privacy', label: 'Politique de protection des données personnelles' },
    { id: 'legal', label: 'Mentions légales' },
    { id: 'cgu', label: 'CGU' },
  ];

  selectedTab = this.tabs[0];

  ngOnInit() {
    this.route.fragment.subscribe(fragment => {
      const tab = this.tabs.find(t => t.id === fragment);
      if (tab) {
        this.selectedTab = tab;
      } else {
        this.selectedTab = this.tabs[0];
      }
    });
  }

  selectTab(tab: any) {
    this.selectedTab = tab;
  }
}
