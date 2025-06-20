import { Component, OnInit } from '@angular/core';
import { SHARED_IMPORTS } from '../../../libs/SharedImports';
import { Newspaper } from '../../model/Newspaper';
import { Responsable } from '../../model/Responsable';
import { LoginService } from '../../services/login.service';
import { NewspaperService } from '../../services/newspaper.service';
import { NewsletterComponent } from "../newsletter/newsletter.component";

@Component({
  selector: 'kiosk',
  templateUrl: './kiosk.component.html',
  styleUrls: ['./kiosk.component.css'],
  imports: [SHARED_IMPORTS, NewsletterComponent],
  standalone: true
})
export class KioskComponent implements OnInit {

  currentUser: Responsable | undefined;
  isSubscribed: boolean = false;
  newspaperIdsSeeableByUser: number[] = [];

  yearOpened: number = 2023;
  jssEditionNumberHovered: number = 0;
  newspapersToDisplay: Newspaper[] = [];
  years: number[] = [2023, 2022, 2021, 2020, 2019, 2018, 2017, 2016, 2015, 2014, 2013, 2012, 2011, 2010, 2009, 2008, 2007];


  constructor(
    private newspaperService: NewspaperService,
    private loginService: LoginService) { }

  ngOnInit() {
    this.loginService.getCurrentUser().subscribe(res => {
      this.currentUser = res;
      if (this.currentUser) {
        this.newspaperService.canSeeAllNewspapersOfKiosk().subscribe(canSeeAll => {
          if (canSeeAll == true) {
            this.isSubscribed = canSeeAll;
          }
          if (!this.isSubscribed) {
            this.newspaperService.getSeeableNewspapersForCurrentUser().subscribe(ids => {
              this.newspaperIdsSeeableByUser = ids;
            });
          }
        })
      }
    });

    this.fetchNewspapersForSelectedYear();
  }

  openTab(event: any, year: number) {
    event.preventDefault();
    this.yearOpened = year;
    this.fetchNewspapersForSelectedYear()
  }

  private fetchNewspapersForSelectedYear() {
    this.newspaperService.getNewspapersForYear(this.yearOpened).subscribe(newspapers => {
      this.newspapersToDisplay = newspapers;
    });
  }

  openReadExtractButton(newspaperId: number) {
    this.jssEditionNumberHovered = newspaperId;
  }

  closeReadExtractButton() {
    this.jssEditionNumberHovered = 0;
  }

  openExtract(newspaperId: number) {
    this.newspaperService.getPdfForUser(newspaperId);
  }
}
