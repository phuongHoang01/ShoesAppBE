import dayjs from 'dayjs';
import { IUser } from 'app/shared/model/user.model';
import { IProduct } from 'app/shared/model/product.model';

export interface IFavorite {
  id?: number;
  createdDate?: string | null;
  user?: IUser | null;
  product?: IProduct | null;
}

export const defaultValue: Readonly<IFavorite> = {};
