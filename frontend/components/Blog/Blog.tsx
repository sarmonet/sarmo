import { useCheckAdmin } from '@/utils/useCheckAdmin'
import { BlogWrapper } from './Blog.styled'
import { BlogContent } from './BlogComponents/BlogContent'
import { BlogHero } from './BlogComponents/BlogHero'
export const Blog = () => {
	const isAdmin = useCheckAdmin()
	if (isAdmin === null) return null
	return (
		<BlogWrapper>
			<BlogHero />
			<BlogContent />
		</BlogWrapper>
	)
}
