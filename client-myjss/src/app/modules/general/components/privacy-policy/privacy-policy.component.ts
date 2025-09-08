import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, UrlSegment } from '@angular/router';
import { SHARED_IMPORTS } from '../../../../libs/SharedImports';

@Component({
  selector: 'privacy-policy',
  templateUrl: './privacy-policy.component.html',
  styleUrls: ['./privacy-policy.component.css'],
  standalone: true,
  imports: [SHARED_IMPORTS]
})
export class PrivacyPolicyComponent implements OnInit {

  constructor(private activatedRoute: ActivatedRoute) { }

  tabs = [
    { id: 'privacy-policy', label: 'Politique de confidentialité' },
    { id: 'disclaimer', label: 'Mentions légales' },
    { id: 'terms', label: 'CGU', pdf: '/assets/documents/CGU_JSS.pdf' },
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
