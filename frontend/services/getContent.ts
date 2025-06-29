import { ICommentaries } from '@/components/Blog/BlogComentaries/Commentaries.interface'
import {
	IContent,
	IContentBlock,
} from '@/components/Blog/ContentInterface/Content.interface'
import { axiosInstanceContent } from '@/utils/axiosInstance'
export const getBlogs = async (): Promise<IContentBlock[] | null> => {
	try {
		const response = await axiosInstanceContent.get<IContentBlock[]>(
			'/article',
			{
				headers: {
					Authorization: `Bearer ${localStorage.getItem('accessToken')}`,
				},
			}
		)
		return response.data
	} catch (error) {
		console.error(`❌ Ошибка при получении статей:`, error)
		return null
	}
}

export const getBlogById = async (id: number) => {
	try {
		const response = await axiosInstanceContent.get(`/full/article/${id}`, {
			headers: {
				Authorization: `Bearer ${localStorage.getItem('accessToken')}`,
			},
		})
		return response.data
	} catch (error) {
		console.error(`❌ Ошибка при получении статьи:`, error)
		return null
	}
}
export const getNews = async (): Promise<IContentBlock[] | null> => {
	try {
		const response = await axiosInstanceContent.get<IContentBlock[]>('/news')
		return response.data
	} catch (error) {
		console.error(`❌ Ошибка при получении статей:`, error)
		return null
	}
}
export const getNewById = async (id: number) => {
	try {
		const response = await axiosInstanceContent.get(`/full/news/${id}`, {
			headers: {
				Authorization: `Bearer ${localStorage.getItem('accessToken')}`,
			},
		})

		return response.data
	} catch (error) {
		console.error(`❌ Ошибка при получении статьи:`, error)
		return null
	}
}

interface DelCommentParams {
	commentId: number
}

export interface PostCommentParams {
	articleId: number
	userId?: number
	text: string
	parentCommentId?: number
}

export const postBlog = async (data: IContent) => {
	try {
		const token = localStorage.getItem('accessToken')
		if (!token) {
			console.error('Токен доступа не найден')
			throw new Error('Authentication required')
		}

		const response = await axiosInstanceContent.post('/full/articles', data, {
			headers: {
				Authorization: `Bearer ${token}`,
				'Content-Type': 'application/json',
			},
		})

		return response.data
		// eslint-disable-next-line @typescript-eslint/no-explicit-any
	} catch (error: any) {
		if (error.response) {
			console.error(`❌ Ошибка сервера: ${error.response.status}`, {
				status: error.response.status,
				data: error.response.data,
				headers: error.response.headers,
			})

			if (error.response.status === 401) {
				console.error('Требуется авторизация')
			} else if (error.response.status === 500) {
				console.error('Внутренняя ошибка сервера')
			}
		} else if (error.request) {
			console.error('❌ Нет ответа от сервера:', error.request)
		} else {
			console.error('❌ Ошибка при настройке запроса:', error.message)
		}

		throw error
	}
}

export const updateBlog = async (dataId: number, data: IContent) => {
	try {
		const token = localStorage.getItem('accessToken')
		if (!token) {
			console.error('Токен доступа не найден')
			throw new Error('Authentication required')
		}

		const response = await axiosInstanceContent.put(
			`/full/articles/${dataId}`,
			data,
			{
				headers: {
					Authorization: `Bearer ${token}`,
					'Content-Type': 'application/json',
				},
			}
		)

		return response.data
		// eslint-disable-next-line @typescript-eslint/no-explicit-any
	} catch (error: any) {
		if (error.response) {
			console.error(
				`❌ Ошибка сервера при обновлении блога: ${error.response.status}`,
				{
					status: error.response.status,
					data: error.response.data,
					headers: error.response.headers,
				}
			)
			if (error.response.status === 401) {
				console.error('Требуется авторизация для обновления')
			} else if (error.response.status === 500) {
				console.error('Внутренняя ошибка сервера при обновлении')
			}
		} else if (error.request) {
			console.error(
				'❌ Нет ответа от сервера при обновлении блога:',
				error.request
			)
		} else {
			console.error(
				'❌ Ошибка при настройке запроса на обновление блога:',
				error.message
			)
		}
		throw error
	}
}
export const updateNew = async (dataId: number, data: IContent) => {
	try {
		const token = localStorage.getItem('accessToken')
		if (!token) {
			console.error('Токен доступа не найден')
			throw new Error('Authentication required')
		}

		const response = await axiosInstanceContent.put(
			`/full/news/${dataId}`,
			data,
			{
				headers: {
					Authorization: `Bearer ${token}`,
					'Content-Type': 'application/json',
				},
			}
		)

		return response.data
		// eslint-disable-next-line @typescript-eslint/no-explicit-any
	} catch (error: any) {
		if (error.response) {
			console.error(
				`❌ Ошибка сервера при обновлении блога: ${error.response.status}`,
				{
					status: error.response.status,
					data: error.response.data,
					headers: error.response.headers,
				}
			)
			if (error.response.status === 401) {
				console.error('Требуется авторизация для обновления')
			} else if (error.response.status === 500) {
				console.error('Внутренняя ошибка сервера при обновлении')
			}
		} else if (error.request) {
			console.error(
				'❌ Нет ответа от сервера при обновлении блога:',
				error.request
			)
		} else {
			console.error(
				'❌ Ошибка при настройке запроса на обновление блога:',
				error.message
			)
		}
		throw error
	}
}

