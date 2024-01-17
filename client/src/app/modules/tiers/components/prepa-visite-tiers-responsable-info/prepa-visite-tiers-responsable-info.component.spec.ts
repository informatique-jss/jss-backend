/* tslint:disable:no-unused-variable */
import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { PrepaVisiteTiersResponsableInfoComponent } from './prepa-visite-tiers-responsable-info.component';

describe('ResponsableMainComponent', () => {
  let component: PrepaVisiteTiersResponsableInfoComponent;
  let fixture: ComponentFixture<PrepaVisiteTiersResponsableInfoComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ PrepaVisiteTiersResponsableInfoComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PrepaVisiteTiersResponsableInfoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
