import { Component, OnInit } from '@angular/core';
import { jarallax } from 'jarallax';
import { AppService } from '../../../../libs/app.service';
import { ConstantService } from '../../../../libs/constant.service';
import { Post } from '../../../tools/model/Post';
import { PostService } from '../../../tools/services/post.service';

@Component({
  selector: 'apostille',
  templateUrl: './apostille.component.html',
  styleUrls: ['./apostille.component.css'],
  standalone: false
})
export class ApostilleComponent implements OnInit {
  tendencyPosts: Post[] = [];
  constructor(private appService: AppService,
    private postService: PostService,
    private constantService: ConstantService
  ) {
  }
  ngOnInit() {
    this.postService.getTopPostByMyJssCategory(0, this.constantService.getMyJssCategoryApostille()).subscribe(response => {
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

  openFormality(event: any) {
    this.appService.openRoute(event, "/services/formality", undefined);
  }

  openDomiciliation(event: any) {
    this.appService.openRoute(event, "/services/domiciliation", undefined);
  }

  openDocument(event: any) {
    this.appService.openRoute(event, "/services/document", undefined);
  }
}
