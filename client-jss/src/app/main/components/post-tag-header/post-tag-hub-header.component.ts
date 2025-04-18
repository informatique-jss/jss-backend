import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Tag } from '../../model/Tag';
import { TagService } from '../../services/tag.service';

@Component({
  selector: 'post-tag-header',
  templateUrl: './post-tag-header.component.html',
  styleUrls: ['./post-tag-header.component.css'],
  standalone: false
})
export class PostTagHeaderComponent implements OnInit {
  constructor(private tagService: TagService,
    private activeRoute: ActivatedRoute
  ) { }

  selectedTag: Tag | undefined;

  ngOnInit() {
    let slug = this.activeRoute.snapshot.params['slug'];
    if (slug)
      this.tagService.getTagBySlug(slug).subscribe(response => {
        if (response)
          this.selectedTag = response;
      });
  }

}
