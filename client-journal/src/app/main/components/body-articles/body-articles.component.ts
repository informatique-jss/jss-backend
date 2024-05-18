import { Component, OnInit } from '@angular/core';
import { MyJssCategory } from '../../model/MyJssCategory';
import { Post } from '../../model/Post';
import { MyJssCategoryService } from '../../services/myjss.category.service';
import { PostService } from '../../services/post.service';

@Component({
  selector: 'body-articles',
  templateUrl: './body-articles.component.html',
  styleUrls: ['./body-articles.component.css']
})
export class BodyArticlesComponent implements OnInit {

  posts: Post[] = [];
  interviews: Post[] = [];
  podcasts: Post[] = [];
  departmentPosts: Post[] = [];
  page: number = 1;
  categories: MyJssCategory[] = [];

  constructor(
    private postService: PostService,
    private myJssCategoryService: MyJssCategoryService,
  ) { }

  ngOnInit() {
    this.fetchNextPosts();
    this.myJssCategoryService.getAvailableMyJssCategories().subscribe(categories => {
      if (categories && categories.length > 0)
        this.categories.push(...categories.sort((a: MyJssCategory, b: MyJssCategory) => { return a.count - b.count }));
    });
    this.postService.getTopPostInterview(1).subscribe(interviews => {
      if (interviews && interviews.length > 0)
        this.interviews = interviews;
    })
    this.postService.getTopPostPodcast(1).subscribe(podcasts => {
      if (podcasts && podcasts.length > 0)
        this.podcasts = podcasts;
      const event = new Event("RefreshThemeFunctions");
      document.dispatchEvent(event);
    })
  }

  getNextPosts() {
    this.page++;
    this.fetchNextPosts();
  }

  fetchNextPosts() {
    this.postService.getTopPost(this.page).subscribe(posts => {
      if (posts && posts.length > 0) {
        this.posts.push(...posts);

        // Load department posts until 5 posts
        if (this.departmentPosts.length < 5)
          for (let departmentPost of this.posts) {
            if (departmentPost.fullDepartment != null && this.departmentPosts.length < 5
              && departmentPost.fullDepartment[0] && !isNaN(departmentPost.fullDepartment[0].code as any) && parseInt(departmentPost.fullDepartment[0].code) > 0) {
              this.departmentPosts.push(departmentPost);
            }
          }
      }
    })
  }
}
