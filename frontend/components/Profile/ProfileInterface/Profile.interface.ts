import { IListingItem } from '@/components/Listing/Listing.interface'
export interface IUser {
	id: number;
	accountStatus: string | null,
	profilePictureUrl: string | null,
	birthDate?: string | null,
	city?: string | null,
	country?: string | null,
	createAt: string | null,
	email?: string | null,
	phoneNumber?: string | null,
	firstName: string,
	fullAddress?: string,
	favoriteListings?: IFavoriteListings,
	lastName: string,
	roles:IUserRoles
	password: string | null,
	userStatus: string | null,
}

export interface IFavoriteListings {
	favoriteListings: IListingItem[];
}

export interface IUserRoles {
	id: number,
	name: string;
}


