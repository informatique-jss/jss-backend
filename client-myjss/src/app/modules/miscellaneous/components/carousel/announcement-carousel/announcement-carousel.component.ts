import { Component, OnInit } from '@angular/core';
import { Announcement } from '../../../../general/model/Announcement';
import { AnnouncementService } from '../../../../general/services/announcement.service';
import { CarouselItem } from '../../../model/CarouselItem';
import { GenericCarouselComponent } from '../generic-carousel/generic-carousel.component';

@Component({
  selector: 'announcement-carousel',
  templateUrl: '../generic-carousel/generic-carousel.component.html',
  styleUrls: ['../generic-carousel/generic-carousel.component.css']
})
export class AnnouncementCarouselComponent extends GenericCarouselComponent implements OnInit {
  topAnnouncements: Announcement[] = [] as Array<Announcement>;

  override title = "Les annonces légales les plus courantes";

  constructor(private announcementService: AnnouncementService) {
    super();
  }

  override ngOnInit(): void {
    this.announcementService.getTopAnnouncement(0).subscribe(response => {
      if (response) {
        this.topAnnouncements.push(...response);

        let carouselImageItem = {} as CarouselItem;
        carouselImageItem.title = '';
        carouselImageItem.description = '';
        carouselImageItem.image = '/assets/img/carousel/carousel_announcement_init.jpg';
        this.carouselItems.push(carouselImageItem);
      }
      for (let announcement of this.topAnnouncements) {
        let carouselItem = {} as CarouselItem;
        carouselItem.title = announcement.noticeTypeFamily.label;
        carouselItem.description = announcement.notice;
        carouselItem.image = '';
        this.carouselItems.push(carouselItem);
      }
      this.calculatePages();
    });
  }
}
