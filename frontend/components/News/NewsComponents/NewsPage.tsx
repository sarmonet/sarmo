// components/News/NewsContent/NewsContent.tsx
import { IContentItem } from '@/components/Blog/ContentInterface/Content.interface'
import { useCatalog } from '@/components/Catalog/CatalogContext/CatalogContext'
import { formatDate } from '@/components/Listing/ListingComponents/FormatDateComponent'
import { getNewById } from '@/services/getContent'
import Image from 'next/legacy/image'
import { useEffect } from 'react'
import { BsFillCalendar2DateFill } from 'react-icons/bs'
import { FaEye } from 'react-icons/fa'

import {
	BlogPageImageBlock,
	BlogPageMain,
	BlogPageTop,
	BlogPageWrapper,
} from '@/components/Blog/BlogComponents/BlogComponents.styled'

export const NewsContent = () => {
	const { blog, setBlog } = useCatalog()
	const path = typeof window !== 'undefined' ? window.location.pathname : ''
	const parts = path.split('/')
	const id = parts[parts.length - 1]

	useEffect(() => {
		const newId = Number(id)
		if (isNaN(newId) || newId === 0) {
			console.warn('Некорректный ID новости:', id)
			setBlog([])
			return
		}

		const fetchNews = async () => {
			try {
				const newsData = await getNewById(newId)
				if (newsData) {
					setBlog(newsData)
				} else {
					setBlog([])
				}
			} catch (error) {
				console.error('Ошибка при получении новости:', error)
				setBlog([])
			}
		}

		fetchNews()
	}, [id, setBlog])

	if (blog === null) {
		return <div>Новость не найдена</div>
	}

	if (!blog || Object.keys(blog).length === 0) {
		return <div>Загрузка новости...</div>
	}

	// eslint-disable-next-line @typescript-eslint/no-explicit-any
	const { news, content } = blog as any

	if (!news || !content?.content) {
		return <div>Контент новости отсутствует</div>
	}

	return (
		<BlogPageWrapper>
			<BlogPageTop>
				<h1>{news.title}</h1>
				<div
					style={{ display: 'flex', alignItems: 'center', columnGap: '45px' }}
				>
					<span
						style={{
							display: 'flex',
							alignItems: 'center',
							columnGap: '5px',
							fontSize: '15px',
						}}
					>
						<BsFillCalendar2DateFill /> {formatDate(news.publicationDate)}
					</span>

					<span
						style={{
							display: 'flex',
							alignItems: 'center',
							columnGap: '5px',
							fontSize: '15px',
						}}
					>
						<FaEye />
						{news.viewCount || 0}{' '}
					</span>
				</div>
				<p>{news.description}</p>{' '}
				<Image src={news.mainImage} alt='Main Image' width={877} height={466} />
			</BlogPageTop>
			<BlogPageMain>
				{content.content.map((item: IContentItem, index: number) =>
					item.type === 'text' ? (
						<>
							{item.title && (
								<h2 className='text-[21px] tracking-[1px] font-[600]'>
									{item.title}
								</h2>
							)}
							<div
								className='mb-[25px] mt-[25px]'
								key={index}
								dangerouslySetInnerHTML={{ __html: item.value }}
							/>
						</>
					) : item.type === 'image' ? (
						<BlogPageImageBlock key={index}>
							<Image
								src={item.value}
								alt='Blog Image'
								width={800}
								height={276}
							/>
							{item.title && <p>{item.title}</p>}
						</BlogPageImageBlock>
					) : null
				)}
			</BlogPageMain>

			{/* {news.id && <BlogCommentaries articleId={news.id} />} */}
		</BlogPageWrapper>
	)
}
