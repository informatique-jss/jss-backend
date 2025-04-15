import { Component, Input, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { JssCategory } from '../../model/JssCategory';
import { PagedContent } from '../../model/PagedContent';
import { Post } from '../../model/Post';
import { PostService } from '../../services/post.service';
import { GenericHubComponent } from '../generic-hub/generic-hub.component';

@Component({
  selector: 'hub-category',
  templateUrl: './../generic-hub/generic-hub.component.html',
  styleUrls: ['./../generic-hub/generic-hub.component.css'],
  standalone: false
})
export class HubCategoryComponent extends GenericHubComponent<JssCategory> implements OnInit {

  @Input() override selectedEntityType: JssCategory | undefined;

  constructor(private postService: PostService) {
    super();
  }
  override getAllPostByEntityType(selectedEntityType: JssCategory, page: number, pageSize: number): Observable<PagedContent<Post>> {
    return this.postService.getAllPostsByJssCategory(selectedEntityType, page, pageSize); //TODO ajout searchText
  }

}
