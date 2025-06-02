import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { NgbTooltipModule } from '@ng-bootstrap/ng-bootstrap';
import { Observable } from 'rxjs';
import { SHARED_IMPORTS } from '../../../libs/SharedImports';
import { AppService } from '../../../services/app.service';
import { PagedContent } from '../../model/PagedContent';
import { Post } from '../../model/Post';
import { Serie } from '../../model/Serie';
import { Tag } from '../../model/Tag';
import { PostService } from '../../services/post.service';
import { TagService } from '../../services/tag.service';
import { GenericHubComponent } from '../generic-hub/generic-hub.component';
import { GenericInputComponent } from '../generic-input/generic-input.component';

@Component({
  selector: 'serie-hub',
  templateUrl: './../generic-hub/generic-hub.component.html',
  styleUrls: ['./../generic-hub/generic-hub.component.css'],
  imports: [SHARED_IMPORTS, GenericInputComponent, NgbTooltipModule],
  standalone: true
})
export class SerieHubComponent extends GenericHubComponent<Serie> implements OnInit {

  constructor(private tagService: TagService, postService: PostService, appService: AppService, formBuilder: FormBuilder, activeRoute: ActivatedRoute
  ) {
    super(appService, formBuilder, activeRoute, postService);
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
