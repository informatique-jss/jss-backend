import { Component, OnInit } from '@angular/core';
import { Meta, Title } from '@angular/platform-browser';
import { ActivatedRoute } from '@angular/router';
import { SHARED_IMPORTS } from '../../../libs/SharedImports';
import { AppService } from '../../../services/app.service';
import { Author } from '../../model/Author';
import { Responsable } from '../../model/Responsable';
import { AssoMailAuthorService } from '../../services/asso.mail.author.service';
import { AuthorService } from '../../services/author.service';
import { LoginService } from '../../services/login.service';
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
    private loginService: LoginService,
    private titleService: Title, private meta: Meta,
    private appService: AppService
  ) { }

  selectedAuthor: Author | undefined;
  isFollowed: Boolean = false;
  currentUser: Responsable | undefined;

  ngOnInit() {
    this.refresh();
    this.activeRoute.paramMap.subscribe(params => {
      this.refresh();
    });
  }

  refresh() {
    this.titleService.setTitle("Tous nos articles - JSS");
    this.meta.updateTag({ name: 'description', content: "Retrouvez l'actualité juridique et économique. JSS analyse pour vous les dernières annonces, formalités et tendances locales." });
    let slug = this.activeRoute.snapshot.params['slug'];
    if (slug) {
      this.authorService.getAuthorBySlug(slug).subscribe(response => {
        if (response) {
          this.selectedAuthor = response;
          this.titleService.setTitle("Tous nos articles - " + this.selectedAuthor.name + " - JSS");
          this.meta.updateTag({ name: 'description', content: this.selectedAuthor.name + " - Retrouvez l'actualité juridique et économique. JSS analyse pour vous les dernières annonces, formalités et tendances locales." });
          this.assoMailAuthorService.getAssoMailAuthor(this.selectedAuthor).subscribe(response => {
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
