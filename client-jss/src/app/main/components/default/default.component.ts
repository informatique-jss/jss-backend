import { Component, OnInit } from '@angular/core';
import { NavigationEnd, Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { SHARED_IMPORTS } from '../../../libs/SharedImports';
import { ToastComponent } from '../../../libs/toast/toast.component';
import { PlatformService } from '../../../services/platform.service';
import { AudioPlayerService } from '../../services/audio.player.service';
import { AudioPlayerComponent } from '../audio-player/audio-player.component';
import { FooterComponent } from '../footer/footer.component';
import { HeaderComponent } from '../header/header.component';

@Component({
  selector: 'default',
  templateUrl: './default.component.html',
  styleUrls: ['./default.component.css'],
  imports: [SHARED_IMPORTS,
    HeaderComponent,
    AudioPlayerComponent,
    FooterComponent,
    ToastComponent,
  ],
  standalone: true
})
export class DefaultComponent implements OnInit {
  currentPodcastId: number = 0;
  currentPodcastSubscription: Subscription = new Subscription;
  navigationCompleted: boolean = false;

  constructor(
    private audioService: AudioPlayerService,
    private platformService: PlatformService,
    private router: Router,
  ) { }

  ngOnInit() {
    if (this.platformService.isBrowser())
      this.currentPodcastSubscription = this.audioService.currentPostObservable.subscribe(item => this.currentPodcastId = item);
    this.router.events.subscribe(event => {
      if (event instanceof NavigationEnd) {
        this.navigationCompleted = true;
      }
    });
  }

  ngOnDestroy() {
    this.currentPodcastSubscription.unsubscribe();
  }
}
