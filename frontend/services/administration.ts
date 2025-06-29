import {
	axiosInstance,
	axiosInstanceDefault,
	axiosInstanceLogin,
	axiosInstanceUsers,
} from '@/utils/axiosInstance'
import { ICreateListing } from './getListings'
export const getTransactionSupport = async () => {
	try {
		const { data } = await axiosInstanceUsers.get(`/transaction-support`, {
			headers: {
				Authorization: `Bearer ${localStorage.getItem('accessToken')}`,
			},
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
export const getPackagingDetails = async () => {
	try {
		const { data } = await axiosInstance.get(`/packaging-details/active`, {
			headers: {
				Authorization: `Bearer ${localStorage.getItem('accessToken')}`,
			},
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
export const getSubscriptionUsers = async () => {
	try {
		const { data } = await axiosInstanceDefault.get(`/subscription/users`)

		if (Array.isArray(data) && data.length > 0) {
			return data
		} else {
			console.warn('ℹ️ Данные пользователя отсутствуют в ответе сервера.')
			return []
		}
	} catch (error: any) {
		if (error.response?.status === 404) {
			console.warn('⚠️ Пользователи по подписке не найдены (404).')
			return []
		}

		console.error('❌ Ошибка при получении данных пользователя:', error)
		throw error
	}
}
export const getAllUsers = async () => {
	try {
		const { data } = await axiosInstanceLogin.get(`user`)

		if (Array.isArray(data) && data.length > 0) {
			return data
		} else {
			console.warn('ℹ️ Данные пользователя отсутствуют в ответе сервера.')
			return []
		}
	} catch (error: any) {
		if (error.response?.status === 404) {
			console.warn('⚠️ Пользователи по подписке не найдены (404).')
			return []
		}

		console.error('❌ Ошибка при получении данных пользователя:', error)
		throw error
	}
}
export const getAllListings = async (page: number) => {
	try {
		const { data } = await axiosInstanceDefault.get(
			`/listing?page=${page - 1}&size=12`
		)

		if (data?.content && Array.isArray(data.content)) {
			return {
				content: data.content,
				totalPages: data.totalPages ?? 1,
			}
		} else {
			console.warn('ℹ️ Некорректный формат данных.')
			return { content: [], totalPages: 1 }
		}
	} catch (error: any) {
		if (error.response?.status === 404) {
			console.warn('⚠️ Листинги не найдены (404).')
			return { content: [], totalPages: 1 }
		}

		console.error('❌ Ошибка при получении листинга:', error)
		throw error
	}
}

export const putNewUserRole = async ({
	id,
	name,
}: {
	id: number
	name: number
}) => {
	const { data } = await axiosInstanceLogin.put(
		`user/${id}`,
		{ roleId: name },
		{
			headers: {
				Authorization: `Bearer ${localStorage.getItem('accessToken')}`,
			},
		}
	)
	return data
}
export const putNewUserStatus = async ({
	id,
	userStatus,
}: {
	id: number
	userStatus: string
}) => {
	const { data } = await axiosInstanceUsers.put(
		`/me/${id}`,
		{ userStatus: userStatus },
		{
			headers: {
				Authorization: `Bearer ${localStorage.getItem('accessToken')}`,
			},
		}
	)
	return data
}
export const postListings = async (
	createParams: ICreateListing,
	userId: number
) => {
	try {
		const response = await axiosInstance.post(
			`/full/user/${userId}`,
			createParams,
			{
				headers: {
					Authorization: `Bearer ${localStorage.getItem('accessToken')}`,
				},
			}
		)
		return response.data
	} catch (error) {
		console.error('Ошибка создания объявления:', error)
		return null
	}
}
