import { Component, Input, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import { AppService } from '../../../services/app.service';
import { PagedContent } from '../../model/PagedContent';
import { Post } from '../../model/Post';
import { Serie } from '../../model/Serie';
import { Tag } from '../../model/Tag';
import { PostService } from '../../services/post.service';
import { TagService } from '../../services/tag.service';
import { GenericHubComponent } from '../generic-hub/generic-hub.component';

@Component({
  selector: 'serie-hub',
  templateUrl: './../generic-hub/generic-hub.component.html',
  styleUrls: ['./../generic-hub/generic-hub.component.css'],
  standalone: false
})
export class SerieHubComponent extends GenericHubComponent<Serie> implements OnInit {
  @Input() override selectedEntityType: Serie | undefined;

  constructor(private postService: PostService, private tagService: TagService, appService: AppService, formBuilder: FormBuilder
  ) {
    super(appService, formBuilder);
  }
  override getAllPostByEntityType(selectedEntityType: Serie, page: number, pageSize: number, searchText: string): Observable<PagedContent<Post>> {
    return this.postService.getAllPostsBySerie(selectedEntityType, page, pageSize, searchText);
  }

  override getAllTagByEntityType(selectedEntityType: Serie): Observable<Array<Tag>> {
    return this.tagService.getAllTagsBySerie(selectedEntityType);
  }

  override getMostSeenPostByEntityType(selectedEntityType: Serie, page: number, pageSize: number): Observable<PagedContent<Post>> {
    return this.postService.getMostSeenPostBySerie(selectedEntityType, page, pageSize);
  }

}
