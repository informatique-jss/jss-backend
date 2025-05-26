import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { SHARED_IMPORTS } from '../../../libs/SharedImports';
import { TrustHtmlPipe } from '../../../libs/TrustHtmlPipe';
import { Announcement } from '../../model/Announcement';
import { AnnouncementService } from '../../services/announcement.service';

@Component({
  selector: 'app-announcement',
  templateUrl: './announcement.component.html',
  styleUrls: ['./announcement.component.css'],
  imports: [SHARED_IMPORTS, TrustHtmlPipe],
  standalone: true
})
export class AnnouncementComponent implements OnInit {

  announcement: Announcement | undefined;
  idAnnouncement: number | undefined;

  constructor(
    private annoucementService: AnnouncementService,
    private activatedRoute: ActivatedRoute
  ) { }

  ngOnInit() {
    this.idAnnouncement = this.activatedRoute.snapshot.params['id'];
    if (this.idAnnouncement)
      this.annoucementService.getAnnouncement(this.idAnnouncement).subscribe(response => {
        this.announcement = response;
      })
  }

}
