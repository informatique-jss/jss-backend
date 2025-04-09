import { Component, OnInit } from '@angular/core';
import { jarallax } from 'jarallax';
import { AppService } from '../../../libs/app.service';
import { MyJssCategory } from '../../tools/model/MyJssCategory';
import { Post } from '../../tools/model/Post';
import { MyJssCategoryService } from '../../tools/services/myjss.category.service';
import { PostService } from '../../tools/services/post.service';

@Component({
  selector: 'formality',
  templateUrl: './formality.component.html',
  styleUrls: ['./formality.component.css'],
  standalone: false
})
export class FormalityComponent implements OnInit {
  myJssCategoryFormality: MyJssCategory | undefined;
  tendencyPosts: Post[] = [] as Array<Post>;
  carouselFormalityPosts: Post[] = [];

  constructor(private appService: AppService,
    private myJssCategoryService: MyJssCategoryService,
    private postService: PostService
  ) { }

  ngOnInit() {
    this.myJssCategoryService.getFormalityMyJssCategory().subscribe(response => {
      if (response) {
        this.myJssCategoryFormality = response;
        this.postService.getTopPostByMyJssCategory(0, this.myJssCategoryFormality).subscribe(posts => {
          if (posts)
            this.carouselFormalityPosts.push(...posts);
        });
        this.postService.getTendencyPosts().subscribe(response => {
          if (response && response.length > 0) {
            this.tendencyPosts = response;
          }
        });
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
