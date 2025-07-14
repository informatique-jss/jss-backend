import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { SHARED_IMPORTS } from '../../../libs/SharedImports';
import { AppService } from '../../../services/app.service';
import { Responsable } from '../../model/Responsable';
import { Tag } from '../../model/Tag';
import { AssoMailTagService } from '../../services/asso.mail.tag.service';
import { LoginService } from '../../services/login.service';
import { TagService } from '../../services/tag.service';
import { TagHubComponent } from '../tag-hub/tag-hub.component';

@Component({
  selector: 'post-tag-header',
  templateUrl: './post-tag-header.component.html',
  styleUrls: ['./post-tag-header.component.css'],
  imports: [SHARED_IMPORTS, TagHubComponent],
  standalone: true
})
export class PostTagHeaderComponent implements OnInit {
  constructor(private tagService: TagService,
    private assoMailTagService: AssoMailTagService,
    private activeRoute: ActivatedRoute,
    private loginService: LoginService,
    private appService: AppService
  ) { }

  selectedTag: Tag | undefined;
  isFollowed: Boolean = false;
  currentUser: Responsable | undefined;

  ngOnInit() {
    let slug = this.activeRoute.snapshot.params['slug'];
    if (slug) {
      this.tagService.getTagBySlug(slug).subscribe(response => {
        if (response) {
          this.selectedTag = response;
          this.assoMailTagService.getAssoMailTag(this.selectedTag).subscribe(response => {
            if (response) {
              this.isFollowed = true;
            }
          });
        }
      });
      this.loginService.getCurrentUser().subscribe(response => {
        this.currentUser = response;
      });
    }
  }

  followAuthor() {
    if (this.selectedTag) {
      this.assoMailTagService.followTag(this.selectedTag).subscribe(response => {
        if (response) {
          this.isFollowed = true;
        }
      });
    }
    else
      this.appService.displayToast("Veuillez vous connecter", true, "Une erreur s’est produite...", 3000);
  }

  unfollowAuthor() {
    if (this.isFollowed && this.selectedTag) {
      this.assoMailTagService.unfollowTag(this.selectedTag).subscribe(response => {
        if (response)
          this.isFollowed = false;
      });
    }
  }
}
