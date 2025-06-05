import { AfterContentChecked, ChangeDetectorRef, Component, Input, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { SHARED_IMPORTS } from '../../../../libs/SharedImports';
import { CommunicationPreference } from '../../../general/model/CommunicationPreference';
import { CommunicationPreferencesService } from '../../../general/services/communication.preference.service';
import { AppService } from '../../../main/services/app.service';
import { GenericToggleComponent } from '../../../miscellaneous/components/forms/generic-toggle/generic-toggle.component';
import { LoginService } from '../../../profile/services/login.service';
import { Author } from '../../../tools/model/Author';
import { JssCategory } from '../../../tools/model/JssCategory';
import { Tag } from '../../../tools/model/Tag';
import { AssoMailAuthorService } from '../../../tools/services/asso.mail.author.service';
import { AssoMailJssCategoryService } from '../../../tools/services/asso.mail.jss.category.service';
import { AssoMailTagService } from '../../../tools/services/asso.mail.tag.service';
import { AuthorService } from '../../../tools/services/author.service';
import { JssCategoryService } from '../../../tools/services/jss.category.service';
import { TagService } from '../../../tools/services/tag.service';


@Component({
  selector: 'communication-preference',
  templateUrl: './communication-preference.component.html',
  styleUrls: ['./communication-preference.component.css'],
  standalone: true,
  imports: [SHARED_IMPORTS, GenericToggleComponent]
})
export class CommunicationPreferenceComponent implements OnInit, AfterContentChecked {

  @Input() mail: string | undefined;
  @Input() validationToken: string | null = null;

  communicationPreference: CommunicationPreference = {} as CommunicationPreference;
  communicationPreferenceForm!: FormGroup;
  followedAuthors: Author[] = [];
  followedTags: Tag[] = [];
  followedJssCategories: JssCategory[] = [];
  isFollowedAuthor: { [key: number]: boolean } = {};
  isFollowedTag: { [key: number]: boolean } = {};
  isFollowedJssCategory: { [key: number]: boolean } = {};
  followedItems: string[] = ['Rédacteurs', 'Catégories', 'Tags'];
  selectedTab: string = this.followedItems[0];

  constructor(
    private communicationPreferenceService: CommunicationPreferencesService,
    private appService: AppService,
    private loginService: LoginService,
    private formBuilder: FormBuilder,
    private changeDetectorRef: ChangeDetectorRef,
    private authorService: AuthorService,
    private tagService: TagService,
    private jssCategoryService: JssCategoryService,
    private assoMailAuthorService: AssoMailAuthorService,
    private assoMailTagService: AssoMailTagService,
    private assoMailJssCategoryService: AssoMailJssCategoryService,
  ) { }

  ngOnInit() {
    this.communicationPreferenceForm = this.formBuilder.group({});
    if (!this.mail) {
      this.loginService.getCurrentUser().subscribe((user) => {
        this.mail = user.mail.mail;
        this.loadPreferenceByMail(this.mail!);
      });
    } else {
      this.loadPreferenceByMail(this.mail);
    }

    this.authorService.getFollowedAuthorForCurrentUser().subscribe(response => {
      if (response) {
        this.followedAuthors = response;
        for (let author of this.followedAuthors)
          this.isFollowedAuthor[author.id] = true;
      }
    });

    this.tagService.getFollowedTagForCurrentUser().subscribe(response => {
      if (response) {
        this.followedTags = response;
        for (let tag of this.followedTags)
          this.isFollowedTag[tag.id] = true;
      }
    });

    this.jssCategoryService.getFollowedJssServiceForCurrentUser().subscribe(response => {
      if (response) {
        this.followedJssCategories = response;
        for (let jssCategory of this.followedJssCategories)
          this.isFollowedJssCategory[jssCategory.id] = true;
      }
    });
  }

  loadPreferenceByMail(mail: string) {
    this.communicationPreferenceService.getCommunicationPreferenceByMail(mail, this.validationToken).subscribe((preferences) => {
      this.communicationPreference = preferences;
    });
  }

  ngAfterContentChecked(): void {
    this.changeDetectorRef.detectChanges();
  }

  toggleNewspaperNewsletter() {
    if (this.mail)
      if (!this.communicationPreference.isSubscribedToNewspaperNewletter) {
        this.communicationPreferenceService.subscribeToNewspaperNewsletter(this.mail).subscribe();
      } else {
        this.communicationPreferenceService.unsubscribeToNewspaperNewsletter(this.mail, this.validationToken).subscribe();
      }
  }

  toggleCorporateNewsletter() {
    if (this.mail)
      if (!this.communicationPreference.isSubscribedToCorporateNewsletter) {
        this.communicationPreferenceService.subscribeToCorporateNewsletter(this.mail).subscribe();
      } else {
        this.communicationPreferenceService.unsubscribeToCorporateNewsletter(this.mail, this.validationToken).subscribe();
      }
  }

  followAuthor(author: Author) {
    if (!this.isFollowedAuthor[author.id])
      this.assoMailAuthorService.followAuthor(author).subscribe(response => {
        if (response) {
          this.isFollowedAuthor[author.id] = true;
        }
      });
  }

  unfollowAuthor(author: Author) {
    if (this.isFollowedAuthor[author.id])
      this.assoMailAuthorService.unfollowAuthor(author).subscribe(response => {
        if (response) {
          this.isFollowedAuthor[author.id] = false;
        }
      });
  }

  followTag(tag: Tag) {
    if (!this.isFollowedTag[tag.id])
      this.assoMailTagService.followTag(tag).subscribe(response => {
        if (response) {
          this.isFollowedTag[tag.id] = true;
        }
      });
  }

  unfollowTag(tag: Tag) {
    if (this.isFollowedTag[tag.id])
      this.assoMailTagService.unfollowTag(tag).subscribe(response => {
        if (response) {
          this.isFollowedTag[tag.id] = false;
        }
      });
  }

  followJssCategory(jssCategory: JssCategory) {
    if (!this.isFollowedJssCategory[jssCategory.id])
      this.assoMailJssCategoryService.followJssCategory(jssCategory).subscribe(response => {
        if (response) {
          this.isFollowedJssCategory[jssCategory.id] = true;
        }
      });
  }

  unfollowJssCategory(jssCategory: JssCategory) {
    if (this.isFollowedJssCategory[jssCategory.id])
      this.assoMailJssCategoryService.unfollowJssCategory(jssCategory).subscribe(response => {
        if (response) {
          this.isFollowedAuthor[jssCategory.id] = false;
        }
      });
  }

  openAuthorNewPosts(event: any, author: Author) {
    this.appService.openJssRoute(event, "post/author/" + author.slug + "/true", false);
  }
  openTagNewPosts(event: any, tag: Tag) {
    this.appService.openJssRoute(event, "post/tag/" + tag.slug + "/true", false);
  }
  openJssCategoryNewPosts(event: any, jssCategory: JssCategory) {
    this.appService.openJssRoute(event, "post/category/" + jssCategory.slug + "/true", false);
  }
}
