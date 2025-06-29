import { IListingItem } from '@/components/Listing/Listing.interface'
import {
	axiosInstance,
	axiosInstanceCommentaries,
	axiosInstanceFavorite,
	axiosInstanceRandom,
} from '@/utils/axiosInstance'
import { AxiosError } from 'axios'
import { useCallback } from 'react'

export interface Filters {
	country?: string | null
	city?: string | null
	minPrice?: number | null
	maxPrice?: number | null
	[key: string]: unknown
}

interface SqlFilters {
	category?: number | null
	subCategory?: number | null
	country?: string | null
	city?: string | null
	invest?: boolean | null
	minPrice?: number | null
	maxPrice?: number | null
	[key: string]: unknown
}

export interface FilterParams {
	sortBy?: string
	sortOrder?: string
	sqlFilters?: SqlFilters
	mongoFilters?: Record<string, unknown>
	page?: number
	size?: number
}
type FieldValue = string | number | boolean | string[]
export interface ICreateListing {
	title: string
	categoryId: number
	subCategoryId: number
	invest: boolean
	description: string
	price: number
	mainImage?: string
	images?: string[]
	videoUrl?: string
	country: string
	city: string
	fullAddress: string
	premiumStartDate?: Date | string | null
	premiumEndDate?: Date | string | null
	fields: Record<string, FieldValue>
	// fields: Record<string, unknown>
	status: 'INACTIVE' | 'ACTIVE'
}
export interface IUpdateListing {
	title?: string
	invest?: boolean
	description?: string
	price?: number
	mainImage?: string
	images?: string[]
	videoUrl?: string
	country?: string
	city?: string
	fullAddress?: string
	fields?: Record<string, unknown>
	status?: 'INACTIVE' | 'ACTIVE'
}

interface FilteredListings {
	premiumListings: IListingItem[]
	paginatedListings: IListingItem[]
}

export const getListings = async (page = 0) => {
	try {
		const response = await axiosInstance.get(
			`/active?page=${page - 1}&size=12&sortOrder=desc`
		)
		return response.data
	} catch (error) {
		console.error('Ошибка получения объявлений:', error)
		return null
	}
}

export const getRandomListings = async ({ count }: { count: number }) => {
	try {
		const response = await axiosInstanceRandom.get(`/random/all?count=${count}`)
		return response.data
	} catch (error) {
		console.error('Ошибка получения объявлений:', error)
		return null
	}
}

export const postListings = async (createParams: ICreateListing) => {
	try {
		const response = await axiosInstance.post('', createParams, {
			headers: {
				Authorization: `Bearer ${localStorage.getItem('accessToken')}`,
			},
		})
		return response.data
	} catch (error) {
		console.error('Ошибка создания объявления:', error)
		return null
	}
}
export const updateListing = async (
	listingId: number,
	updateParams: IUpdateListing
) => {
	try {
		const response = await axiosInstance.put(`/${listingId}`, updateParams, {
			headers: {
				Authorization: `Bearer ${localStorage.getItem('accessToken')}`,
			},
		})
		return response.data
	} catch (error) {
		console.error('Error updating listing:', error)
		throw error
	}
}

export const getListingsByFilter = async (filterParams: FilterParams) => {
	try {
		const response = await axiosInstance.post('/filter', filterParams)
		return response.data
	} catch (error) {
		console.error('Ошибка фильтрации объявлений:', error)
		return null
	}
}

export const getFieldsById = async ({ id }: { id: number }) => {
	try {
		const response = await axiosInstance.get(`/category/fields/${id}`)
		return response.data
	} catch (error) {
		console.error('Ошибка получения полей:', error)
		return null
	}
}
export const getInvestFieldsById = async ({ id }: { id: number }) => {
	try {
		const response = await axiosInstance.get(
			`/investment-category/fields/${id}`
		)
		return response.data
	} catch (error) {
		console.error('Ошибка получения полей:', error)
		return null
	}
}
export const getUserListings = async () => {
	try {
		const { data } = await axiosInstance.get(`/user`, {
			headers: {
				Authorization: `Bearer ${localStorage.getItem('accessToken')}`,
				'Content-Type': 'application/json',
			},
			withCredentials: true,
		})

		if (Array.isArray(data) && data.length > 0) {
			return data
		} else {
			throw new Error('Данные пользователя отсутствуют в ответе сервера.')
		}
	} catch (error) {
		console.error('❌ Ошибка при получении данных пользователя:', error)
		throw error
	}
}
export const getListingById = async ({ id }: { id: number }) => {
	//
	try {
		const response = await axiosInstance.get(`/full/${id}`)
		return response.data
	} catch (error) {
		console.error('Ошибка получения объявления:', error)
		return null
	}
}

export const getTotalCount = async () => {
	try {
		const response = await axiosInstance.get('/count')
		return response.data
	} catch (error) {
		console.error('Ошибка получения общего количества:', error)
		return null
	}
}

export const getCountByCategoryId = async ({ id }: { id: number }) => {
	try {
		const response = await axiosInstance.get(`/count/category/${id}`)
		return response.data
	} catch (error) {
		console.error('Ошибка получения количества по категории:', error)
		return null
	}
}

export const getCountBySubCategoryId = async ({ id }: { id: number }) => {
	try {
		const response = await axiosInstance.get(`/count/subcategory/${id}`)
		return response.data
	} catch (error) {
		console.error('Ошибка получения количества по подкатегории:', error)
		return null
	}
}

export const getCategoryFields = async ({ id }: { id: number }) => {
	try {
		const response = await axiosInstance.get(`/category/fields/${id}`)
		return response.data
	} catch (error) {
		console.error('Ошибка получения полей категории:', error)
		return null
	}
}

