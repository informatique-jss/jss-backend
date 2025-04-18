import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { getTimeReading } from '../../../libs/FormatHelper';
import { AppService } from '../../../services/app.service';
import { Author } from '../../model/Author';
import { JssCategory } from '../../model/JssCategory';
import { Post } from '../../model/Post';
import { AuthorService } from '../../services/author.service';
import { PostService } from '../../services/post.service';

@Component({
  selector: 'app-author-list',
  templateUrl: './author-list.component.html',
  styleUrls: ['./author-list.component.css'],
  standalone: false
})
export class AuthorListComponent implements OnInit {

  page: number = 0;
  posts: Post[] = [];
  currentAuthor: Author | null = null;
  slug: string = "";
  displayLoadMoreButton: boolean = true;

  constructor(
    private postService: PostService,
    private activatedRoute: ActivatedRoute,
    private authorService: AuthorService,
    private appService: AppService,
  ) { }

  getTimeReading = getTimeReading;

  ngOnInit() {
    this.slug = this.activatedRoute.snapshot.params['slug'];
    if (this.slug)
      this.authorService.getAuthorBySlug(this.slug).subscribe(author => {
        if (author) {
          this.currentAuthor = author;
          this.fetchNextPosts();
        }
      })
  }

  getNextPosts() {
    this.page++;
    this.fetchNextPosts();
  }

  fetchNextPosts() {
    // TODO To remove if not used anymore
    // if (this.currentAuthor)
    //   this.postService.getTopPostByAuthor(this.page, this.currentAuthor).subscribe(posts => {
    //     if (posts && posts.length > 0) {
    //       this.posts.push(...posts);
    //     } else {
    //       this.displayLoadMoreButton = false;
    //     }
    //   });
  }


  openPost(post: Post, event: any) {
    this.appService.openRoute(event, "post/" + post.slug, undefined);
  }

  openCategoryPosts(category: JssCategory, event: any) {
    this.appService.openRoute(event, "category/" + category.slug, undefined);
  }

}
