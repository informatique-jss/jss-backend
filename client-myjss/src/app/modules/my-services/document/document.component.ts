import { Component, OnInit } from '@angular/core';
import { jarallax } from 'jarallax';
import { AppService } from '../../../libs/app.service';
import { Post } from '../../tools/model/Post';
import { PostService } from '../../tools/services/post.service';

@Component({
  selector: 'document',
  templateUrl: './document.component.html',
  styleUrls: ['./document.component.css'],
  standalone: false
})
export class DocumentComponent implements OnInit {

  tendencyPosts: Post[] = [];

  constructor(private appService: AppService,
    private postService: PostService,
  ) {
  }
  ngOnInit() {
    this.postService.getTendencyPosts().subscribe(response => {
      if (response && response.length > 0) {
        this.tendencyPosts = response;
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

  openApostille(event: any) {
    this.appService.openRoute(event, "/services/apostille", undefined);
  }

  openDomiciliation(event: any) {
    this.appService.openRoute(event, "/services/domiciliation", undefined);
  }
}
