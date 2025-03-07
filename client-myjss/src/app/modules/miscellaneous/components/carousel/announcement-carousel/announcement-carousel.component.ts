import { Component, Input, OnInit } from '@angular/core';
import { Post } from '../../../../general/model/Post';
import { PostService } from '../../../../general/services/post.service';
import { CarouselItem } from '../../../model/CarouselItem';
import { GenericCarouselComponent } from '../generic-carousel/generic-carousel.component';

@Component({
  selector: 'announcement-carousel',
  templateUrl: '../generic-carousel/generic-carousel.component.html',
  styleUrls: ['../generic-carousel/generic-carousel.component.css']
})
export class AnnouncementCarouselComponent extends GenericCarouselComponent implements OnInit {
  topAnnouncements: Post[] = [] as Array<Post>;
  @Input() page: number = 0;
  override title = "Les annonces légales les plus courantes";

  constructor(private postService: PostService) {
    super();
  }

  override ngOnInit(): void {
    //TODO récupérer du back les articles qui traitent des AL
    this.postService.getTopPost(this.page).subscribe(response => {
      if (response) {
        let firstItem = {} as CarouselItem;
        firstItem.title = '';
        firstItem.description = '';
        firstItem.image = '/assets/img/carousel/carousel_announcement_init.jpg';
        this.carouselItems.push(firstItem);
        this.topAnnouncements.push(...response);
      }
      for (let announcement of this.topAnnouncements) {
        let carouselItem = {} as CarouselItem;
        carouselItem.title = announcement.slug;
        carouselItem.description = announcement.excerptText;
        carouselItem.image = '';
        this.carouselItems.push(carouselItem);
      }
      this.calculatePages();
    });
  }
}
