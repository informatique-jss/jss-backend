import { Component, OnInit } from '@angular/core';
import { Subscription } from 'rxjs';
import { SHARED_IMPORTS } from '../../../libs/SharedImports';
import { ToastComponent } from '../../../libs/toast/toast.component';
import { PlatformService } from '../../../services/platform.service';
import { AudioPlayerService } from '../../services/audio.player.service';
import { AudioPlayerComponent } from '../audio-player/audio-player.component';
import { CookieConsentComponent } from '../cookie-consent/cookie-consent.component';
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
    CookieConsentComponent],
  standalone: true
})
export class DefaultComponent implements OnInit {
  currentPodcastId: number = 0;
  currentPodcastSubscription: Subscription = new Subscription;
  isBrowser: boolean = true;

  constructor(
    private audioService: AudioPlayerService,
    private platformService: PlatformService,
  ) { }

  ngOnInit() {
    if (this.platformService) {
      this.isBrowser = this.platformService.isBrowser();
      if (this.isBrowser)
        this.currentPodcastSubscription = this.audioService.currentPostObservable.subscribe(item => this.currentPodcastId = item);
    }
  }

  ngOnDestroy() {
    this.currentPodcastSubscription.unsubscribe();
  }
}
