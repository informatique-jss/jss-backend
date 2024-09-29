import { Component, OnInit } from '@angular/core';
import { getTimeReading } from '../../../libs/FormatHelper';
import { Post } from '../../model/Post';
import { PostService } from '../../services/post.service';

@Component({
  selector: 'new-articles',
  templateUrl: './new-articles.component.html',
  styleUrls: ['./new-articles.component.css']
})
export class NewArticlesComponent implements OnInit {

  posts: Post[] = [];

  constructor(
    private postService: PostService,
  ) { }

  ngOnInit() {
    this.postService.getTopPost(1).subscribe(posts => {
      this.posts = posts;
    })
  }

  getTimeReading = getTimeReading;

}
