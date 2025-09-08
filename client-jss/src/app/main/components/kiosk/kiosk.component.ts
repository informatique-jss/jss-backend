import { Component, OnInit } from '@angular/core';
import { Meta, Title } from '@angular/platform-browser';
import { SHARED_IMPORTS } from '../../../libs/SharedImports';
import { AppService } from '../../../services/app.service';
import { GtmService } from '../../../services/gtm.service';
import { CtaClickPayload, PageInfo } from '../../../services/GtmPayload';
import { Newspaper } from '../../model/Newspaper';
import { Responsable } from '../../model/Responsable';
import { NEWSPAPER_KIOSK_BUY } from '../../model/Subscription';
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
    private loginService: LoginService,
    private gtmService: GtmService,
    private titleService: Title, private meta: Meta,
    private appService: AppService) { }

  ngOnInit() {
    this.titleService.setTitle("Toutes les éditions du Journal Spécial des Sociétés - JSS");
    this.meta.updateTag({ name: 'description', content: "Accédez à toutes les éditions du Journal Spécial des Sociétés avec JSS. Consultez nos archives pour retrouver une annonce ou une information juridique précise." });
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
    if (this.isSubscribed || this.newspaperIdsSeeableByUser.includes(newspaperId))
      this.trackCtaClickDownloadComplete(newspaperId);
    else
      this.trackCtaClickDownloadExtract(newspaperId);
    this.newspaperService.getPdfForUser(newspaperId);
  }

  buyNewspaper(newspaperId: number) {
    this.trackCtaClicBuy(newspaperId);
    this.appService.openMyJssRoute(event, "/quotation/subscription/" + NEWSPAPER_KIOSK_BUY + "/" + false + "/" + newspaperId, true);
  }

  trackCtaClickDownloadExtract(newspaperId: number) {
    this.gtmService.trackCtaClick(
      {
        cta: { type: 'link', label: "Lire un extrait", objectId: newspaperId },
        page: {
          type: 'main',
          name: 'kiosk'
        } as PageInfo
      } as CtaClickPayload
    );
  }

  trackCtaClicBuy(newspaperId: number) {
    this.gtmService.trackCtaClick(
      {
        cta: { type: 'link', label: "Acheter", objectId: newspaperId },
        page: {
          type: 'main',
          name: 'kiosk'
        } as PageInfo
      } as CtaClickPayload
    );
  }

  trackCtaClickDownloadComplete(newspaperId: number) {
    this.gtmService.trackCtaClick(
      {
        cta: { type: 'link', label: "Lire le journal", objectId: newspaperId },
        page: {
          type: 'main',
          name: 'kiosk'
        } as PageInfo
      } as CtaClickPayload
    );
  }

}
