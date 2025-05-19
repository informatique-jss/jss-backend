import { Component, OnInit } from '@angular/core';
import { jarallax } from 'jarallax';
import { AppService } from '../../../../libs/app.service';
import { ConstantService } from '../../../../libs/constant.service';
import { Post } from '../../../tools/model/Post';
import { PostService } from '../../../tools/services/post.service';

@Component({
  selector: 'formality',
  templateUrl: './formality.component.html',
  styleUrls: ['./formality.component.css'],
  standalone: false
})
export class FormalityComponent implements OnInit {
  tendencyPosts: Post[] = [] as Array<Post>;
  myJssCategoryFormality = this.constantService.getMyJssCategoryFormality();
  carouselFormalityPosts: Post[] = [];

  constructor(private appService: AppService,
    private constantService: ConstantService,
    private postService: PostService
  ) { }

  ngOnInit() {
    this.postService.getPinnedPostByMyJssCategory(0, this.myJssCategoryFormality).subscribe(posts => {
      if (posts && posts.content)
        this.carouselFormalityPosts.push(...posts.content);
    });
    this.postService.getTopPostByMyJssCategory(0, this.constantService.getMyJssCategoryFormality()).subscribe(response => {
      if (response && response.content && response.content.length > 0) {
        this.tendencyPosts = response.content;
      }
    });
  }

  ngAfterViewInit(): void {
    jarallax(document.querySelectorAll('.jarallax'), {
      speed: 0.5
    });
  }

  openPost(slug: string, event: any) {
    this.appService.openRoute(event, "post/" + slug, undefined);
  }

  openAnnouncements(event: any) {
    this.appService.openRoute(event, "/services/announcement", undefined);
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

}
