import { BlogWrapper } from './News.styled'
import { BlogContent } from './NewsComponents/NewsContent'
import { BlogHero } from './NewsComponents/NewsHero'
export const News = () => {
	return (
		<BlogWrapper>
			<BlogHero/>
			<BlogContent/>
		</BlogWrapper>
	);
}