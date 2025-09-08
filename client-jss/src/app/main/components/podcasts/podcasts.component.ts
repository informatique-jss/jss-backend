import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { Meta, Title } from '@angular/platform-browser';
import { Observable } from 'rxjs';
import { SHARED_IMPORTS } from '../../../libs/SharedImports';
import { TimeFormatPipe } from '../../../libs/TimeFormatPipe';
import { AppService } from '../../../services/app.service';
import { ConstantService } from '../../../services/constant.service';
import { GtmService } from '../../../services/gtm.service';
import { CtaClickPayload, PageInfo } from '../../../services/GtmPayload';
import { Category } from '../../model/Category';
import { PagedContent } from '../../model/PagedContent';
import { Post } from '../../model/Post';
import { Tag } from '../../model/Tag';
import { AudioPlayerService } from '../../services/audio.player.service';
import { PostService } from '../../services/post.service';
import { TagService } from '../../services/tag.service';

@Component({
  selector: 'podcasts',
  templateUrl: './podcasts.component.html',
  styleUrls: ['./podcasts.component.css'],
  imports: [SHARED_IMPORTS, TimeFormatPipe],
  standalone: true
})
export class PodcastsComponent implements OnInit {

  categoryPodcast!: Category;
  hubForm!: FormGroup;

  openedListenDropdownPostId: number | null = null;

  postsByEntityType: Post[] = [] as Array<Post>;
  tagsByEntityType: Tag[] = [] as Array<Tag>;
  mostSeenPostsByEntityType: Post[] = [] as Array<Post>;

  constructor(
    private appService: AppService,
    private formBuilder: FormBuilder,
    private postService: PostService,
    private tagService: TagService,
    private titleService: Title, private meta: Meta,
    private audioService: AudioPlayerService,
    private gtmService: GtmService,
    private constantService: ConstantService) { }

  ngOnInit() {
    this.titleService.setTitle("Découvez notre podcast - JSS");
    this.meta.updateTag({ name: 'description', content: "Plongez au cœur de l'actualité juridique et économique avec le podcast de JSS. Nos experts décryptent pour vous les sujets qui façonnent le monde des affaires." });
    this.categoryPodcast = this.constantService.getCategoryPodcast();
    this.hubForm = this.formBuilder.group({});
    this.fetchPosts(0);
    this.fetchTags();
    this.fetchMostSeenPosts();
  }

  fetchPosts(page: number) {
    if (this.categoryPodcast)
      this.getAllPostByEntityType(this.categoryPodcast, page, 10).subscribe(data => {
        if (data) {
          this.postsByEntityType = data.content;
        }
      });
  }

  trackCtaClickExternalPodcastDeezer(podcastId: number) {
    this.gtmService.trackCtaClick(
      {
        cta: { type: 'link', label: "Lire sur Deezer", objectId: podcastId },
        page: {
          type: 'main',
          name: 'podcasts'
        } as PageInfo
      } as CtaClickPayload
    );
  }

  trackCtaClickExternalPodcastSpotify(podcastId: number) {
    this.gtmService.trackCtaClick(
      {
        cta: { type: 'link', label: "Lire sur Spotify", objectId: podcastId },
        page: {
          type: 'main',
          name: 'podcasts'
        } as PageInfo
      } as CtaClickPayload
    );
  }

  trackCtaClickExternalPodcastApplePodcast(podcastId: number) {
    this.gtmService.trackCtaClick(
      {
        cta: { type: 'link', label: "Lire sur Apple Podcast", objectId: podcastId },
        page: {
          type: 'main',
          name: 'podcasts'
        } as PageInfo
      } as CtaClickPayload
    );
  }

  getAllPostByEntityType(selectedEntityType: Category, page: number, pageSize: number): Observable<PagedContent<Post>> {
    return this.postService.getAllPostsByCategory(selectedEntityType, page, pageSize);
  }

  fetchTags() {
    this.getAllTagByEntityType(0, this.categoryPodcast).subscribe(data => {
      if (data)
        this.tagsByEntityType = data.content;
    })
  }

  getAllTagByEntityType(page: number, selectedEntityType: Category): Observable<PagedContent<Tag>> {
    return this.tagService.getAllTagsByCategory(page, 10, selectedEntityType);
  }

  fetchMostSeenPosts() {
    this.getMostSeenPosts(0, 5).subscribe(data => {
      if (data)
        this.mostSeenPostsByEntityType = data.content;
    });
  }

  getMostSeenPosts(page: number, pageSize: number): Observable<PagedContent<Post>> {
    return this.postService.getMostSeenPosts(page, pageSize, "");
  }



  // ----------------- Player methods ---------------------------------

  toggleListenDropdown(event: Event, postId: number): void {
    event.preventDefault();
    this.openedListenDropdownPostId = this.openedListenDropdownPostId === postId ? null : postId;
  }

  togglePlayPodcast(post: Post) {
    if (this.audioService.currentPost && this.audioService.currentPost.id == post.id) {
      this.audioService.togglePlayPause();
    } else {
      this.audioService.loadTrack(post.id);
    }
  }

  isPlayingPodcast(post: Post) {
    return this.audioService.isPlayingPost(post);
  }
}
