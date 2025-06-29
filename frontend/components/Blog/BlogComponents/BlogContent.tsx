import { useCatalog } from '@/components/Catalog/CatalogContext/CatalogContext'
import { getBlogById, getBlogs } from '@/services/getContent'
import { useRouter } from 'next/router'
import { useCallback, useEffect, useState } from 'react'
import { IContentBlock } from '../ContentInterface/Content.interface'
import { BlogContentList } from './BlogComponents.styled'
import { BlogContentItem } from './BlogContentItem'
export const BlogContent = () => {
	const [content, setContent] = useState<IContentBlock[]>([])
	const { setBlog } = useCatalog()
	const router = useRouter()

	const fetchData = useCallback(async () => {
		const data = await getBlogs()
		setContent(data || [])
		return data
	}, [])

	const fetchBlogById = useCallback(
		async (id: number) => {
			const data = await getBlogById(id)
			setBlog(data || [])
			router.push(`/blog/${id}`)
			return data
		},
		[router, setBlog]
	)

	useEffect(() => {
		fetchData()
	}, [fetchData])

	return (
		<div>
			<BlogContentList>
				{content.map(item => (
					<li key={item.id}>
						<BlogContentItem
							onClick={() => {
								fetchBlogById(item.id)
							}}
							content={item}
						/>
					</li>
				))}
			</BlogContentList>
		</div>
	)
}
