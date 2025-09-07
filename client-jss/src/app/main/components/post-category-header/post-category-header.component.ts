import { Component, OnInit } from '@angular/core';
import { Meta, Title } from '@angular/platform-browser';
import { ActivatedRoute } from '@angular/router';
import { SHARED_IMPORTS } from '../../../libs/SharedImports';
import { AppService } from '../../../services/app.service';
import { JssCategory } from '../../model/JssCategory';
import { Responsable } from '../../model/Responsable';
import { AssoMailJssCategoryService } from '../../services/asso.mail.jss.category.service';
import { JssCategoryService } from '../../services/jss.category.service';
import { LoginService } from '../../services/login.service';
import { CategoryHubComponent } from '../category-hub/category-hub.component';

@Component({
  selector: 'post-category-header',
  templateUrl: './post-category-header.component.html',
  styleUrls: ['./post-category-header.component.css'],
  imports: [SHARED_IMPORTS, CategoryHubComponent],
  standalone: true
})
export class PostCategoryHeaderComponent implements OnInit {

  constructor(private jssCategoryService: JssCategoryService,
    private assoMailJssCategoryService: AssoMailJssCategoryService,
    private activeRoute: ActivatedRoute,
    private loginService: LoginService,
    private titleService: Title, private meta: Meta,
    private appService: AppService
  ) { }

  selectedJssCategory: JssCategory | undefined;
  isFollowed: Boolean = false;
  currentUser: Responsable | undefined;

  updateSelectedCategory(jssCategory: JssCategory) {
    this.selectedJssCategory = jssCategory;
  }

  ngOnInit() {
    this.titleService.setTitle("Tous nos articles - JSS");
    this.meta.updateTag({ name: 'description', content: "Retrouvez l'actualité juridique et économique. JSS analyse pour vous les dernières annonces, formalités et tendances locales." });
    let slug = this.activeRoute.snapshot.params['slug'];
    if (slug) {
      this.jssCategoryService.getJssCategoryBySlug(slug).subscribe(response => {
        if (response) {
          this.selectedJssCategory = response;
          this.titleService.setTitle("Tous nos articles - " + this.selectedJssCategory.name + " - JSS");
          this.meta.updateTag({ name: 'description', content: this.selectedJssCategory.name + " - Retrouvez l'actualité juridique et économique. JSS analyse pour vous les dernières annonces, formalités et tendances locales." });
          this.assoMailJssCategoryService.getAssoMailJssCategory(this.selectedJssCategory).subscribe(response => {
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
    if (this.selectedJssCategory) {
      this.assoMailJssCategoryService.followJssCategory(this.selectedJssCategory).subscribe(response => {
        if (response) {
          this.isFollowed = true;
        }
      });
    }
    else
      this.appService.displayToast("Veuillez vous connecter", true, "Une erreur s’est produite...", 3000);
  }

  unfollowAuthor() {
    if (this.isFollowed && this.selectedJssCategory) {
      this.assoMailJssCategoryService.unfollowJssCategory(this.selectedJssCategory).subscribe(response => {
        if (response)
          this.isFollowed = false;
      });
    }
  }
}
