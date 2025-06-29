import { ProfileSideBarItem, ProfileSideBarItems } from '@/components/Profile/ProfileSideBar/ProfileSideBar.styled'
import { colors } from '@/utils'
import { useTranslation } from 'next-i18next'
import { GetListingSideData } from './ListingSide.data'
export interface Props {
	setActiveTab: (index: number) => void
	activeTab: number
}
export const ListingSideBar = ({ setActiveTab, activeTab }: Props) => {
	const { t } = useTranslation('common')
	const ListingSideData = GetListingSideData(t)
	return(
		<ProfileSideBarItems>
			{ListingSideData.map((item ,index) => {
				return(
						<ProfileSideBarItem key={index} onClick={() => setActiveTab(index)}
						style={activeTab === index ? { backgroundColor: `${colors.profileBgColor}` , color: `${colors.mainTextColor}` } : {}}>{item.title}</ProfileSideBarItem>
				)
			})}
		</ProfileSideBarItems>	
	)
}