import {
	ICatalog,
	ICatalogSub,
} from '../Catalog/Catalog-data/CatalogInterface/Catalog.interface'
import { ICommentaries } from './ListingComponents/ListingComentaries/Commentaries.interface'

export interface IListingItem {
	id: number
	user: {
		id: number
		firstName: string
		lastName: string
		email: string
		phoneNumber: number
		profileImageUrl: string
	}

	title: string
	averageProfit: number
	fullAddress: string
	averageRating: number
	totalRating: number
	category: ICatalog
	subCategory: ICatalogSub
	city: string
	country: string
	paybackPeriod: number
	ratings: number[]
	description: string
	price: number
	fields: Record<string, unknown>
	images: string[]
	mainImage: string
	viewCount: number
	filters: string[]
	videoUrl: string
	updatedAt: string
	createdAt: string
	invest: boolean
	status: 'INACTIVE' | 'ACTIVE' | 'REJECTED'
	comments: ICommentaries[]
	premiumSubscription: boolean
	similarListings: IListingItem[]
	content: IListingItem[]
}

export interface IPaginatedListingResponse {
	content: IListingItem[]
	totalElements: number
	totalPages: number
	number: number
	size: number
}
