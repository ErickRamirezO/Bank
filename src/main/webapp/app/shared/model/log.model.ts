import dayjs from 'dayjs';
import { IUser } from 'app/shared/model/user.model';

export interface ILog {
  id?: number;
  eventType?: string;
  eventTime?: dayjs.Dayjs | null;
  ipAddress?: string | null;
  description?: string | null;
  user?: IUser | null;
}

export const defaultValue: Readonly<ILog> = {};
