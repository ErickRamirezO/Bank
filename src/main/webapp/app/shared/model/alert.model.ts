import dayjs from 'dayjs';
import { IUser } from 'app/shared/model/user.model';

export interface IAlert {
  id?: number;
  alertType?: string;
  alertTime?: dayjs.Dayjs | null;
  emailSent?: boolean;
  details?: string | null;
  user?: IUser | null;
}

export const defaultValue: Readonly<IAlert> = {
  emailSent: false,
};
