import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { SHARED_IMPORTS } from '../../../../libs/SharedImports';
import { ResponsableDto } from '../../model/ResponsableDto';
import { ResponsableService } from '../../services/responsable.service';
import { ResponsableHeaderComponent } from '../responsable-header/responsable-header.component';

@Component({
  selector: 'app-responsable',
  templateUrl: './responsable.component.html',
  styleUrls: ['./responsable.component.css'],
  standalone: true,
  imports: [...SHARED_IMPORTS,
    ResponsableHeaderComponent,
  ]
})
export class ResponsableComponent implements OnInit {

  responsableId: number | undefined;
  responsable: ResponsableDto | undefined;

  constructor(private activeRoute: ActivatedRoute,
    private responsableService: ResponsableService
  ) { }

  ngOnInit() {
    if (this.activeRoute.snapshot.params['id'])
      this.responsableId = this.activeRoute.snapshot.params['id'];

    if (this.responsableId)
      this.responsableService.getResponsable(this.responsableId).subscribe(response => {
        this.responsable = response;
      })
  }

}
