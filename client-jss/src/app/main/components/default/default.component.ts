import { Component, OnInit } from '@angular/core';
import { Subscription } from 'rxjs';
import { SHARED_IMPORTS } from '../../../libs/SharedImports';
import { ToastComponent } from '../../../libs/toast/toast.component';
import { PlatformService } from '../../../services/platform.service';
import { AudioService } from '../../services/audio.service';
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
  isCurrentPodcastDisplayed: boolean = false;
  currentPodcastSubscription: Subscription = new Subscription;

  constructor(
    private audioService: AudioService,
    private platformService: PlatformService
  ) { }

  ngOnInit() {
    if (this.platformService.isBrowser())
      this.currentPodcastSubscription = this.audioService.currentPodcastObservable.subscribe(item => this.isCurrentPodcastDisplayed = item);
  }

  ngOnDestroy() {
    this.currentPodcastSubscription.unsubscribe();
  }

}
