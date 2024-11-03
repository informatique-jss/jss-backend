import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Post } from '../../model/Post';
import { PostService } from '../../services/post.service';

@Component({
  selector: 'app-podcast-post',
  templateUrl: './podcast-post.component.html',
  styleUrls: ['./podcast-post.component.css']
})
export class PodcastPostComponent implements OnInit {

  podcast: Post | undefined;
  slug: string | undefined;

  constructor(
    private postService: PostService,
    private activatedRoute: ActivatedRoute
  ) { }

  ngOnInit() {
    this.slug = this.activatedRoute.snapshot.params['slug'];
    if (this.slug)
      this.postService.getPostBySlug(this.slug).subscribe(post => {
        this.podcast = post;
      })
  }

}
