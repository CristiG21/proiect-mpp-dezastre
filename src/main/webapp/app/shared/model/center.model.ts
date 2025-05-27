import { IUser } from 'app/shared/model/user.model';

export interface ICenter {
  id?: number;
  name?: string | null;
  longitude?: number | null;
  latitude?: number | null;
  status?: boolean | null;
  description?: string | null;
  availableSeats?: number | null;
  openTime?: string;
  closeTime?: string;
  user?: IUser | null;
}

export const defaultValue: Readonly<ICenter> = {
  status: false,
};
