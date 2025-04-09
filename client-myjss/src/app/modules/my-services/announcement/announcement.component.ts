import { Component, OnInit } from '@angular/core';
import { jarallax } from 'jarallax';
import { AppService } from '../../../libs/app.service';
import { ConstantService } from '../../../libs/constant.service';
import { Post } from '../../tools/model/Post';
import { PostService } from '../../tools/services/post.service';

@Component({
  selector: 'announcement',
  templateUrl: './announcement.component.html',
  styleUrls: ['./announcement.component.css'],
  standalone: false
})
export class AnnouncementComponent implements OnInit {
  myJssCategoryAnnouncement = this.constantService.getMyJssCategoryAnnouncement();
  carouselAnnouncementPosts: Post[] = [];
  tendencyPosts: Post[] = [];

  constructor(private appService: AppService,
    private constantService: ConstantService,
    private postService: PostService
  ) { }

  ngOnInit() {
    this.postService.getTendencyPosts().subscribe(response => {
      if (response && response.length > 0) {
        this.tendencyPosts = response;
      }
    });

    this.postService.getTopPostByMyJssCategory(0, this.myJssCategoryAnnouncement).subscribe(posts => {
      if (posts)
        this.carouselAnnouncementPosts = posts;
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
