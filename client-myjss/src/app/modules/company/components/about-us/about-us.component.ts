import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { Meta, Title } from '@angular/platform-browser';
import { SHARED_IMPORTS } from '../../../../libs/SharedImports';
import { GtmService } from '../../../main/services/gtm.service';
import { CtaClickPayload, PageInfo } from '../../../main/services/GtmPayload';
import { PlatformService } from '../../../main/services/platform.service';

@Component({
  selector: 'about-us',
  templateUrl: './about-us.component.html',
  styleUrls: ['./about-us.component.css'],
  standalone: true,
  imports: [SHARED_IMPORTS]
})
export class AboutUsComponent implements OnInit {

  @ViewChild('modalImage') modalImageRef!: ElementRef<HTMLImageElement>;

  constructor(private platformService: PlatformService,
    private gtmService: GtmService,
    private titleService: Title, private meta: Meta,
  ) { }

  ngOnInit() {
    this.titleService.setTitle("À propos - MyJSS");
    this.meta.updateTag({ name: 'description', content: "Découvrez MyJSS, votre partenaire expert en annonces et formalités légales. Notre mission : vous offrir un service premium, alliant technologie et savoir-faire." });
  }

  trackClickContact() {
    this.gtmService.trackCtaClick(
      {
        cta: { type: 'link', label: 'Nous contacter' },
        page: {
          type: 'company',
          name: 'about-us'
        } as PageInfo
      } as CtaClickPayload
    );
  }

  ngAfterViewInit(): void {
    if (this.platformService.getNativeDocument())
      import('jarallax').then(module => {
        module.jarallax(this.platformService.getNativeDocument()!.querySelectorAll('.jarallax'), {
          speed: 0.5
        });
      });
  }

  openImageModal(imageUrl: string): void {
    if (this.modalImageRef) {
      this.modalImageRef.nativeElement.src = imageUrl;
    }
  }
}
