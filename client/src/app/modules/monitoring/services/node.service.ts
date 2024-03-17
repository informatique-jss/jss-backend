import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { AppRestService } from 'src/app/services/appRest.service';
import { Node } from '../model/Node';

@Injectable({
  providedIn: 'root'
})
export class NodeService extends AppRestService<Node>{

  constructor(http: HttpClient) {
    super(http, "node");
  }

  getNodes(): Observable<Node[]> {
    return this.getList(new HttpParams(), "nodes");
  }

  performGc(node: Node): Observable<Node> {
    return this.get(new HttpParams().set("nodeId", node.id), "node/gc");
  }

  stopNode(node: Node): Observable<Node> {
    return this.get(new HttpParams().set("nodeId", node.id), "node/stop");
  }

  restartNode(node: Node): Observable<Node> {
    return this.get(new HttpParams().set("nodeId", node.id), "node/restart");
  }
}
