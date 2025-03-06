import { Post } from "../../general/model/Post";
import { CarouselItem } from "./CarouselItem";

export interface PostCarouselItem extends CarouselItem {
  post: Post;
}
