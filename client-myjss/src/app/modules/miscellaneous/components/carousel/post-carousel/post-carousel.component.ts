import { Component, OnInit } from '@angular/core';
import { Post } from '../../../../general/model/Post';
import { PostService } from '../../../../general/services/post.service';
import { PostCarouselItem } from '../../../model/PostCarouselItem';
import { GenericCarouselComponent } from '../generic-carousel/generic-carousel.component';


@Component({
  selector: 'post-carousel',
  templateUrl: './post-carousel.component.html',
  styleUrls: ['./post-carousel.component.css']
})
export class PostCarouselComponent extends GenericCarouselComponent implements OnInit {
  posts: Post[] = [];
  page: number = 0;
  override carouselItems: PostCarouselItem[] = [];
  override title: string = "Les articles à la une";

  constructor(private postService: PostService) {
    super();
  }
  override ngOnInit(): void {

    this.fetchNextPosts();
    console.log(this.pages);
  }

  fetchNextPosts() {
    this.postService.getTopPost(this.page).subscribe(response => {
      if (response && response.length > 0) {
        this.posts.push(...response);
        for (let post of response) {
          let carouselItem = {} as PostCarouselItem;
          carouselItem.title = '';
          carouselItem.description = '';
          carouselItem.image = '';
          carouselItem.post = post;
          this.carouselItems.push(carouselItem);
        }
        this.calculatePages();
      }
    });
  }
}
