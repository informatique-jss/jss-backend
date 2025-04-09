import { Component, OnInit } from '@angular/core';
import { jarallax } from 'jarallax';
import { AppService } from '../../../libs/app.service';
import { MyJssCategory } from '../../tools/model/MyJssCategory';
import { Post } from '../../tools/model/Post';
import { MyJssCategoryService } from '../../tools/services/myjss.category.service';
import { PostService } from '../../tools/services/post.service';

@Component({
  selector: 'announcement',
  templateUrl: './announcement.component.html',
  styleUrls: ['./announcement.component.css'],
  standalone: false
})
export class AnnouncementComponent implements OnInit {
  myJssCategoryAnnouncement: MyJssCategory | undefined;
  carouselAnnouncementPosts: Post[] = [];

  constructor(private appService: AppService,
    private myJssCategoryService: MyJssCategoryService,
    private postService: PostService
  ) {
  }

  ngOnInit() {
    this.myJssCategoryService.getAnnouncementMyJssCategory().subscribe(response => {
      if (response) {
        this.myJssCategoryAnnouncement = response;
        this.postService.getTopPostByMyJssCategory(0, this.myJssCategoryAnnouncement).subscribe(posts => {
          if (posts)
            this.carouselAnnouncementPosts.push(...posts);
        });
      }
    });
  }

  ngAfterViewInit(): void {
    jarallax(document.querySelectorAll('.jarallax'), {
      speed: 0.5
    });
  }

  openFormality(event: any) {
    this.appService.openRoute(event, "/services/formality", undefined);
  }

  openApostille(event: any) {
    this.appService.openRoute(event, "/services/apostille", undefined);
  }

  openDomiciliation(event: any) {
    this.appService.openRoute(event, "/services/domiciliation", undefined);
  }

  openDocument(event: any) {
    this.appService.openRoute(event, "/services/document", undefined);
  }
  openPost(slug: string, event: any) {
    this.appService.openRoute(event, "post/" + slug, undefined);
  }
}
