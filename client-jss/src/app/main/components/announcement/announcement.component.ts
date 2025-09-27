import { Location } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { SHARED_IMPORTS } from '../../../libs/SharedImports';
import { TrustHtmlPipe } from '../../../libs/TrustHtmlPipe';
import { GtmService } from '../../../services/gtm.service';
import { CtaClickPayload, PageInfo } from '../../../services/GtmPayload';
import { Announcement } from '../../model/Announcement';
import { AnnouncementService } from '../../services/announcement.service';
import { NewsletterComponent } from "../newsletter/newsletter.component";

@Component({
  selector: 'announcement',
  templateUrl: './announcement.component.html',
  styleUrls: ['./announcement.component.css'],
  imports: [SHARED_IMPORTS, TrustHtmlPipe, NewsletterComponent],
  standalone: true
})
export class AnnouncementComponent implements OnInit {

  announcement: Announcement | undefined;
  idAnnouncement: number | undefined;

  constructor(
    private announcementService: AnnouncementService,
    private activatedRoute: ActivatedRoute,
    private location: Location,
    private gtmService: GtmService
  ) { }

  ngOnInit() {
    this.idAnnouncement = this.activatedRoute.snapshot.params['id'];
    if (this.idAnnouncement)
      this.announcementService.getAnnouncement(this.idAnnouncement).subscribe(response => {
        this.announcement = response;
      })
  }

  trackCtaClickDownloadFlag() {
    this.gtmService.trackCtaClick(
      {
        cta: { type: 'link', label: "Télécharger le témoin de parution", objectId: this.idAnnouncement },
        page: {
          type: 'main',
          name: 'announcement'
        } as PageInfo
      } as CtaClickPayload
    );
  }

  goBack() {
    this.location.back();
  }

  downloadPublicationFlag() {
    if (this.announcement) {
      this.trackCtaClickDownloadFlag();
      this.announcementService.downloadPublicationFlag(this.announcement);
    }
  }
}
