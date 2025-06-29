import { IContentItem } from '@/components/Blog/ContentInterface/Content.interface'
import { useCatalog } from '@/components/Catalog/CatalogContext/CatalogContext'
import { formatDate } from '@/components/Listing/ListingComponents/FormatDateComponent'
import { getBlogById } from '@/services/getContent'
import Image from 'next/legacy/image'
import { useEffect } from 'react'
import { BsFillCalendar2DateFill } from 'react-icons/bs'
import { FaEye } from 'react-icons/fa'
import { BlogCommentaries } from '../BlogComentaries/BlogComentaries'
import {
	BlogPageImageBlock,
	BlogPageMain,
	BlogPageTop,
	BlogPageWrapper,
} from './BlogComponents.styled'

export const BlogPage = () => {
	const { blog, setBlog } = useCatalog()
	const path = window.location.pathname

	const parts = path.split('/')

	const id = parts[parts.length - 1]

	useEffect(() => {
		const blogId = Number(id)
		const fetchBlog = async () => {
			try {
				const blogData = await getBlogById(blogId)
				if (blogData) {
					setBlog(blogData)
				} else {
					setBlog([])
				}
			} catch (error) {
				console.error('Ошибка при получении блога:', error)
				setBlog([])
			}
		}

		fetchBlog()
	}, [id, setBlog])
	if (!blog) {
		return <div>Блог не найден</div>
	}

	// eslint-disable-next-line @typescript-eslint/no-explicit-any
	const { article, content } = blog as any
	if (!article || !content?.content) {
		return <div>Контент отсутствует</div>
	}

	return (
		<BlogPageWrapper>
			<BlogPageTop>
				<h1>{article.title}</h1>
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
						<BsFillCalendar2DateFill /> {formatDate(article.publicationDate)}
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
						{article.viewCount}
					</span>
				</div>

				<p>{article.description}</p>
				<Image
					src={article.mainImage}
					alt='Main Image'
					width={877}
					height={466}
				/>
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
					) : // <div key={index} dangerouslySetInnerHTML={{ __html: DOMPurify.sanitize(item.value) }} />

					item.type === 'image' ? (
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
			<BlogCommentaries articleId={article.id} />
		</BlogPageWrapper>
	)
}
