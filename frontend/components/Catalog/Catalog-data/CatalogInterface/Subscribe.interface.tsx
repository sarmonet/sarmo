import { FilterParams } from '@/services/getListings'

export interface ISubscribe {
  id: number;
  preferredCommunicationChannel?: "EMAIL" | "APPLICATION" ;
  categorySubscription?: {
    frequency?: "DAILY" | "WEEKLY" | "MONTHLY";
    filters?: FilterParams; 
    active?: boolean;
  };
}
export interface ISubscribeChange {
  preferredCommunicationChannel?: "EMAIL" | "APPLICATION" ;
  categorySubscription?: {
    frequency?: "DAILY" | "WEEKLY" | "MONTHLY";
    active?: boolean;
  };
}