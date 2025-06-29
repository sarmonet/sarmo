import { ProfileContentWrapper } from '@/components/Profile/ProfileContent/ProfileContent.styled'
import { ProfileBlog } from './ContentSideTab/ProfileBlog'
import { ProfileNews } from './ContentSideTab/ProfileNews'
export interface Props {
	activeTab: number
}

export const ContentMain = ({ activeTab }: Props) => {
	
	return(
		<ProfileContentWrapper>
			{activeTab === 0 &&  <ProfileBlog />}
			{activeTab === 1 &&  <ProfileNews />}
		</ProfileContentWrapper>
	)


}