import { ICategory } from 'app/shared/model/category.model';
import { ISize } from 'app/shared/model/size.model';

export interface IProduct {
  id?: number;
  name?: string;
  description?: string | null;
  price?: number;
  image?: string;
  productSize?: number;
  color?: string | null;
  quantity?: number | null;
  category?: ICategory | null;
  sizes?: ISize[] | null;
}

export const defaultValue: Readonly<IProduct> = {};
