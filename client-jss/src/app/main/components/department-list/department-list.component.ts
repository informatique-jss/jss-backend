import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { getTimeReading } from '../../../libs/FormatHelper';
import { AppService } from '../../../services/app.service';
import { Author } from '../../model/Author';
import { JssCategory } from '../../model/JssCategory';
import { Post } from '../../model/Post';
import { PublishingDepartment } from '../../model/PublishingDepartment';
import { DepartmentService } from '../../services/department.service';
import { PostService } from '../../services/post.service';

@Component({
    selector: 'app-department-list',
    templateUrl: './department-list.component.html',
    styleUrls: ['./department-list.component.css'],
    standalone: false
})
export class DepartmentListComponent implements OnInit {

  page: number = 0;
  posts: Post[] = [];
  departments: PublishingDepartment[] = [];
  currentDepartment: PublishingDepartment | null = null;
  code: string = "";
  displayLoadMoreButton: boolean = true;

  getTimeReading = getTimeReading;

  constructor(
    private postService: PostService,
    private departmentService: DepartmentService,
    private activatedRoute: ActivatedRoute,
    private appService: AppService
  ) { }

  ngOnInit() {
    this.code = this.activatedRoute.snapshot.params['code'];
    if (this.code && this.code.length > 0)
      this.departmentService.getAvailablePublishingDepartments().subscribe(departments => {
        if (departments && departments.length > 0) {
          this.departments = departments;
          for (let department of this.departments) {
            if (department.code == this.code) {
              this.currentDepartment = department;
              break;
            }
          }
          if (this.currentDepartment)
            this.fetchNextPosts();
        }
      })
  }

  getNextPosts() {
    this.page++;
    this.fetchNextPosts();
  }

  openCategoryPosts(category: JssCategory, event: any) {
    this.appService.openRoute(event, "category/" + category.slug, undefined);
  }

  openPost(post: Post, event: any) {
    this.appService.openRoute(event, "post/" + post.slug, undefined);
  }

  openAuthorPosts(author: Author, event: any) {
    this.appService.openRoute(event, "author/" + author.slug, undefined);
  }

  fetchNextPosts() {
    if (this.currentDepartment)
      this.postService.getTopPostByDepartment(this.page, this.currentDepartment).subscribe(posts => {
        if (posts && posts.length > 0) {
          this.posts.push(...posts);
        } else {
          this.displayLoadMoreButton = false;
        }
      });
  }
}
