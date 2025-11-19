import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { SHARED_IMPORTS } from '../../../../libs/SharedImports';
import { PageTitleComponent } from '../../../main/components/page-title/page-title.component';
import { Tiers } from '../../../profile/model/Tiers';
import { TiersService } from '../../services/tiers.service';

@Component({
  selector: 'app-tiers',
  templateUrl: './tiers.component.html',
  styleUrls: ['./tiers.component.css'],
  standalone: true,
  imports: [...SHARED_IMPORTS, PageTitleComponent]
})
export class TiersComponent implements OnInit {

  tiersId: number | undefined;
  tiers: Tiers | undefined;

  constructor(private activeRoute: ActivatedRoute,
    private tiersService: TiersService
  ) { }

  ngOnInit() {
    if (this.activeRoute.snapshot.params['id'])
      this.tiersId = this.activeRoute.snapshot.params['id'];

    if (this.tiersId)
      this.tiersService.getTiersById(this.tiersId).subscribe(response => {
        this.tiers = response;
      })
  }

}