export const postNews = async (data: IContent) => {
	try {
		const token = localStorage.getItem('accessToken')
		if (!token) {
			console.error('Токен доступа не найден')
			throw new Error('Authentication required')
		}

		const response = await axiosInstanceContent.post('/full/news', data, {
			headers: {
				Authorization: `Bearer ${token}`,
				'Content-Type': 'application/json',
			},
		})

		return response.data
		// eslint-disable-next-line @typescript-eslint/no-explicit-any
	} catch (error: any) {
		if (error.response) {
			console.error(`❌ Ошибка сервера: ${error.response.status}`, {
				status: error.response.status,
				data: error.response.data,
				headers: error.response.headers,
			})

			if (error.response.status === 401) {
				console.error('Требуется авторизация')
			} else if (error.response.status === 500) {
				console.error('Внутренняя ошибка сервера')
			}
		} else if (error.request) {
			console.error('❌ Нет ответа от сервера:', error.request)
		} else {
			console.error('❌ Ошибка при настройке запроса:', error.message)
		}

		throw error
	}
}

export const postComment = async (
	postCommentParams: PostCommentParams
): Promise<ICommentaries> => {
	try {
		const response = await axiosInstanceContent.post<ICommentaries>(
			`/article/comment`,
			{
				articleId: postCommentParams.articleId,
				text: postCommentParams.text,
				parentCommentId: postCommentParams.parentCommentId,
			},
			{
				headers: {
					Authorization: `Bearer ${localStorage.getItem('accessToken')}`,
				},
			}
		)

		return response.data
	} catch (error) {
		console.error('Ошибка при отправке комментария:', error)
		throw error
	}
}

interface GetCommentParams {
	articleId: number
}

export const getComment = async (
	getCommentParams: GetCommentParams
): Promise<ICommentaries[] | undefined> => {
	try {
		const response = await axiosInstanceContent.get<ICommentaries[]>(
			`/article/${getCommentParams.articleId}/comment`,
			{
				headers: {
					Authorization: `Bearer ${localStorage.getItem('accessToken')}`,
				},
			}
		)
		return response.data
		// eslint-disable-next-line @typescript-eslint/no-explicit-any
	} catch (error: any) {
		if (error.response?.status === 404) {
			return undefined
		}
		console.error('Ошибка при получении комментариев:', error)
		return undefined
	}
}
export const delComment = async (delCommentParams: DelCommentParams) => {
	try {
		const response = await axiosInstanceContent.delete(
			`/article/comment/${delCommentParams.commentId}`,
			{
				headers: {
					Authorization: `Bearer ${localStorage.getItem('accessToken')}`,
				},
			}
		)
		return response.data
	} catch (error) {
		console.error('Ошибка при получении комментариев:', error)
		return undefined
	}
}
export const delBlog = async (id: number) => {
	try {
		const response = await axiosInstanceContent.delete(`/article/${id}`, {
			headers: {
				Authorization: `Bearer ${localStorage.getItem('accessToken')}`,
			},
		})
		return response.data
	} catch (error) {
		console.error('Ошибка при получении комментариев:', error)
		return undefined
	}
}
export const delNew = async (id: number) => {
	try {
		const response = await axiosInstanceContent.delete(`/news/${id}`, {
			headers: {
				Authorization: `Bearer ${localStorage.getItem('accessToken')}`,
			},
		})
		return response.data
	} catch (error) {
		console.error('Ошибка при получении комментариев:', error)
		return undefined
	}
}
