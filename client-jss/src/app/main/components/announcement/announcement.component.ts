import { Location } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { SHARED_IMPORTS } from '../../../libs/SharedImports';
import { TrustHtmlPipe } from '../../../libs/TrustHtmlPipe';
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
  ) { }

  ngOnInit() {
    this.idAnnouncement = this.activatedRoute.snapshot.params['id'];
    if (this.idAnnouncement)
      this.announcementService.getAnnouncement(this.idAnnouncement).subscribe(response => {
        this.announcement = response;
      })
  }

  goBack() {
    this.location.back();
  }

  downloadPublicationReceipt() {
    if (this.announcement)
      this.announcementService.downloadPublicationReceipt(this.announcement);
  }
}
