import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { AzureReceipt } from '../model/AzureReceipt';

@Injectable({
  providedIn: 'root'
})
export class AzureReceiptService extends AppRestService<AzureReceipt>{

  constructor(http: HttpClient) {
    super(http, "invoicing");
  }

  getAzureReceipt(azureReceiptId: number) {
    return this.get(new HttpParams().set("idAzureReceipt", azureReceiptId), "azure-receipt");
  }

}
