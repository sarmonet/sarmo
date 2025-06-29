import {
	IContent,
	IContentBlock,
} from '@/components/Blog/ContentInterface/Content.interface'
import { MoreBtn } from '@/components/MoreBtn/MoreBtn'
import { delNew, getNewById, getNews } from '@/services/getContent'
import { colors } from '@/utils'
import { useTranslation } from 'next-i18next'
import Image from 'next/legacy/image'
import { useRouter } from 'next/router'
import { useCallback, useEffect, useState } from 'react'

import { FaRegTrashAlt } from 'react-icons/fa'
import { PiMagnifyingGlass } from 'react-icons/pi'
import {
	ListingButtons,
	ListingInfo,
	ListingItem,
	ListingItems,
	ListingMain,
} from '../../../AdministrationListings/AdministrationListings.styled'
import { EditNew } from '../EditContent/EditNew'

export const AdminNews = () => {
	const [news, setNews] = useState<IContentBlock[]>([])

	const [editingContent, setEditingContent] = useState<IContent | null>(null)
	const router = useRouter()
	const { t } = useTranslation('common')

	const fetchData = useCallback(async () => {
		const data = await getNews()
		setNews(data || [])
		return data
	}, [])

	useEffect(() => {
		fetchData()
	}, [fetchData])

	const handleDelete = async (id: number) => {
		try {
			setNews(prevNews => prevNews.filter(news => news.id !== id))
			await delNew(id)
		} catch (error) {
			console.error('Ошибка при удалении листинга:', error)
		}
	}

	const handleEdit = async (id: number) => {
		try {
			const responseData = await getNewById(id)
			if (responseData && responseData.news && responseData.content) {
				const processedBlogContent: IContent = {
					id: responseData.news.id,
					mainImage: responseData.news.mainImage || '',
					title: responseData.news.title || '',
					description: responseData.news.description || '',
					content: responseData.content.content || [],
				}
				setEditingContent(processedBlogContent)
			} else {
				console.error(
					'Не удалось получить полный контент блога для редактирования или структура данных некорректна.'
				)
			}
		} catch (error) {
			console.error('Ошибка при получении блога для редактирования:', error)
		}
	}

	const handleEditComplete = () => {
		setEditingContent(null)
		fetchData()
	}

	if (editingContent) {
		return (
			<EditNew
				blogContent={editingContent}
				onEditComplete={handleEditComplete}
			/>
		)
	}

	return (
		<div>
			{news.length > 0 && (
				<ListingItems>
					{news.map(newItem => (
						<ListingItem key={newItem.id}>
							<ListingMain>
								<Image
									src={newItem.mainImage || '/images/default-blog.jpg'}
									alt={newItem.title}
									width={200}
									height={200}
								/>
								<ListingInfo>
									<h2>{newItem.title}</h2>
									<div style={{ display: 'flex', alignItems: 'center' }}>
										<span
											style={{
												display: 'flex',
												alignItems: 'center',
												columnGap: '5px',
											}}
										>
											{newItem.description}
										</span>
									</div>
									<span>
										{' '}
										{new Date(newItem.publicationDate).toLocaleDateString()}
									</span>
								</ListingInfo>
							</ListingMain>
							<ListingButtons>
								<MoreBtn onClick={() => handleEdit(newItem.id)}>
									{t('administrationArticles.edit')}
								</MoreBtn>
								<MoreBtn
									color={colors.greyTextColor}
									bgcolor='transparent'
									onClick={() => {
										handleDelete(newItem.id)
									}}
								>
									{t('administrationArticles.delete')}{' '}
									<FaRegTrashAlt fill={colors.errorColor} />
								</MoreBtn>
								<MoreBtn onClick={() => router.push(`/new/${newItem.id}`)}>
									{t('listingAll.view')} <PiMagnifyingGlass />
								</MoreBtn>
							</ListingButtons>
						</ListingItem>
					))}
				</ListingItems>
			)}
		</div>
	)
}
