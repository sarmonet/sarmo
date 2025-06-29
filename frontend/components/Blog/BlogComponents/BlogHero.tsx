import { useTranslation } from 'next-i18next'
import { BlogHeroWrapper } from './BlogComponents.styled'
export const BlogHero = () => {
	const { t } = useTranslation('common')
	return (
		<BlogHeroWrapper>
			<span>{t('blog&News.capsuleBlog')}</span>
			<h1>{t('blog&News.titleBlog')}</h1>
			<p>{t('blog&News.subtitleBlog')}</p>
			{/* <BlogSearch content = {content}/> */}
		</BlogHeroWrapper>
	)
}
