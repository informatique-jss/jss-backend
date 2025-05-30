import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { SHARED_IMPORTS } from '../../../libs/SharedImports';
import { AppService } from '../../../services/app.service';
import { Author } from '../../model/Author';
import { AssoMailAuthorService } from '../../services/asso.mail.author.service';
import { AuthorService } from '../../services/author.service';
import { AuthorHubComponent } from '../author-hub/author-hub.component';

@Component({
  selector: 'app-post-author-header',
  templateUrl: './post-author-header.component.html',
  styleUrls: ['./post-author-header.component.css'],
  imports: [SHARED_IMPORTS, AuthorHubComponent],
  standalone: true
})
export class PostAuthorHeaderComponent implements OnInit {

  constructor(private assoMailAuthorService: AssoMailAuthorService,
    private authorService: AuthorService,
    private activeRoute: ActivatedRoute,
    private appService: AppService
  ) { }

  selectedAuthor: Author | undefined;
  isFollowed: Boolean = false;

  ngOnInit() {
    let slug = this.activeRoute.snapshot.params['slug'];
    if (slug)
      this.authorService.getAuthorBySlug(slug).subscribe(response => {
        if (response) {
          this.selectedAuthor = response;
          this.assoMailAuthorService.getAssoMailAuthor(this.selectedAuthor).subscribe(response => {
            if (response) {
              this.isFollowed = true;
            }
          });
        }
      });
  }

  followAuthor() {
    if (this.selectedAuthor) {
      this.assoMailAuthorService.followAuthor(this.selectedAuthor).subscribe(response => {
        if (response) {
          this.isFollowed = true;
        }
      });
    }
    else
      this.appService.displayToast("Veuillez vous connecter", true, "Une erreur s’est produite...", 3000);
  }

  unfollowAuthor() {
    if (this.isFollowed && this.selectedAuthor) {
      this.assoMailAuthorService.unfollowAuthor(this.selectedAuthor).subscribe(response => {
        if (response)
          this.isFollowed = false;
      });
    }
  }
}
