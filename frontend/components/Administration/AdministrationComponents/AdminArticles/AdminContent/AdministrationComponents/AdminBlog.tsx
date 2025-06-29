import {
	IContent,
	IContentBlock,
} from '@/components/Blog/ContentInterface/Content.interface'
import { MoreBtn } from '@/components/MoreBtn/MoreBtn'
import { delBlog, getBlogById, getBlogs } from '@/services/getContent'
import { colors } from '@/utils'
import { useTranslation } from 'next-i18next'
import Image from "next/legacy/image"
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
import { EditBlog } from '../EditContent/EditBlog'

export const AdminBlog = () => {
	const [blogs, setBlogs] = useState<IContentBlock[]>([])

	const [editingBlogContent, setEditingBlogContent] = useState<IContent | null>(
		null
	)
	const router = useRouter()
	const { t } = useTranslation('common')

	const fetchData = useCallback(async () => {
		const data = await getBlogs()
		setBlogs(data || [])
		return data
	}, [])

	useEffect(() => {
		fetchData()
	}, [fetchData])

	const handleDelete = async (id: number) => {
		try {
			setBlogs(prevBlogs => prevBlogs.filter(blog => blog.id !== id))
			await delBlog(id)
		} catch (error) {
			console.error('Ошибка при удалении листинга:', error)
		}
	}

	const handleEdit = async (id: number) => {
		try {
			const responseData = await getBlogById(id)
			if (responseData && responseData.article && responseData.content) {
				const processedBlogContent: IContent = {
					id: responseData.article.id,
					mainImage: responseData.article.mainImage || '',
					title: responseData.article.title || '',
					description: responseData.article.description || '',
					content: responseData.content.content || [],
				}
				setEditingBlogContent(processedBlogContent)
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
		setEditingBlogContent(null)
		fetchData()
	}

	if (editingBlogContent) {
		return (
			<EditBlog
				blogContent={editingBlogContent}
				onEditComplete={handleEditComplete}
			/>
		)
	}

	return (
		<div>
			{blogs.length > 0 && (
				<ListingItems>
					{blogs.map(blog => (
						<ListingItem key={blog.id}>
							<ListingMain>
								<Image
									src={blog.mainImage || '/images/default-blog.jpg'}
									alt={blog.title}
									width={200}
									height={200}
								/>
								<ListingInfo>
									<h2>{blog.title}</h2>
									<div style={{ display: 'flex', alignItems: 'center' }}>
										<span
											style={{
												display: 'flex',
												alignItems: 'center',
												columnGap: '5px',
											}}
										>
											{blog.description}
										</span>
									</div>
									<span>
										{' '}
										{new Date(blog.publicationDate).toLocaleDateString()}
									</span>
								</ListingInfo>
							</ListingMain>
							<ListingButtons>
								<MoreBtn onClick={() => handleEdit(blog.id)}>
									{t('administrationArticles.edit')}
								</MoreBtn>
								<MoreBtn
									color={colors.greyTextColor}
									bgcolor='transparent'
									onClick={() => {
										handleDelete(blog.id)
									}}
								>
									{t('administrationArticles.delete')}{' '}
									<FaRegTrashAlt fill={colors.errorColor} />
								</MoreBtn>
								<MoreBtn onClick={() => router.push(`/blog/${blog.id}`)}>
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
