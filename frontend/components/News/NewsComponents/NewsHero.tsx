import { useTranslation } from 'next-i18next'
import { NewsHeroWrapper } from './NewsComponents.styled'
export const BlogHero = () => {
	const { t } = useTranslation('common')
	return (
		<NewsHeroWrapper>
			<span>{t('blog&News.capsuleNews')}</span>
			<h1>{t('blog&News.titleNews')}</h1>
			<p>{t('blog&News.subtitleNews')}</p>
			{/* <BlogSearch content = {content}/> */}
		</NewsHeroWrapper>
	)
}
