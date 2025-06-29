import { colors } from '@/utils'
import { useTranslation } from 'next-i18next'
import { GetProfileSideData } from './ProfileSideBar.data'
import { ProfileSideBarItem, ProfileSideBarItems } from './ProfileSideBar.styled'
export interface Props {
	setActiveTab: (index: number) => void
	activeTab: number
}
export const ProfileSideBar = ({ setActiveTab, activeTab }: Props) => {
	const { t } = useTranslation('common')
	const ProfileSideData = GetProfileSideData(t)
	return(
		<ProfileSideBarItems>
			{ProfileSideData.map((item ,index) => {
				return(
						<ProfileSideBarItem key={index} onClick={() => setActiveTab(index)}
						style={activeTab === index ? { backgroundColor: `${colors.profileBgColor}` , color: `${colors.mainTextColor}` } : {}}>{item.title}</ProfileSideBarItem>
				)
			})}
		</ProfileSideBarItems>	
	)
}