export const getCategoryMongoFilter = async ({ id }: { id: number }) => {
	try {
		const response = await axiosInstance.get(`/mongo/filter/${id}`)
		return response.data
	} catch (error) {
		console.error('Ошибка получения Mongo фильтра:', error)
		return null
	}
}

export const getInactive = async (page: number) => {
	try {
		// const token = localStorage.getItem("accessToken");
		// if (!token) throw new Error("Требуется авторизация");

		const response = await axiosInstance.get(
			`/inactive?page=${page - 1}&size=12`,
			{
				// headers: { Authorization: `Bearer ${token}` },
			}
		)

		return response.data
	} catch (error) {
		const err = error as AxiosError

		console.error('Ошибка получения избранного:', err.message)

		throw err
	}
}
export const putStatus = async (
	{ id }: { id: number },
	status: string
): Promise<void> => {
	try {
		const token = localStorage.getItem('accessToken')
		await axiosInstance.put(
			`/admin/${id}`,
			{
				status: status,
			},
			{
				headers: { Authorization: `Bearer ${token}` },
			}
		)
	} catch (error) {
		const err = error as AxiosError
		console.error(
			'Ошибка при изменении статуса объявления:',
			err.message || err
		)
		throw err
	}
}

export const getFavoriteAll = async () => {
	try {
		const token = localStorage.getItem('accessToken')
		if (!token) throw new Error('Требуется авторизация')

		const response = await axiosInstanceFavorite.get('/listing', {
			headers: { Authorization: `Bearer ${token}` },
		})
		return response.data
	} catch (error) {
		const err = error as AxiosError

		console.error('Ошибка получения избранного:', err.message)

		throw err
	}
}

export const getFavoriteById = async (listingId: number) => {
	try {
		const token = localStorage.getItem('accessToken')
		if (!token) throw new Error('Требуется авторизация')

		const response = await axiosInstanceFavorite.get(`/listing/${listingId}`, {
			headers: { Authorization: `Bearer ${token}` },
		})
		return response.data
	} catch (error) {
		console.error('Ошибка получения избранного:', error)
		return null
	}
}

export const postFavorite = async (listingId: number) => {
	try {
		const token = localStorage.getItem('accessToken')
		if (!token) throw new Error('Требуется авторизация')

		const response = await axiosInstanceFavorite.post(
			`/listing/${listingId}`,
			{},
			{ headers: { Authorization: `Bearer ${token}` } }
		)
		return response.data
	} catch (error) {
		console.error('Ошибка добавления в избранное:', error)
		return null
	}
}

export const delFavorite = async (listingId: number) => {
	try {
		const token = localStorage.getItem('accessToken')
		if (!token) throw new Error('Требуется авторизация')

		const response = await axiosInstanceFavorite.delete(
			`/listing/${listingId}`,
			{
				headers: { Authorization: `Bearer ${token}` },
			}
		)
		return response.data
	} catch (error) {
		console.error('Ошибка удаления из избранного:', error)
		return null
	}
}
export const delListing = async (listingId: number) => {
	try {
		const token = localStorage.getItem('accessToken')
		if (!token) throw new Error('Требуется авторизация')

		const response = await axiosInstanceCommentaries.delete(`/${listingId}`, {
			headers: { Authorization: `Bearer ${token}` },
		})
		return response.data
	} catch (error) {
		console.error('Ошибка удаления из избранного:', error)
		return null
	}
}
export const useApplyFilters = (
	activeCategory: { id: number } | null,
	selectedSubCategory: { id: number } | null,
	filters: Filters | null,
	setFilteredListings: (data: FilteredListings) => void,
	setLoading: (loading: boolean) => void
) => {
	return useCallback(async () => {
		if (!activeCategory && !filters) return

		setLoading(true)
		try {
			// const filterParams: FilterParams = {
			// 	sqlFilters: {
			// 		category: activeCategory?.id || null,
			// 		subCategory: selectedSubCategory?.id || null,
			// 		...(filters
			// 			? {
			// 					country: filters.country ?? null,
			// 					city: filters.city ?? null,
			// 					minPrice: filters.minPrice ?? null,
			// 					maxPrice: filters.maxPrice ?? null,
			// 			  }
			// 			: {}),
			// 	},
			// 	mongoFilters: {},
			// }
			// console.log('filterParams', filterParams)
		} catch (error) {
			console.error('Ошибка фильтрации:', error)
		} finally {
			setLoading(false)
		}
	}, [])
}

export const getPackagingConfig = async () => {
	try {
		const token = localStorage.getItem('accessToken')
		if (!token) throw new Error('Требуется авторизация')

		const response = await axiosInstance.get(`/packaging-config`, {
			headers: { Authorization: `Bearer ${token}` },
		})
		return response.data
	} catch (error) {
		console.error('Ошибка получения избранного:', error)
		return null
	}
}
export const postPackagingConfig = async (
	listingId: number,
	page: boolean,
	presentation: boolean,
	financial: boolean
) => {
	try {
		const token = localStorage.getItem('accessToken')
		if (!token) throw new Error('Требуется авторизация')

		const response = await axiosInstance.post(
			`/${listingId}/packaging-details`,
			{
				status: 'ACTIVE',
				pageDesignSelected: page,
				presentationSelected: presentation,
				financialModelSelected: financial,
			},
			{
				headers: { Authorization: `Bearer ${token}` },
			}
		)
		return response.data
	} catch (error) {
		console.error('Ошибка получения избранного:', error)
		return null
	}
}
