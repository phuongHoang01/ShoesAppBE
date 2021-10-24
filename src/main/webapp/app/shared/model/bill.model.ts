import dayjs from 'dayjs';
import { IProduct } from 'app/shared/model/product.model';
import { IUser } from 'app/shared/model/user.model';

export interface IBill {
  id?: number;
  createdDate?: string;
  totalPrice?: number;
  products?: IProduct[] | null;
  user?: IUser | null;
}

export const defaultValue: Readonly<IBill> = {};
