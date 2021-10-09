import dayjs from 'dayjs';
import { IProduct } from 'app/shared/model/product.model';

export interface IBill {
  id?: number;
  createdDate?: string;
  totalPrice?: number;
  products?: IProduct[] | null;
}

export const defaultValue: Readonly<IBill> = {};
